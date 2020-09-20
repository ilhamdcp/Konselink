package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.registration

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.CAMERA_PERMISSION
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.CAMERA_REQUEST
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.Document
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.FormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.Specialization
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.FragmentRegistrationFirstPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.SpinnerFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.TextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.RegistrationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.RegistrationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.RegistrationViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class FirstPageFragment() : Fragment() {
    private var addStrPhoto: ImageView? = null
    private var imageUri: Uri? = null
    private var currentPhotoPath: String = ""

    private val registrationViewModel by lazy {
        ViewModelProvider(
            this,
            RegistrationViewModelFactory(
                requireContext(),
                RegistrationService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(RegistrationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentRegistrationFirstPageBinding>(
            inflater,
            R.layout.fragment_registration_first_page,
            container,
            false
        )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = registrationViewModel    // Attach your view model here
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateSpinner()
        assignElements()
        initializeViewModel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Toast.makeText(requireContext(), "Izin ditolak", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val imgFile = File(currentPhotoPath)

            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                addStrPhoto?.setImageBitmap(bitmap)
            }
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    Toast.makeText(requireContext(), "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureIntent.putExtra("return-data", true)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "${Document.STR.code}_${timeStamp}", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            registrationViewModel.handleStrPhotoPath(currentPhotoPath)
        }
    }

    private fun initializeViewModel() {
        registrationViewModel.registrationLiveData.observe(viewLifecycleOwner) {
            it?.apply {
                registrationViewModel.mutableLiveData.value = it
            }
        }
    }

    fun assignElements() {
        val nextButton = view?.findViewById<ImageView>(R.id.button_fragment_first_next)
        nextButton?.setOnClickListener {
            if (registrationViewModel.firstPageIsValid()) {
                registrationViewModel.saveCurrentRegistration()
                val secondPageFragment = SecondPageFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, secondPageFragment)
                    .commit()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Mohon isi data secara lengkap",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        val fullnameForm = view?.findViewById<EditText>(R.id.form_full_name)
        fullnameForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.FULLNAME.typeName
            )
        )

        val strNumberForm = view?.findViewById<EditText>(R.id.form_str_number)
        strNumberForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.STR_NUMBER.typeName
            )
        )

        val specializationSpinner = view?.findViewById<Spinner>(R.id.spinner_specialization)
        specializationSpinner?.onItemSelectedListener =
            SpinnerFormListener(registrationViewModel, FormType.SPECIALIZATION.typeName)

        addStrPhoto = view?.findViewById<ImageView>(R.id.button_add_str_photo)
        addStrPhoto?.setOnClickListener {
            if (checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    CAMERA_PERMISSION
                )

            } else {
                takePhoto()
                if (currentPhotoPath.isNotEmpty()) {
                    registrationViewModel.handleStrPhotoPath(currentPhotoPath)
                }
            }
        }
    }

    fun populateSpinner() {
        // spinner is a special case for data binding where it requires much effort to implement
        // especially to retain previous selected value

        val specializationSpinner = view?.findViewById<Spinner>(R.id.spinner_specialization)
        val specializationSpinnerAdapter = ArrayAdapter<Specialization>(
            requireContext(),
            R.layout.item_spinner,
            R.id.text_spinner,
            Specialization.values()
        )
        specializationSpinner?.adapter = specializationSpinnerAdapter
    }
}