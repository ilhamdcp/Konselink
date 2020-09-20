package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.theartofdev.edmodo.cropper.CropImage
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ActivityEditProfileBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.UserService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ProfileViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ProfileViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class EditProfileActivity: AppCompatActivity() {
    val profileViewModel by lazy {
        ViewModelProvider(
            this, ProfileViewModelFactory(
                this,
                UserService.create(),
                ApplicationDatabase.getInstance(this)!!
            )
        ).get(ProfileViewModel::class.java)
    }

    private var displayPictureImageView: ImageView? = null
    private var imageUri: Uri? = null
    private var currentPhotoPath: String? = null
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityEditProfileBinding>(this, R.layout.activity_edit_profile).apply {
            vm = profileViewModel
            lifecycleOwner = this@EditProfileActivity
        }

        sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)

        initializeViewModel()
        assignElements()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST && data != null && data.data != null) {
            val selectedImageUri = data.data
            imageUri = FileProvider.getUriForFile(
                this,
                "id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.fileprovider",
                createImageFile()
            )
            CropImage.activity(selectedImageUri).setOutputUri(imageUri).start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val imageFile = File(currentPhotoPath!!)
                if (imageFile.exists()) {
                    Glide.with(this)
                        .setDefaultRequestOptions(
                            RequestOptions()
                                .circleCrop()
                        )
                        .load(imageFile)
                        .placeholder(R.drawable.user_placeholder)
                        .into(displayPictureImageView!!)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CHANGE_PROFILE_PICTURE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent , GALLERY_REQUEST )
            } else {
                Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "dp_${timeStamp}", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun initializeViewModel() {
        profileViewModel.updateProfileStatusCode.observe(this) {
            if (it > 0 && it == 200) {
                Toast.makeText(this, "Sukses mengedit profil", Toast.LENGTH_SHORT).show()
                finishAndRemoveTask()

            } else {
                Toast.makeText(this, "Gagal mengedit profil", Toast.LENGTH_SHORT).show()
            }
        }

        profileViewModel.userDataLiveData.observe(this) {
            profileViewModel.updatedUserLiveData.value = it
        }
        profileViewModel.getUserData(sharedPref.getString(TOKEN, "")!!)
    }

    private fun assignElements() {
        displayPictureImageView = findViewById(R.id.display_picture_thumbnail)
        displayPictureImageView?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                        CHANGE_PROFILE_PICTURE_PERMISSION
                    )
                }
            } else {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent , GALLERY_REQUEST )
            }
        }

        val saveProfileButton = findViewById<Button>(R.id.button_save_profile)
        saveProfileButton.setOnClickListener {
            profileViewModel.updateProfile(sharedPref.getString(TOKEN, "")!!, currentPhotoPath)
        }
    }
}