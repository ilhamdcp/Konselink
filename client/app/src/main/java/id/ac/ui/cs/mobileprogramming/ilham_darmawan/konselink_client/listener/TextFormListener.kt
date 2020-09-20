package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.FormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.CounselorViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ProfileViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.RegistrationViewModel

class TextFormListener : TextWatcher {
    private var registrationViewModel: RegistrationViewModel? = null
    private var counselorViewModel: CounselorViewModel? = null
    private var profileViewModel: ProfileViewModel? = null
    private var formType: String

    constructor(registrationViewModel: RegistrationViewModel, formType: String) {
        this.registrationViewModel = registrationViewModel
        this.formType = formType
    }

    constructor(counselorViewModel: CounselorViewModel, formType: String) {
        this.counselorViewModel = counselorViewModel
        this.formType = formType
    }

    constructor(profileViewModel: ProfileViewModel, formType: String) {
        this.profileViewModel = profileViewModel
        this.formType = formType
    }

    override fun afterTextChanged(s: Editable?) {
        when (formType) {
            FormType.NICKNAME.typeName -> {
                registrationViewModel?.handleNameEditText(s.toString())
                profileViewModel?.handleNicknameEditText(s.toString())
            }

            FormType.BIRTH_PLACE.typeName -> {
                registrationViewModel?.handleBirthPlaceEditText(s.toString())
            }

            FormType.DAY.typeName -> {
                registrationViewModel?.handleBirthDateEditText(s.toString(), FormType.DAY.typeName)
            }

            FormType.MONTH.typeName -> {
                registrationViewModel?.handleBirthDateEditText(s.toString(), FormType.MONTH.typeName)
            }

            FormType.BIRTH_YEAR.typeName -> {
                registrationViewModel?.handleBirthDateEditText(s.toString(), FormType.BIRTH_YEAR.typeName)
            }

            FormType.ADDRESS.typeName -> {
                registrationViewModel?.handleAddressEditText(s.toString(), FormType.ADDRESS.typeName)
            }

            FormType.PHONE_NUMBER.typeName -> {
                registrationViewModel?.handlePhoneNumberEditText(s.toString(), FormType.PHONE_NUMBER.typeName)
            }

            FormType.KINDERGARTEN_ADDRESS.typeName -> {
                registrationViewModel?.handleKindergartenEducationAddress(s.toString())
            }

            FormType.ELEMENTARY_ADDRESS.typeName -> {
                registrationViewModel?.handleElementaryEducationAddress(s.toString())
            }

            FormType.JUNIOR_ADDRESS.typeName -> {
                registrationViewModel?.handleJuniorEducationAddress(s.toString())
            }

            FormType.SENIOR_ADDRESS.typeName -> {
                registrationViewModel?.handleSeniorEducationAddress(s.toString())
            }

            FormType.COLLEGE_ADDRESS.typeName -> {
                registrationViewModel?.handleCollegeEducationAddress(s.toString())
            }

            FormType.FATHER_NAME.typeName -> {
                registrationViewModel?.handleFatherNameEditText(s.toString())
            }

            FormType.FATHER_AGE.typeName -> {
                registrationViewModel?.handleFatherAgeEditText(s.toString())
            }

            FormType.FATHER_TRIBE.typeName -> {
                registrationViewModel?.handleFatherTribe(s.toString())
            }

            FormType.FATHER_OCCUPATION.typeName -> {
                registrationViewModel?.handleFatherOccupationEditText(s.toString())
            }

            FormType.FATHER_ADDRESS.typeName -> {
                registrationViewModel?.handleFatherAddressEditText(s.toString())
            }

            FormType.MOTHER_NAME.typeName -> {
                registrationViewModel?.handleMotherNameEditText(s.toString())
            }

            FormType.MOTHER_AGE.typeName -> {
                registrationViewModel?.handleMotherAgeEditText(s.toString())
            }

            FormType.MOTHER_TRIBE.typeName -> {
                registrationViewModel?.handleMotherTribe(s.toString())
            }

            FormType.MOTHER_OCCUPATION.typeName -> {
                registrationViewModel?.handleMotherOccupationEditText(s.toString())
            }

            FormType.MOTHER_ADDRESS.typeName -> {
                registrationViewModel?.handleMotherAddressEditText(s.toString())
            }

            FormType.SIBLING1_NAME.typeName -> {
                registrationViewModel?.handleSibling1NameEditText(s.toString())
            }

            FormType.SIBLING1_AGE.typeName -> {
                registrationViewModel?.handleSibling1AgeEditText(s.toString())
            }

            FormType.SIBLING2_NAME.typeName -> {
                registrationViewModel?.handleSibling2NameEditText(s.toString())
            }

            FormType.SIBLING2_AGE.typeName -> {
                registrationViewModel?.handleSibling2AgeEditText(s.toString())
            }

            FormType.SIBLING3_NAME.typeName -> {
                registrationViewModel?.handleSibling3NameEditText(s.toString())
            }

            FormType.SIBLING3_AGE.typeName -> {
                registrationViewModel?.handleSibling3AgeEditText(s.toString())
            }

            FormType.SIBLING4_NAME.typeName -> {
                registrationViewModel?.handleSibling4NameEditText(s.toString())
            }

            FormType.SIBLING4_AGE.typeName -> {
                registrationViewModel?.handleSibling4AgeEditText(s.toString())
            }

            FormType.SIBLING5_NAME.typeName -> {
                registrationViewModel?.handleSibling5NameEditText(s.toString())
            }

            FormType.SIBLING5_AGE.typeName -> {
                registrationViewModel?.handleSibling5AgeEditText(s.toString())
            }

            FormType.PLACE_CONSULTED.typeName -> {
                registrationViewModel?.handlePlaceConsultedEditText(s.toString())
            }

            FormType.MONTH_CONSULTED.typeName, FormType.YEAR_CONSULTED.typeName -> {
                registrationViewModel?.handleDateConsultedEditText(s.toString(), formType)
            }

            FormType.PLACE_CONSULTED.typeName -> {
                registrationViewModel?.handlePlaceConsultedEditText(s.toString())
            }

            FormType.COMPLAINT.typeName -> {
                registrationViewModel?.handleComplaintEditText(s.toString())
            }

            FormType.SOLUTION.typeName -> {
                registrationViewModel?.handleSolutionEditText(s.toString())
            }

            FormType.PROBLEM.typeName -> {
                registrationViewModel?.handleProblemEditText(s.toString())
            }

            FormType.EFFORT_DONE.typeName -> {
                registrationViewModel?.handleEffortDoneEditText(s.toString())
            }

            FormType.SEARCH_COUNSELOR.typeName -> {
                counselorViewModel?.handleSearchCounselorEditText(s.toString())
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}