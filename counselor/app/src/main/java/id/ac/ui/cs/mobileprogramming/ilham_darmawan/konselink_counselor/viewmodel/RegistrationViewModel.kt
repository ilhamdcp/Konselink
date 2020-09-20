package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.gson.Gson
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.Gender
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.RegistrationError
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.Registration
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository.RegistrationRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.Specialization
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.RegistrationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.uploadFileToS3
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegistrationViewModel(
    val application: Context,
    registrationService: RegistrationService,
    db: ApplicationDatabase
) : ViewModel() {
    val registrationRepository = RegistrationRepository(registrationService, db)
    val postRegistrationResponseCode: LiveData<Int> = registrationRepository.getRegistrationResult()
    var mutableLiveData = MutableLiveData<Registration>(Registration())
    var registrationLiveData = registrationRepository.getRegistrationData()
    val _dataConfirmed = MutableLiveData<Boolean>(false)
    val dataConfimed: LiveData<Boolean> = _dataConfirmed
    val disabledDrawable = ContextCompat.getDrawable(application, R.drawable.button_disabled)
    val enabledDrawable = ContextCompat.getDrawable(application, R.drawable.button_blue_gradient)


    // first page
    val errFullName = ObservableField<String>()
    val errGender = ObservableField<String>()
    val errSpecialization = ObservableField<String>()
    val errStrNumber = ObservableField<String>()
    val errStrPhotoPath = ObservableField<String>()

    // second page
    val errSipNumber = ObservableField<String>()
    val errSipPhotoPath = ObservableField<String>()
    val errSspNumber = ObservableField<String>()
    val errSspPhotoPath = ObservableField<String>()


    fun submitRegistration(userId: Int) {
        val gson = Gson()
        val jsonResult = gson.toJson(mutableLiveData.value)
        Log.d("resulto", jsonResult)

        val filePhotoPaths  = arrayOf(mutableLiveData.value?.strPhotoPath,
            mutableLiveData.value?.sipPhotoPath,
            mutableLiveData.value?.sspPhotoPath)
            .filter { it != null && it.isNotBlank() }

        filePhotoPaths.forEach {
            Thread {
                uploadFileToS3(application, it!!, userId, true)
            }.start()
        }
    }

    fun postRegistrationData(token: String) {
        registrationRepository.postRegistrationData(mutableLiveData.value!!, token)
    }

    fun firstPageIsValid(): Boolean {
        Log.d("user", mutableLiveData.value?.toString())
        Log.d("PHOTOPATH", mutableLiveData.value?.strPhotoPath.toString())
        var result = mutableLiveData.value?.specialization != null && mutableLiveData.value?.specialization!!.isNotBlank() &&
                mutableLiveData.value?.fullname != null && mutableLiveData.value?.fullname!!.isNotBlank() &&
                mutableLiveData.value?.gender != null && mutableLiveData.value?.gender!!.isNotBlank() &&
                errFullName.get().isNullOrBlank() &&
                errGender.get().isNullOrBlank() &&
                errSpecialization.get().isNullOrBlank()

        if (mutableLiveData.value?.specialization.equals(Specialization.CLINICAL.specializationName)) {
            result = result.and(mutableLiveData.value?.strNumber != null && mutableLiveData.value?.strNumber!!.isNotBlank() &&
                    mutableLiveData.value?.strPhotoPath != null && mutableLiveData.value?.strPhotoPath!!.isNotBlank() &&
                    errStrNumber.get().isNullOrBlank() &&
                    errStrPhotoPath.get().isNullOrBlank()

            )
        }

        return result
    }

    fun secondPageIsValid(): Boolean {
        Log.d("user", mutableLiveData.value?.toString()!!)
        return mutableLiveData.value?.sipNumber != null && mutableLiveData.value?.sipNumber!!.isNotBlank() &&
                mutableLiveData.value?.sspNumber != null && mutableLiveData.value?.sspNumber!!.isNotBlank() &&
                mutableLiveData.value?.sipPhotoPath != null && mutableLiveData.value?.sipPhotoPath!!.isNotBlank() &&
                mutableLiveData.value?.sspPhotoPath != null && mutableLiveData.value?.sipPhotoPath!!.isNotBlank() &&
                errSipNumber.get().isNullOrBlank() &&
                errSspNumber.get().isNullOrBlank() &&
                errSipPhotoPath.get().isNullOrBlank() &&
                errSspPhotoPath.get().isNullOrBlank()
    }

    fun saveCurrentRegistration() {
        registrationRepository.insertRegistration(mutableLiveData)
    }

    fun handleGenderRadioButton(genderCode: String) {
        val genderFound = Gender.values().filter {
            it.code.equals(genderCode)
        }

        if (genderFound.isNotEmpty()) {
            mutableLiveData.value?.gender = genderCode
            errGender.set("")
        } else {
            errGender.set(RegistrationError.GENDER_NOT_CHOSEN.errName)
        }
        errGender.notifyChange()
    }

    fun handleFullNameEditText(fullName: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(fullName)
        val containsSpecialChar = matcher.find()
        when {
            fullName.isBlank() -> {
                errFullName.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errFullName.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                Log.d("DETECTED", "3")
                errFullName.set("")
                mutableLiveData.value?.fullname = fullName

            }
        }
        errFullName.notifyChange()
    }

    fun handleSpecializationSpinner(specialization: String?, position: Int) {
        if (specialization != null && specialization.isNotBlank()) {
            mutableLiveData.value?.specialization = specialization
            mutableLiveData.value?.specializationSpinnerId = position
            errSpecialization.set("")
        } else {
            errSpecialization.set(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName)
        }
        errSpecialization.notifyChange()
    }

    fun handleStrNumber(strNumber: String?) {
        if (strNumber.isNullOrBlank()) {
            errStrNumber.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.strNumber = strNumber
            errStrNumber.set("")
        }
        errStrNumber.notifyChange()
    }

    fun handleSipNumber(sipNumber: String?) {
        if (sipNumber.isNullOrBlank()) {
            errSipNumber.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.sipNumber = sipNumber
            errSipNumber.set("")
        }
        errSipNumber.notifyChange()
    }

    fun handleSspNumber(sspNumber: String?) {
        if (sspNumber.isNullOrBlank()) {
            errSspNumber.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.sspNumber = sspNumber
            errSspNumber.set("")
        }
        errSspNumber.notifyChange()
    }

    fun handleStrPhotoPath(photoPath: String?) {
        if (photoPath.isNullOrBlank()) {
            errStrPhotoPath.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.strPhotoPath = photoPath
            errStrPhotoPath.set("")
        }
        errStrPhotoPath.notifyChange()
    }

    fun handleSipPhotoPath(photoPath: String?) {
        if (photoPath.isNullOrBlank()) {
            errSipPhotoPath.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.sipPhotoPath = photoPath
            errSipPhotoPath.set("")
        }
        errSipPhotoPath.notifyChange()
    }

    fun handleSspPhotoPath(photoPath: String?) {
        if (photoPath.isNullOrBlank()) {
            errSspPhotoPath.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.sspPhotoPath = photoPath
            errSspPhotoPath.set("")
        }
        errSspPhotoPath.notifyChange()
    }
}

