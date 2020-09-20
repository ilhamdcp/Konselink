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
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.Document
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.FormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.FragmentRegistrationSecondPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.TextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.RegistrationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.RegistrationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.RegistrationViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SecondPageFragment : Fragment() {
    private var currentPhotoFlag: String = ""
    private var addSipPhoto: ImageView? = null
    private var addSspPhoto: ImageView? = null
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
        val binding = DataBindingUtil.inflate<FragmentRegistrationSecondPageBinding>(
            inflater,
            R.layout.fragment_registration_second_page,
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
        assignElements()
        initializeViewModel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == BuildConfig.CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                takePhoto(currentPhotoFlag)
            } else {
                Toast.makeText(requireContext(), "Izin ditolak", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            val imgFile = File(currentPhotoPath)

            if (imgFile.exists()) {
                when {
                    currentPhotoPath.contains(Document.SIP.code) -> {
                        val bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                        this.addSipPhoto?.setImageBitmap(bitmap)
                    }

                    currentPhotoPath.contains(Document.SSP.code) -> {
                        val bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                        this.addSspPhoto?.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun takePhoto(
        documentCode:
        String
    ) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile(documentCode)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    Toast.makeText(requireContext(), "Gagal mengambil foto", Toast.LENGTH_SHORT)
                        .show()
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
                    startActivityForResult(takePictureIntent, BuildConfig.CAMERA_REQUEST)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(documentCode: String): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "${documentCode}_${timeStamp}", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            when(documentCode) {
                Document.SIP.code -> {
                    registrationViewModel.handleSipPhotoPath(currentPhotoPath)
                }

                Document.SSP.code -> {
                    registrationViewModel.handleSspPhotoPath(currentPhotoPath)
                }
            }
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
        val previousButton = view?.findViewById<ImageView>(R.id.button_fragment_second_previous)
        previousButton?.setOnClickListener {
            registrationViewModel.saveCurrentRegistration()
            val firstPageFragment = FirstPageFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, firstPageFragment)
                .commit()
        }

        val nextButton = view?.findViewById<ImageView>(R.id.button_fragment_second_next)
        nextButton?.setOnClickListener {
            if (registrationViewModel.secondPageIsValid()) {
                registrationViewModel.saveCurrentRegistration()
                val finalPageFragment = FinalPageFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, finalPageFragment)
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

        val sipNumberForm = view?.findViewById<EditText>(R.id.form_sip_number)
        sipNumberForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SIP_NUMBER.typeName
            )
        )

        val sspNumberForm = view?.findViewById<EditText>(R.id.form_ssp_number)
        sspNumberForm?.addTextChangedListener(
            TextFormListener(
                registrationViewModel,
                FormType.SSP_NUMBER.typeName
            )
        )

        addSipPhoto = view?.findViewById<ImageView>(R.id.button_add_sip_photo)
        addSipPhoto?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                currentPhotoFlag = Document.SIP.code
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    BuildConfig.CAMERA_PERMISSION
                )

            } else {
                takePhoto(Document.SIP.code)
            }
        }

        addSspPhoto = view?.findViewById<ImageView>(R.id.button_add_ssp_photo)
        addSspPhoto?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                currentPhotoFlag = Document.SSP.code
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    CAMERA_PERMISSION
                )

            } else {
                takePhoto(Document.SSP.code)
            }
        }
    }
}