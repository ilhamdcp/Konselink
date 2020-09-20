package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.UpdateProfileRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.UserData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository.UserRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.UserService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.uploadFileToS3

open class ProfileViewModel(private val context: Context, userService: UserService, db: ApplicationDatabase): ViewModel() {
    private val userRepository = UserRepository(context, userService, db)
    var userDataLiveData: LiveData<UserData> = userRepository.userLiveData
    var updatedUserLiveData = MutableLiveData<UserData>(UserData())
    val updateProfileStatusCode = userRepository.editProfileStatusCode
    val tokenLiveData = userRepository.tokenLiveData

    fun loginViaSso(cookie: String) {
        userRepository.loginViaSso(cookie)
    }

    fun getUserData(token: String) {
        userRepository.retrieveUserData(token)
    }

    fun updateProfile(token: String, currentPhotoPath: String?) {
        if (currentPhotoPath != null && currentPhotoPath.isNotBlank()) {
            uploadFileToS3(context, currentPhotoPath, userDataLiveData.value?.userId!!, false)
        }

        if (currentPhotoPath != null && currentPhotoPath.isNotBlank()) {
            val updateProfileRequest = UpdateProfileRequest(hasDisplayPicture = true)
            userRepository.updateProfileData(token, updateProfileRequest)
        }
    }
}