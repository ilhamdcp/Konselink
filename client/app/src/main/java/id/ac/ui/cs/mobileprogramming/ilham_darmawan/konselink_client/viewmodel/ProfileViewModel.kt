package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.RegistrationError
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.UpdateProfileRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.UserData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository.UserRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.UserService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.uploadFileToS3
import java.util.regex.Matcher
import java.util.regex.Pattern

open class ProfileViewModel(private val context: Context, userService: UserService, db: ApplicationDatabase): ViewModel() {
    private val userRepository = UserRepository(context, userService, db)
    val userDataLiveData = userRepository.userLiveData
    var updatedUserLiveData = MutableLiveData<UserData>(UserData())
    val updateProfileStatusCode = userRepository.editProfileStatusCode
    val tokenLiveData = userRepository.tokenLiveData

    val errNickname = ObservableField<String>()

    fun loginViaSso(cookie: String) {
        userRepository.loginViaSso(cookie)
    }

    fun getUserData(token: String) {
        userRepository.retrieveUserData(token)
    }

    fun profileIsValid(): Boolean {
        return !updatedUserLiveData.value?.nickname.isNullOrBlank() && errNickname.get().isNullOrBlank()
    }

    fun handleNicknameEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errNickname.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errNickname.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                val tempUser = updatedUserLiveData.value
                tempUser?.nickname = name
                updatedUserLiveData.value = tempUser
                errNickname.set("")

            }
        }
        errNickname.notifyChange()
    }

    fun updateProfile(token: String, currentPhotoPath: String?) {
        if (currentPhotoPath != null && currentPhotoPath.isNotBlank()) {
            uploadFileToS3(context, currentPhotoPath, userDataLiveData.value?.userId!!, false)
        }
        val updateProfileRequest: UpdateProfileRequest = if (currentPhotoPath != null && currentPhotoPath.isNotBlank()) {
            UpdateProfileRequest(hasDisplayPicture = true, nickname = updatedUserLiveData.value?.nickname!!)
        } else {
            UpdateProfileRequest(nickname = updatedUserLiveData.value?.nickname!!)
        }
        userRepository.updateProfileData(token, updateProfileRequest)
    }
}