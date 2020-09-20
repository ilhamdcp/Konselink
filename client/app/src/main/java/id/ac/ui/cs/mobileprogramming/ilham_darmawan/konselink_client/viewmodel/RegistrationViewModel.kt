package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.gson.Gson
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Registration
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository.RegistrationRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.IpipAndSrqRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.OptionSurvey
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RegistrationViewModel(
    context: Context,
    registrationService: RegistrationService,
    db: ApplicationDatabase
) : ViewModel() {
    val registrationRepository = RegistrationRepository(context, registrationService, db)
    val postRegistrationResponseCode: LiveData<Int> = registrationRepository.postRegistrationCode
    var mutableLiveData = MutableLiveData<Registration>(Registration())
    var registrationLiveData = registrationRepository.getRegistrationData()
    var _agreeToStatement = MutableLiveData<Boolean>(false)
    val postIpipAndSrqStatusCode = registrationRepository.postIpipAndSrqStatusCode
    val agreeToStatement: LiveData<Boolean> = _agreeToStatement
    val disabledDrawable = ContextCompat.getDrawable(context, R.drawable.button_disabled)
    val enabledDrawable = ContextCompat.getDrawable(context, R.drawable.button_blue_gradient)


    // first page
    val errName = ObservableField<String>()
    val errGender = ObservableField<String>()
    val errBirthPlace = ObservableField<String>()
    val errBirthDate = ObservableField<String>()
    val errAddress = ObservableField<String>()
    val errPhoneNumber = ObservableField<String>()
    val errReligion = ObservableField<String>()
    val errCurrentEducation = ObservableField<String>()

    // second page
    val errKindergartenAddress = ObservableField<String>()
    val errElementaryAddress = ObservableField<String>()
    val errJuniorAddress = ObservableField<String>()
    val errSeniorAddress = ObservableField<String>()
    val errCollegeAddress = ObservableField<String>()

    // third page
    val errFatherName = ObservableField<String>()
    val errFatherAge = ObservableField<String>()
    val errFatherReligion = ObservableField<String>()
    val errFatherTribe = ObservableField<String>()
    val errFatherEducation = ObservableField<String>()
    val errFatherOccupation = ObservableField<String>()
    val errFatherAddress = ObservableField<String>()
    val errMotherName = ObservableField<String>()
    val errMotherAge = ObservableField<String>()
    val errMotherReligion = ObservableField<String>()
    val errMotherTribe = ObservableField<String>()
    val errMotherEducation = ObservableField<String>()
    val errMotherOccupation = ObservableField<String>()
    val errMotherAddress = ObservableField<String>()

    // fourth page
    val errSibling1Name = ObservableField<String>()
    val errSibling1Gender = ObservableField<String>()
    val errSibling1Age = ObservableField<String>()
    val errSibling1Education = ObservableField<String>()

    val errSibling2Name = ObservableField<String>()
    val errSibling2Gender = ObservableField<String>()
    val errSibling2Age = ObservableField<String>()
    val errSibling2Education = ObservableField<String>()

    val errSibling3Name = ObservableField<String>()
    val errSibling3Gender = ObservableField<String>()
    val errSibling3Age = ObservableField<String>()
    val errSibling3Education = ObservableField<String>()

    val errSibling4Name = ObservableField<String>()
    val errSibling4Gender = ObservableField<String>()
    val errSibling4Age = ObservableField<String>()
    val errSibling4Education = ObservableField<String>()

    val errSibling5Name = ObservableField<String>()
    val errSibling5Gender = ObservableField<String>()
    val errSibling5Age = ObservableField<String>()
    val errSibling5Education = ObservableField<String>()

    // fifth page
    val errProblem = ObservableField<String>()
    val errEffortDone = ObservableField<String>()
    val errHasConsultedBefore = ObservableField<String>()
    val errDateConsulted = ObservableField<String>()
    val errPlaceConsulted = ObservableField<String>()
    val errComplaint = ObservableField<String>()
    val errSolution = ObservableField<String>()

    private val ipipValue: HashMap<String, MutableLiveData<Int>> = hashMapOf(
        IpipQuestion.QUESTION_1.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_2.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_3.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_4.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_5.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_6.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_7.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_8.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_9.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_10.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_11.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_12.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_13.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_14.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_15.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_16.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_17.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_18.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_19.question to MutableLiveData(-1),
        IpipQuestion.QUESTION_20.question to MutableLiveData(-1)
    )

    private val srqValue: HashMap<String, MutableLiveData<Int>> = hashMapOf(
        SrqQuestion.QUESTION_1.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_2.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_3.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_4.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_5.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_6.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_7.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_8.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_9.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_10.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_11.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_12.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_13.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_14.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_15.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_16.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_17.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_18.question to MutableLiveData(-1),
        SrqQuestion.QUESTION_19.question to MutableLiveData(-1)
    )

    fun parseJson() {
        val gson = Gson()
        val jsonResult = gson.toJson(mutableLiveData.value)
    }

    fun postRegistrationData(token: String?) {
        registrationRepository.postRegistrationData(mutableLiveData.value!!, token!!)
    }

    fun postIpipAndSrqData(token: String) {
        val ipip: ArrayList<OptionSurvey> = ArrayList()
        val srq: ArrayList<OptionSurvey> = ArrayList()
        ipipValue.forEach {
            ipip.add(OptionSurvey(answerKey = it.key, answerValue = it.value.value))
        }

        srqValue.forEach {
            srq.add(OptionSurvey(answerKey = it.key, answerValue = it.value.value))
        }

        registrationRepository.postIpipAndSrqData(token, IpipAndSrqRequest(srq, ipip))
    }

    fun firstPageIsValid(): Boolean {
        return mutableLiveData.value?.name != null && mutableLiveData.value?.name!!.isNotEmpty() &&
                mutableLiveData.value?.gender != null && mutableLiveData.value?.gender!!.isNotEmpty() &&
                mutableLiveData.value?.birthPlace != null && mutableLiveData.value?.birthPlace!!.isNotEmpty() &&
                mutableLiveData.value?.birthDay != null && mutableLiveData.value?.birthDay!! > 0 &&
                mutableLiveData.value?.birthMonth != null && mutableLiveData.value?.birthMonth!! > 0 &&
                mutableLiveData.value?.birthYear != null && mutableLiveData.value?.birthYear!! > 0 &&
                mutableLiveData.value?.address != null && mutableLiveData.value?.address!!.isNotEmpty() &&
                mutableLiveData.value?.phoneNumber != null && mutableLiveData.value?.phoneNumber!!.isNotEmpty() &&
                mutableLiveData.value?.religion != null && mutableLiveData.value?.religion!!.isNotEmpty() &&
                mutableLiveData.value?.currentEducation != null && mutableLiveData.value?.currentEducation!!.isNotEmpty() &&
                errName.get().isNullOrEmpty() &&
                errGender.get().isNullOrEmpty() &&
                errBirthDate.get().isNullOrEmpty() &&
                errAddress.get().isNullOrEmpty() &&
                errPhoneNumber.get().isNullOrEmpty() &&
                errReligion.get().isNullOrEmpty() &&
                errCurrentEducation.get().isNullOrEmpty()
    }

    fun secondPageIsValid(): Boolean {
        var boolean = true
        val currentEducationDegree = Education.values()
            .filter { it.educationType == mutableLiveData.value?.currentEducation }.first()
        if (currentEducationDegree.level >= Education.KINDERGARTEN.level) {
            boolean = boolean.and(
                mutableLiveData.value?.kindergartenData != null &&
                        mutableLiveData.value?.kindergartenData?.isNotBlank()!! &&
                        errKindergartenAddress.get().isNullOrEmpty()
            )
        }

        if (currentEducationDegree.level >= Education.ELEMENTARY.level) {
            boolean = boolean.and(
                mutableLiveData.value?.elementaryData != null &&
                        mutableLiveData.value?.elementaryData?.isNotBlank()!! &&
                        errElementaryAddress.get().isNullOrEmpty()
            )
        }

        if (currentEducationDegree.level >= Education.JUNIOR.level) {
            boolean = boolean.and(
                mutableLiveData.value?.juniorData != null &&
                        mutableLiveData.value?.juniorData?.isNotBlank()!! &&
                        errJuniorAddress.get().isNullOrEmpty()
            )
        }

        if (currentEducationDegree.level >= Education.SENIOR.level) {
            boolean = boolean.and(
                mutableLiveData.value?.seniorData != null &&
                        mutableLiveData.value?.seniorData?.isNotBlank()!! &&
                        errSeniorAddress.get().isNullOrEmpty()
            )
        }

        if (currentEducationDegree.level >= Education.DIPLOMA.level) {
            boolean = boolean.and(
                mutableLiveData.value?.collegeData != null &&
                        mutableLiveData.value?.collegeData?.isNotBlank()!! &&
                        errCollegeAddress.get().isNullOrEmpty()
            )
        }

        return boolean
    }

    fun thirdPageIsValid(): Boolean {
        return mutableLiveData.value?.fatherName != null && mutableLiveData.value?.fatherName!!.isNotEmpty() &&
                mutableLiveData.value?.fatherAge != null && mutableLiveData.value?.fatherAge!! > 0 &&
                mutableLiveData.value?.fatherReligion != null && mutableLiveData.value?.fatherReligion!!.isNotEmpty() &&
                mutableLiveData.value?.fatherTribe != null && mutableLiveData.value?.fatherTribe!!.isNotEmpty() &&
                mutableLiveData.value?.fatherEducation != null && mutableLiveData.value?.fatherEducation!!.isNotEmpty() &&
                mutableLiveData.value?.fatherOccupation != null && mutableLiveData.value?.fatherOccupation!!.isNotEmpty() &&
                mutableLiveData.value?.fatherAddress != null && mutableLiveData.value?.fatherAddress!!.isNotEmpty() &&
                mutableLiveData.value?.motherName != null && mutableLiveData.value?.motherName!!.isNotEmpty() &&
                mutableLiveData.value?.motherAge != null && mutableLiveData.value?.motherAge!! > 0 &&
                mutableLiveData.value?.motherReligion != null && mutableLiveData.value?.motherReligion!!.isNotEmpty() &&
                mutableLiveData.value?.motherTribe != null && mutableLiveData.value?.motherTribe!!.isNotEmpty() &&
                mutableLiveData.value?.motherEducation != null && mutableLiveData.value?.motherEducation!!.isNotEmpty() &&
                mutableLiveData.value?.motherOccupation != null && mutableLiveData.value?.motherOccupation!!.isNotEmpty() &&
                mutableLiveData.value?.motherAddress != null && mutableLiveData.value?.fatherAddress!!.isNotEmpty() &&
                errFatherName.get().isNullOrEmpty() &&
                errFatherAge.get().isNullOrEmpty() &&
                errFatherReligion.get().isNullOrEmpty() &&
                errFatherTribe.get().isNullOrEmpty() &&
                errFatherEducation.get().isNullOrEmpty() &&
                errFatherOccupation.get().isNullOrEmpty() &&
                errFatherAddress.get().isNullOrEmpty() &&
                errMotherName.get().isNullOrEmpty() &&
                errMotherAge.get().isNullOrEmpty() &&
                errMotherReligion.get().isNullOrEmpty() &&
                errMotherTribe.get().isNullOrEmpty() &&
                errMotherEducation.get().isNullOrEmpty() &&
                errMotherOccupation.get().isNullOrEmpty() &&
                errMotherAddress.get().isNullOrEmpty()
    }

    fun fourthPageIsValid(): Boolean {
        return mutableLiveData.value?.sibling1Name != null && mutableLiveData.value?.sibling1Name!!.isNotBlank() &&
                mutableLiveData.value?.sibling1Age != null && mutableLiveData.value?.sibling1Age!! > 0 &&
                mutableLiveData.value?.sibling1Education != null && mutableLiveData.value?.sibling1Education!!.isNotBlank() &&
                errSibling1Name.get().isNullOrBlank() &&
                errSibling1Age.get().isNullOrBlank() &&
                errSibling1Education.get().isNullOrBlank()
    }

    fun fifthPageIsValid(): Boolean {
        var result = true
        if (mutableLiveData.value?.hasConsultedBefore == null) {
            result = result.and(false)
        } else if (mutableLiveData.value?.hasConsultedBefore!!) {
            result = result.and(
                mutableLiveData.value?.placeConsulted != null && mutableLiveData.value?.placeConsulted!!.isNotBlank() &&
                        mutableLiveData.value?.yearConsulted != null && mutableLiveData.value?.yearConsulted!! > 0 &&
                        mutableLiveData.value?.monthConsulted != null && mutableLiveData.value?.monthConsulted!! > 0 &&
                        errPlaceConsulted.get().isNullOrBlank() &&
                        errDateConsulted.get().isNullOrBlank()
            )
        }

        result = result.and(
            mutableLiveData.value?.complaint != null && mutableLiveData.value?.complaint!!.isNotBlank() &&
                    mutableLiveData.value?.solution != null && mutableLiveData.value?.solution!!.isNotBlank() &&
                    mutableLiveData.value?.problem != null && mutableLiveData.value?.problem!!.isNotBlank() &&
                    mutableLiveData.value?.effortDone != null && mutableLiveData.value?.effortDone!!.isNotBlank() &&
                    errComplaint.get().isNullOrBlank() &&
                    errSolution.get().isNullOrBlank() &&
                    errProblem.get().isNullOrBlank() &&
                    errEffortDone.get().isNullOrBlank()
        )

        return result
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

    fun handleNameEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errName.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errName.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                errName.set("")
                mutableLiveData.value?.name = name

            }
        }
        errName.notifyChange()
    }

    fun handleBirthPlaceEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errBirthPlace.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errBirthPlace.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                errBirthPlace.set("")
                mutableLiveData.value?.birthPlace = name

            }
        }
        errBirthDate.notifyChange()
    }

    fun handleBirthDateEditText(value: String, formType: String) {
        if (value.isBlank()) {
            errBirthDate.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else if (!value.isDigitsOnly()) {
            errBirthDate.set(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName)
        } else {
            when (formType) {
                FormType.DAY.typeName -> {
                    if (Integer.parseInt(value) in 1..31) {
                        errBirthDate.set("")
                        mutableLiveData.value?.birthDay = Integer.parseInt(value)
                    } else {
                        errBirthDate.set(RegistrationError.INVALID_DATE.errName)
                        mutableLiveData.value?.birthDay = null
                    }
                }

                FormType.MONTH.typeName -> {
                    if (Integer.parseInt(value) in 1..12) {
                        errBirthDate.set("")
                        mutableLiveData.value?.birthMonth = Integer.parseInt(value)
                    } else {
                        errBirthDate.set(RegistrationError.INVALID_MONTH.errName)
                        mutableLiveData.value?.birthMonth = null
                    }
                }

                FormType.BIRTH_YEAR.typeName -> {
                    if (Integer.parseInt(value) in 1 until Calendar.getInstance().get(Calendar.YEAR)) {
                        errBirthDate.set(
                            ""
                        )
                        mutableLiveData.value?.birthYear = Integer.parseInt(value)
                    } else {
                        errBirthDate.set(RegistrationError.INVALID_YEAR.errName)
                        mutableLiveData.value?.birthYear = null
                    }
                }
            }
        }
        errBirthPlace.notifyChange()
    }

    fun handleAddressEditText(address: String, formType: String) {
        if (address.isBlank()) {
            errAddress.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            errAddress.set("")
            mutableLiveData.value?.address = address
        }
        errAddress.notifyChange()
    }

    fun handlePhoneNumberEditText(phoneNumber: String, formType: String) {
        if (phoneNumber.trim().length < 9) {
            errPhoneNumber.set(RegistrationError.PHONE_NUMBER_SHOULD_NOT_LESS_THAN_NINE_CHAR.errName)
        } else if (!phoneNumber.isDigitsOnly()) {
            errPhoneNumber.set(RegistrationError.PHONE_NUMBER_SHOULD_CONTAIN_DIGIT_ONLY.errName)
        } else {
            errPhoneNumber.set("")
            mutableLiveData.value?.phoneNumber = phoneNumber
        }
        errPhoneNumber.notifyChange()
    }

    fun handleReligionSpinner(religion: String?, position: Int) {
        if (religion != null && religion.isNotBlank()) {
            mutableLiveData.value?.religion = religion
            mutableLiveData.value?.religionSpinnerId = position
            errReligion.set("")
        } else {
            errReligion.set(RegistrationError.RELIGION_NOT_SELECTED.errName)
        }
        errReligion.notifyChange()
    }

    fun handleCurrentEducationSpinner(education: String?, position: Int) {
        if (education != null && education.isNotBlank()) {
            mutableLiveData.value?.currentEducation = education
            mutableLiveData.value?.currentEducationSpinnerId = position
            errCurrentEducation.set("")
        } else {
            errCurrentEducation.set(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName)
        }
        errCurrentEducation.notifyChange()
    }

    fun handleKindergartenEducationAddress(address: String?) {
        if (address != null && address.isNotBlank()) {
            mutableLiveData.value?.kindergartenData = address
            errKindergartenAddress.set("")
        } else {
            errKindergartenAddress.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        }
        errKindergartenAddress.notifyChange()
    }

    fun handleElementaryEducationAddress(address: String?) {
        if (address != null && address.isNotBlank()) {
            mutableLiveData.value?.elementaryData = address
            errElementaryAddress.set("")
        } else {
            errElementaryAddress.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        }
        errElementaryAddress.notifyChange()
    }

    fun handleJuniorEducationAddress(address: String?) {
        if (address != null && address.isNotBlank()) {
            mutableLiveData.value?.juniorData = address
            errJuniorAddress.set("")
        } else {
            errJuniorAddress.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        }
        errJuniorAddress.notifyChange()
    }

    fun handleSeniorEducationAddress(address: String?) {
        if (address != null && address.isNotBlank()) {
            mutableLiveData.value?.seniorData = address
            errSeniorAddress.set("")
        } else {
            errSeniorAddress.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        }
        errSeniorAddress.notifyChange()
    }

    fun handleCollegeEducationAddress(address: String?) {
        if (address != null && address.isNotBlank()) {
            mutableLiveData.value?.collegeData = address
            errCollegeAddress.set("")
        } else {
            errCollegeAddress.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        }
        errCollegeAddress.notifyChange()
    }

    fun handleFatherNameEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errFatherName.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errFatherName.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                errFatherName.set("")
                mutableLiveData.value?.fatherName = name

            }
        }
        errFatherName.notifyChange()
    }

    fun handleFatherAgeEditText(age: String) {
        if (age.isNotBlank() && age.isDigitsOnly()) {
            mutableLiveData.value?.fatherAge = age.toInt()
            errFatherAge.set("")
        } else {
            errFatherAge.set(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName)
        }
        errFatherAge.notifyChange()
    }

    fun handleFatherReligionSpinner(religion: String?, position: Int) {
        if (religion != null && religion.isNotBlank()) {
            mutableLiveData.value?.fatherReligion = religion
            mutableLiveData.value?.fatherReligionSpinnerId = position
            errFatherReligion.set("")
        } else {
            errFatherReligion.set(RegistrationError.RELIGION_NOT_SELECTED.errName)
        }
        errFatherReligion.notifyChange()
    }

    fun handleFatherEducationSpinner(education: String?, position: Int) {
        if (education != null && education.isNotBlank()) {
            mutableLiveData.value?.fatherEducation = education
            mutableLiveData.value?.fatherEducationSpinnerId = position
            errFatherEducation.set("")
        } else {
            errFatherEducation.set(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName)
        }
        errFatherEducation.notifyChange()
    }

    fun handleFatherOccupationEditText(tribe: String?) {
        if (tribe.isNullOrBlank()) {
            errFatherOccupation.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.fatherOccupation = tribe
            errFatherOccupation.set("")
        }
        errFatherOccupation.notifyChange()
    }

    fun handleFatherTribe(tribe: String?) {
        if (tribe.isNullOrBlank()) {
            errFatherTribe.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.fatherTribe = tribe
            errFatherTribe.set("")
        }
        errFatherTribe.notifyChange()
    }

    fun handleFatherAddressEditText(address: String) {
        if (address.isBlank()) {
            errFatherAddress.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            errFatherAddress.set("")
            mutableLiveData.value?.fatherAddress = address
        }
        errFatherAddress.notifyChange()
    }

    fun handleMotherNameEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errMotherName.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errMotherName.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                errMotherName.set("")
                mutableLiveData.value?.motherName = name

            }
        }
        errMotherName.notifyChange()
    }

    fun handleMotherAgeEditText(age: String) {
        if (age.isNotBlank() && age.isDigitsOnly()) {
            mutableLiveData.value?.motherAge = age.toInt()
            errMotherAge.set("")
        } else {
            errMotherAge.set(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName)
        }
        errMotherAge.notifyChange()
    }

    fun handleMotherReligionSpinner(religion: String?, position: Int) {
        if (religion != null && religion.isNotBlank()) {
            mutableLiveData.value?.motherReligion = religion
            mutableLiveData.value?.motherReligionSpinnerId = position
            errMotherReligion.set("")
        } else {
            errMotherReligion.set(RegistrationError.RELIGION_NOT_SELECTED.errName)
        }
        errMotherReligion.notifyChange()
    }

    fun handleMotherEducationSpinner(education: String?, position: Int) {
        if (education != null && education.isNotBlank()) {
            mutableLiveData.value?.motherEducation = education
            mutableLiveData.value?.motherEducationSpinnerId = position
            errMotherEducation.set("")
        } else {
            errMotherEducation.set(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName)
        }
        errMotherEducation.notifyChange()
    }

    fun handleMotherOccupationEditText(occupation: String?) {
        if (occupation.isNullOrBlank()) {
            errMotherOccupation.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.motherOccupation = occupation
            errMotherOccupation.set("")
        }
        errMotherOccupation.notifyChange()
    }

    fun handleMotherTribe(tribe: String?) {
        if (tribe.isNullOrBlank()) {
            errMotherTribe.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            mutableLiveData.value?.motherTribe = tribe
            errMotherTribe.set("")
        }
        errMotherTribe.notifyChange()
    }

    fun handleMotherAddressEditText(address: String) {
        if (address.isBlank()) {
            errMotherAddress.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            errMotherAddress.set("")
            mutableLiveData.value?.motherAddress = address
        }
        errMotherAddress.notifyChange()
    }

    fun handleSibling1NameEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errSibling1Name.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errSibling1Name.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                errSibling1Name.set("")
                mutableLiveData.value?.sibling1Name = name

            }
        }
        errSibling1Name.notifyChange()
    }

    fun handleSibling1AgeEditText(age: String) {
        if (age.isNotBlank() && age.isDigitsOnly()) {
            mutableLiveData.value?.sibling1Age = age.toInt()
            errSibling1Age.set("")
        } else {
            errSibling1Age.set(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName)
        }
        errSibling1Age.notifyChange()
    }

    fun handleSibling1GenderRadioButton(genderCode: String) {
        val genderFound = Gender.values().filter {
            it.code.equals(genderCode)
        }

        if (genderFound.isNotEmpty()) {
            mutableLiveData.value?.sibling1Gender = genderCode
            errSibling1Gender.set("")
        } else {
            errSibling1Gender.set(RegistrationError.GENDER_NOT_CHOSEN.errName)
        }
        errSibling1Gender.notifyChange()
    }

    fun handleSibling1EducationSpinner(education: String?, position: Int) {
        if (education != null && education.isNotBlank()) {
            mutableLiveData.value?.sibling1Education = education
            mutableLiveData.value?.sibling1EducationSpinnerId = position
            errSibling1Education.set("")
        } else {
            errSibling1Education.set(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName)
        }
        errSibling1Education.notifyChange()
    }

    fun handleSibling2NameEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errSibling2Name.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errSibling2Name.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                errSibling2Name.set("")
                mutableLiveData.value?.sibling2Name = name

            }
        }
        errSibling2Name.notifyChange()
    }

    fun handleSibling2AgeEditText(age: String) {
        if (age.isNotBlank() && age.isDigitsOnly()) {
            mutableLiveData.value?.sibling2Age = age.toInt()
            errSibling2Age.set("")
        } else {
            errSibling2Age.set(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName)
        }
        errSibling2Age.notifyChange()
    }

    fun handleSibling2GenderRadioButton(genderCode: String) {
        val genderFound = Gender.values().filter {
            it.code.equals(genderCode)
        }

        if (genderFound.isNotEmpty()) {
            mutableLiveData.value?.sibling2Gender = genderCode
            errSibling2Gender.set("")
        } else {
            errSibling2Gender.set(RegistrationError.GENDER_NOT_CHOSEN.errName)
        }
        errSibling2Gender.notifyChange()
    }

    fun handleSibling2EducationSpinner(education: String?, position: Int) {
        if (education != null && education.isNotBlank()) {
            mutableLiveData.value?.sibling2Education = education
            mutableLiveData.value?.sibling2EducationSpinnerId = position
            errSibling2Education.set("")
        } else {
            errSibling2Education.set(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName)
        }
        errSibling2Education.notifyChange()
    }

    fun handleSibling3NameEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errSibling3Name.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errSibling3Name.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                errSibling3Name.set("")
                mutableLiveData.value?.sibling3Name = name

            }
        }
        errSibling3Name.notifyChange()
    }

    fun handleSibling3AgeEditText(age: String) {
        if (age.isNotBlank() && age.isDigitsOnly()) {
            mutableLiveData.value?.sibling3Age = age.toInt()
            errSibling3Age.set("")
        } else {
            errSibling3Age.set(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName)
        }
        errSibling3Age.notifyChange()
    }

    fun handleSibling3GenderRadioButton(genderCode: String) {
        val genderFound = Gender.values().filter {
            it.code.equals(genderCode)
        }

        if (genderFound.isNotEmpty()) {
            mutableLiveData.value?.sibling3Gender = genderCode
            errSibling3Gender.set("")
        } else {
            errSibling3Gender.set(RegistrationError.GENDER_NOT_CHOSEN.errName)
        }
        errSibling3Gender.notifyChange()
    }

    fun handleSibling3EducationSpinner(education: String?, position: Int) {
        if (education != null && education.isNotBlank()) {
            mutableLiveData.value?.sibling3Education = education
            mutableLiveData.value?.sibling3EducationSpinnerId = position
            errSibling3Education.set("")
        } else {
            errSibling3Education.set(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName)
        }
        errSibling3Education.notifyChange()
    }

    fun handleSibling4NameEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errSibling4Name.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errSibling4Name.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                errSibling4Name.set("")
                mutableLiveData.value?.sibling4Name = name

            }
        }
        errSibling4Name.notifyChange()
    }

    fun handleSibling4AgeEditText(age: String) {
        if (age.isNotBlank() && age.isDigitsOnly()) {
            mutableLiveData.value?.sibling4Age = age.toInt()
            errSibling4Age.set("")
        } else {
            errSibling4Age.set(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName)
        }
        errSibling4Age.notifyChange()
    }

    fun handleSibling4GenderRadioButton(genderCode: String) {
        val genderFound = Gender.values().filter {
            it.code.equals(genderCode)
        }

        if (genderFound.isNotEmpty()) {
            mutableLiveData.value?.sibling4Gender = genderCode
            errSibling4Gender.set("")
        } else {
            errSibling4Gender.set(RegistrationError.GENDER_NOT_CHOSEN.errName)
        }
        errSibling4Gender.notifyChange()
    }

    fun handleSibling4EducationSpinner(education: String?, position: Int) {
        if (education != null && education.isNotBlank()) {
            mutableLiveData.value?.sibling4Education = education
            mutableLiveData.value?.sibling4EducationSpinnerId = position
            errSibling4Education.set("")
        } else {
            errSibling4Education.set(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName)
        }
        errSibling4Education.notifyChange()
    }

    fun handleSibling5NameEditText(name: String) {
        val pattern: Pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(name)
        val containsSpecialChar = matcher.find()
        when {
            name.isBlank() -> {
                errSibling5Name.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
            }

            containsSpecialChar -> {
                errSibling5Name.set(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName)
            }
            else -> {
                errSibling5Name.set("")
                mutableLiveData.value?.sibling5Name = name

            }
        }
        errSibling5Name.notifyChange()
    }

    fun handleSibling5AgeEditText(age: String) {
        if (age.isNotBlank() && age.isDigitsOnly()) {
            mutableLiveData.value?.sibling5Age = age.toInt()
            errSibling5Age.set("")
        } else {
            errSibling5Age.set(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName)
        }
        errSibling5Age.notifyChange()
    }

    fun handleSibling5GenderRadioButton(genderCode: String) {
        val genderFound = Gender.values().filter {
            it.code.equals(genderCode)
        }

        if (genderFound.isNotEmpty()) {
            mutableLiveData.value?.sibling5Gender = genderCode
            errSibling5Gender.set("")
        } else {
            errSibling5Gender.set(RegistrationError.GENDER_NOT_CHOSEN.errName)
        }
        errSibling5Gender.notifyChange()
    }

    fun handleSibling5EducationSpinner(education: String?, position: Int) {
        if (education != null && education.isNotBlank()) {
            mutableLiveData.value?.sibling5Education = education
            mutableLiveData.value?.sibling5EducationSpinnerId = position
            errSibling5Education.set("")
        } else {
            errSibling5Education.set(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName)
        }
        errSibling5Education.notifyChange()
    }

    fun handleHasConsultedBeforeRadioButton(hasConsulted: Boolean?) {
        if (hasConsulted != null) {
            mutableLiveData.value?.hasConsultedBefore = hasConsulted
            mutableLiveData.value?.placeConsulted = if (!hasConsulted) null else mutableLiveData.value?.placeConsulted
            mutableLiveData.value?.monthConsulted = if (!hasConsulted) null else mutableLiveData.value?.monthConsulted
            mutableLiveData.value?.yearConsulted = if (!hasConsulted) null else mutableLiveData.value?.yearConsulted

            mutableLiveData.value = mutableLiveData.value
            errHasConsultedBefore.set("")
        } else {
            errHasConsultedBefore.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        }
        errHasConsultedBefore.notifyChange()
    }

    fun handleDateConsultedEditText(value: String, formType: String) {
        if (value.isBlank()) {
            errDateConsulted.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else if (!value.isDigitsOnly()) {
            errDateConsulted.set(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName)
        } else {
            when (formType) {
                FormType.MONTH_CONSULTED.typeName -> {
                    if (Integer.parseInt(value) in 1..12) {
                        mutableLiveData.value?.monthConsulted = Integer.parseInt(value)
                        errDateConsulted.set("")
                    } else {
                        errDateConsulted.set(RegistrationError.INVALID_MONTH.errName)
                    }
                }

                FormType.YEAR_CONSULTED.typeName -> {
                    if (Integer.parseInt(value) in 1 until Calendar.getInstance().get(Calendar.YEAR)) {
                        mutableLiveData.value?.yearConsulted = Integer.parseInt(value)
                        errDateConsulted.set("")
                    } else {
                        errDateConsulted.set(RegistrationError.INVALID_YEAR.errName)
                    }
                }
            }
        }
        errDateConsulted.notifyChange()
    }

    fun handleComplaintEditText(complaint: String?) {
        if (complaint.isNullOrBlank()) {
            errComplaint.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            errComplaint.set("")
            mutableLiveData.value?.complaint = complaint
        }
        errComplaint.notifyChange()
    }

    fun handleSolutionEditText(solution: String?) {
        if (solution.isNullOrBlank()) {
            errSolution.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            errSolution.set("")
            mutableLiveData.value?.solution = solution
        }
        errSolution.notifyChange()
    }

    fun handleProblemEditText(problem: String?) {
        if (problem.isNullOrBlank()) {
            errProblem.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            errProblem.set("")
            mutableLiveData.value?.problem = problem
        }
        errProblem.notifyChange()
    }

    fun handleEffortDoneEditText(effortDone: String?) {
        if (effortDone.isNullOrBlank()) {
            errEffortDone.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            errEffortDone.set("")
            mutableLiveData.value?.effortDone = effortDone
        }
        errEffortDone.notifyChange()
    }

    fun handlePlaceConsultedEditText(placeConsulted: String?) {
        if (placeConsulted.isNullOrBlank()) {
            errPlaceConsulted.set(RegistrationError.SHOULD_NOT_EMPTY.errName)
        } else {
            errPlaceConsulted.set("")
            mutableLiveData.value?.placeConsulted = placeConsulted
        }
        errPlaceConsulted.notifyChange()
    }

    fun handleIpipQuestion(question: String, value: Int) {
        if (ipipValue.containsKey(question)) {
            ipipValue[question]?.value = value
        }
    }

    fun getIpipAnswer(question: String): Int? {
        if (ipipValue.containsKey(question)) {
            return ipipValue[question]?.value
        }

        return null
    }

    fun handleSrqQuestion(question: String, value: Int) {
        if (srqValue.containsKey(question)) {
            srqValue[question]?.value = value
        }
    }

    fun getSrqAnswer(question: String): Int? {
        if (srqValue.containsKey(question)) {
            return srqValue[question]?.value
        }

        return -1
    }

    fun ipipPageIsValid(): Boolean {
        return ipipValue.filter {
            it.value.value == -1
        }.isEmpty()
    }

    fun srqPageIsValid(): Boolean {
        return srqValue.filter {
            it.value.value == null
        }.isEmpty()
    }
}

