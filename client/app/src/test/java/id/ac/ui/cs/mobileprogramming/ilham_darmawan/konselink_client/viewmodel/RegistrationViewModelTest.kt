package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.os.Build
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration.RegistrationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.IpipAndSrqRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Registration
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository.UserRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
class RegistrationViewModelTest {
    private lateinit var activity: RegistrationActivity
    private lateinit var registrationViewModel: RegistrationViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private var mockWebServer = MockWebServer()
    private lateinit var registrationService: RegistrationService
    private lateinit var basicCall: Call<BasicResponse>
    private val jsonBody = "{\"code\": \"200\", " +
            "\"data\": [" +
            "{\"username\": \"username\", " +
            "\"name\": \"name\", " +
            "\"faculty\": \"faculty\"," +
            "\"academicId\": \"academicId\"," +
            "\"studyProgram\": \"studyProgram\"," +
            "\"educationalProgram\": \"educationalProgram\"," +
            "\"role\": \"role \"}" +
            "]}"

    @Before
    fun setUp() {
        val gson = GsonBuilder().setLenient().create()
        registrationService = mock(RegistrationService::class.java)
        activity = buildActivity(RegistrationActivity::class.java).create().get()
        registrationViewModel = ViewModelProvider(
            activity,
            RegistrationViewModelFactory(
                activity.applicationContext,
                registrationService,
                Room.inMemoryDatabaseBuilder(activity, ApplicationDatabase::class.java).allowMainThreadQueries().build()
            )
        ).get(RegistrationViewModel::class.java)
        registrationViewModel.mutableLiveData.value = Registration()

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        verifyNoMoreInteractions(registrationService, basicCall)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Test
    fun givenInvalidGender_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleGenderRadioButton("asd")
        assertEquals(RegistrationError.GENDER_NOT_CHOSEN.errName, registrationViewModel.errGender.get().toString())
    }

    @Test
    fun givenValidGender_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleGenderRadioButton(Gender.L.code)
        assertNotNull(registrationViewModel.mutableLiveData.value?.gender)
    }

    @Test
    fun givenValidName_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val name = "Name"
        registrationViewModel.handleNameEditText(name)
        assertEquals(name, registrationViewModel.mutableLiveData.value?.name)
    }

    @Test
    fun givenNameContainingSpecialCharOrNumber_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val name = "999,,"
        registrationViewModel.handleNameEditText(name)
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errName.get().toString())
    }

    @Test
    fun givenBlankName_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val name = "    "
        registrationViewModel.handleNameEditText(name)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errName.get().toString())
    }

    @Test
    fun givenValidBirthPlace_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val name = "Jakarta"
        registrationViewModel.handleBirthPlaceEditText(name)
        assertEquals(name, registrationViewModel.mutableLiveData.value?.birthPlace)
    }

    @Test
    fun givenBirthPlaceContainingSpecialCharOrNumber_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val name = "Kelapa 2"
        registrationViewModel.handleBirthPlaceEditText(name)
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errBirthPlace.get().toString())
    }

    @Test
    fun givenBlankBirthPlace_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val name = "    "
        registrationViewModel.handleBirthPlaceEditText(name)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errBirthPlace.get().toString())
    }

    @Test
    fun givenBlankBirthDate_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthDate = "    "
        registrationViewModel.handleBirthDateEditText(birthDate, FormType.DAY.typeName)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errBirthDate.get().toString())
    }

    @Test
    fun givenContainingNonDigitBirthDate_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthDate = "date"
        registrationViewModel.handleBirthDateEditText(birthDate, FormType.DAY.typeName)
        assertEquals(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName, registrationViewModel.errBirthDate.get().toString())
    }

    @Test
    fun givenValidDay_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthDay = "9"
        registrationViewModel.handleBirthDateEditText(birthDay, FormType.DAY.typeName)
        assertEquals(birthDay, registrationViewModel.mutableLiveData.value?.birthDay.toString())
    }

    @Test
    fun givenInvalidDay_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthDay = "32"
        registrationViewModel.handleBirthDateEditText(birthDay, FormType.DAY.typeName)
        assertEquals(RegistrationError.INVALID_DATE.errName, registrationViewModel.errBirthDate.get())
    }

    @Test
    fun givenValidMonth_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthMonth = "9"
        registrationViewModel.handleBirthDateEditText(birthMonth, FormType.MONTH.typeName)
        assertEquals(birthMonth, registrationViewModel.mutableLiveData.value?.birthMonth.toString())
    }

    @Test
    fun givenInvalidMonth_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthMonth = "13"
        registrationViewModel.handleBirthDateEditText(birthMonth, FormType.MONTH.typeName)
        assertEquals(RegistrationError.INVALID_MONTH.errName, registrationViewModel.errBirthDate.get())
    }

    @Test
    fun givenValidBirthYear_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthYear = "1999"
        registrationViewModel.handleBirthDateEditText(birthYear, FormType.BIRTH_YEAR.typeName)
        assertEquals(birthYear, registrationViewModel.mutableLiveData.value?.birthYear.toString())
    }

    @Test
    fun givenInvalidYear_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthDay = Int.MAX_VALUE.toString()
        registrationViewModel.handleBirthDateEditText(birthDay, FormType.BIRTH_YEAR.typeName)
        assertEquals(RegistrationError.INVALID_YEAR.errName, registrationViewModel.errBirthDate.get())
    }

    @Test
    fun givenValidAddress_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val address = "Address St., Jakarta Selatan"
        registrationViewModel.handleAddressEditText(address, FormType.ADDRESS.typeName)
        assertEquals(address, registrationViewModel.mutableLiveData.value?.address.toString())
    }

    @Test
    fun givenInvalidAddress_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val address = "        "
        registrationViewModel.handleAddressEditText(address, FormType.ADDRESS.typeName)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errAddress.get())
    }

    @Test
    fun givenPhoneNumberWithLengthLessThanNine_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val phoneNumber = "14045"
        registrationViewModel.handlePhoneNumberEditText(phoneNumber, FormType.PHONE_NUMBER.typeName)
        assertEquals(RegistrationError.PHONE_NUMBER_SHOULD_NOT_LESS_THAN_NINE_CHAR.errName, registrationViewModel.errPhoneNumber.get())
    }

    @Test
    fun givenPhoneNumberContainingNonDigit_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val phoneNumber = "+628129889900"
        registrationViewModel.handlePhoneNumberEditText(phoneNumber, FormType.PHONE_NUMBER.typeName)
        assertEquals(RegistrationError.PHONE_NUMBER_SHOULD_CONTAIN_DIGIT_ONLY.errName, registrationViewModel.errPhoneNumber.get())
    }

    @Test
    fun givenValidPhoneNumber_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val phoneNumber = "0812128080"
        registrationViewModel.handlePhoneNumberEditText(phoneNumber, FormType.PHONE_NUMBER.typeName)
        assertEquals(phoneNumber, registrationViewModel.mutableLiveData.value?.phoneNumber)
    }

    @Test
    fun givenSelectedReligionSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val religion = Religion.ISLAM.religionName
        registrationViewModel.handleReligionSpinner(religion, 0)
        assertEquals(religion, registrationViewModel.mutableLiveData.value?.religion)
    }

    @Test
    fun givenNullReligionSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val religion = null
        registrationViewModel.handleReligionSpinner(religion, 0)
        assertEquals(RegistrationError.RELIGION_NOT_SELECTED.errName, registrationViewModel.errReligion.get())
    }

    @Test
    fun givenSelectedCurrentEducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = Education.BACHELOR.educationType
        registrationViewModel.handleCurrentEducationSpinner(education, 0)
        assertEquals(education, registrationViewModel.mutableLiveData.value?.currentEducation)
    }

    @Test
    fun givenNullCurrentEducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = null
        registrationViewModel.handleCurrentEducationSpinner(education, 0)
        assertEquals(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName, registrationViewModel.errCurrentEducation.get())
    }

    @Test
    fun givenUninitializedLiveData_shouldReturnFalse() {
        shadowOf(Looper.getMainLooper()).idle()
        assertFalse(registrationViewModel.firstPageIsValid())
    }

    @Test
    fun givenNullData_shouldReturnNullRegistrationData() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.registrationLiveData.observeForever {
            assertNull(it)
        }
    }

    @Test
    fun givenSuccessfulInsert_shouldIncreaseSize() {
        shadowOf(Looper.getMainLooper()).idle()
        val registration = Registration(name = "registration")
        registrationViewModel.mutableLiveData.value = registration
        registrationViewModel.saveCurrentRegistration()
        registrationViewModel.registrationLiveData.observeForever {
            assertEquals(registration.name, it.name)
        }
    }

    @Test
    fun givenNonBlankKindergartenData_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleKindergartenEducationAddress("address")
        assertEquals("address", registrationViewModel.mutableLiveData.value?.kindergartenData)
    }

    @Test
    fun givenBlankKindergartenData_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleKindergartenEducationAddress("  ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errKindergartenAddress.get())
    }

    @Test
    fun givenNonBlankElementaryData_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleElementaryEducationAddress("address")
        assertEquals("address", registrationViewModel.mutableLiveData.value?.elementaryData)
    }

    @Test
    fun givenBlankElementaryData_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleElementaryEducationAddress("  ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errElementaryAddress.get())
    }

    @Test
    fun givenNonBlankJuniorData_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleJuniorEducationAddress("address")
        assertEquals("address", registrationViewModel.mutableLiveData.value?.juniorData)
    }

    @Test
    fun givenBlankJuniorData_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleJuniorEducationAddress("  ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errJuniorAddress.get())
    }

    @Test
    fun givenNonBlankSeniorData_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSeniorEducationAddress("address")
        assertEquals("address", registrationViewModel.mutableLiveData.value?.seniorData)
    }

    @Test
    fun givenBlankSeniorData_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSeniorEducationAddress("  ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSeniorAddress.get())
    }

    @Test
    fun givenNonBlankCollegeData_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleCollegeEducationAddress("address")
        assertEquals("address", registrationViewModel.mutableLiveData.value?.collegeData)
    }

    @Test
    fun givenBlankCollegeData_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleCollegeEducationAddress("  ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errCollegeAddress.get())
    }

    @Test
    fun givenValidData_shouldReturnFirstPageValid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.mutableLiveData.value?.name ="name"
        registrationViewModel.mutableLiveData.value?.gender ="gender"
        registrationViewModel.mutableLiveData.value?.birthPlace ="place"
        registrationViewModel.mutableLiveData.value?.birthDay = 1
        registrationViewModel.mutableLiveData.value?.birthMonth = 10
        registrationViewModel.mutableLiveData.value?.birthYear = 2000
        registrationViewModel.mutableLiveData.value?.address = "address"
        registrationViewModel.mutableLiveData.value?.phoneNumber = "081212121212"
        registrationViewModel.mutableLiveData.value?.religion = "religion"
        registrationViewModel.mutableLiveData.value?.currentEducation = "education"
        assertTrue(registrationViewModel.firstPageIsValid())
    }

    @Test
    fun givenCollegeDegreeAndValidData_shouldReturnSecondPageValid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.mutableLiveData.value?.currentEducation = Education.BACHELOR.educationType
        registrationViewModel.handleKindergartenEducationAddress("This is data")
        registrationViewModel.handleElementaryEducationAddress("This is data")
        registrationViewModel.handleJuniorEducationAddress("This is data")
        registrationViewModel.handleSeniorEducationAddress("This is data")
        registrationViewModel.handleCollegeEducationAddress("This is data")

        assertTrue(registrationViewModel.secondPageIsValid())
    }

    @Test
    fun givenValidFatherName_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFatherNameEditText("Name")
        assertEquals("Name", registrationViewModel.mutableLiveData.value?.fatherName)
    }

    @Test
    fun givenContainingSpecialCharFatherName_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFatherNameEditText(",,")
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errFatherName.get())
    }

    @Test
    fun givenBlankFatherName_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFatherNameEditText("   ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errFatherName.get())
    }

    @Test
    fun givenValidFatherAge_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFatherAgeEditText("32")
        assertEquals(32, registrationViewModel.mutableLiveData.value?.fatherAge)
    }

    @Test
    fun givenNonDigitFatherAge_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFatherAgeEditText("32a")
        assertEquals(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName, registrationViewModel.errFatherAge.get())
    }

    @Test
    fun givenSelectedFatherReligionSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val religion = Religion.ISLAM.religionName
        registrationViewModel.handleFatherReligionSpinner(religion, 0)
        assertEquals(religion, registrationViewModel.mutableLiveData.value?.fatherReligion)
    }

    @Test
    fun givenNullFatherReligionSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val religion = null
        registrationViewModel.handleFatherReligionSpinner(religion, 0)
        assertEquals(RegistrationError.RELIGION_NOT_SELECTED.errName, registrationViewModel.errFatherReligion.get())
    }

    @Test
    fun givenSelectedFatherEducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = Education.BACHELOR.educationType
        registrationViewModel.handleFatherEducationSpinner(education, 0)
        assertEquals(education, registrationViewModel.mutableLiveData.value?.fatherEducation)
    }

    @Test
    fun givenNullFatherEducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = null
        registrationViewModel.handleFatherEducationSpinner(education, 0)
        assertEquals(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName, registrationViewModel.errFatherEducation.get())
    }

    @Test
    fun givenValidFatherOccupation_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFatherOccupationEditText("Occupation")
        assertEquals("Occupation", registrationViewModel.mutableLiveData.value?.fatherOccupation)
    }

    @Test
    fun givenBlankOccupation_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFatherOccupationEditText("  ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errFatherOccupation.get())
    }


    @Test
    fun givenNonBlankFatherTribeName_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFatherTribe("tribe")
        assertEquals("tribe", registrationViewModel.mutableLiveData.value?.fatherTribe)
    }

    @Test
    fun givenBlankFatherTribeName_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFatherTribe("  ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errFatherTribe.get())
    }

    @Test
    fun givenValidFatherAddress_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val address = "Address St., Jakarta Selatan"
        registrationViewModel.handleFatherAddressEditText(address)
        assertEquals(address, registrationViewModel.mutableLiveData.value?.fatherAddress.toString())
    }

    @Test
    fun givenInvalidFatherAddress_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val address = "        "
        registrationViewModel.handleFatherAddressEditText(address)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errFatherAddress.get())
    }

    @Test
    fun givenValidMotherName_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleMotherNameEditText("Name")
        assertEquals("Name", registrationViewModel.mutableLiveData.value?.motherName)
    }

    @Test
    fun givenContainingSpecialCharMotherName_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleMotherNameEditText(",,")
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errMotherName.get())
    }

    @Test
    fun givenBlankMotherName_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleMotherNameEditText("   ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errMotherName.get())
    }

    @Test
    fun givenValidMotherAge_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleMotherAgeEditText("32")
        assertEquals(32, registrationViewModel.mutableLiveData.value?.motherAge)
    }

    @Test
    fun givenNonDigitMotherAge_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleMotherAgeEditText("32a")
        assertEquals(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName, registrationViewModel.errMotherAge.get())
    }

    @Test
    fun givenSelectedMotherReligionSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val religion = Religion.ISLAM.religionName
        registrationViewModel.handleMotherReligionSpinner(religion, 0)
        assertEquals(religion, registrationViewModel.mutableLiveData.value?.motherReligion)
    }

    @Test
    fun givenNullMotherReligionSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val religion = null
        registrationViewModel.handleMotherReligionSpinner(religion, 0)
        assertEquals(RegistrationError.RELIGION_NOT_SELECTED.errName, registrationViewModel.errMotherReligion.get())
    }

    @Test
    fun givenSelectedMotherEducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = Education.BACHELOR.educationType
        registrationViewModel.handleMotherEducationSpinner(education, 0)
        assertEquals(education, registrationViewModel.mutableLiveData.value?.motherEducation)
    }

    @Test
    fun givenNullMotherEducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = null
        registrationViewModel.handleMotherEducationSpinner(education, 0)
        assertEquals(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName, registrationViewModel.errMotherEducation.get())
    }

    @Test
    fun givenNonBlankMotherTribeName_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleMotherTribe("tribe")
        assertEquals("tribe", registrationViewModel.mutableLiveData.value?.motherTribe)
    }

    @Test
    fun givenValidMotherOccupation_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleMotherOccupationEditText("Occupation")
        assertEquals("Occupation", registrationViewModel.mutableLiveData.value?.motherOccupation)
    }

    @Test
    fun givenBlankMotherOccupation_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleMotherOccupationEditText("  ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errMotherOccupation.get())
    }

    @Test
    fun givenBlankMotherTribeName_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleMotherTribe("  ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errMotherTribe.get())
    }

    @Test
    fun givenValidMotherAddress_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val address = "Address St., Jakarta Selatan"
        registrationViewModel.handleMotherAddressEditText(address)
        assertEquals(address, registrationViewModel.mutableLiveData.value?.motherAddress.toString())
    }

    @Test
    fun givenInvalidMotherAddress_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val address = "        "
        registrationViewModel.handleMotherAddressEditText(address)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errMotherAddress.get())
    }

    @Test
    fun givenValidThirdPage_shouldReturnTrue() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.mutableLiveData.value?.fatherName = "name"
        registrationViewModel.mutableLiveData.value?.fatherAge = 32
        registrationViewModel.mutableLiveData.value?.fatherReligion = "religion"
        registrationViewModel.mutableLiveData.value?.fatherTribe = "tribe"
        registrationViewModel.mutableLiveData.value?.fatherEducation = "education"
        registrationViewModel.mutableLiveData.value?.fatherOccupation = "occupation"
        registrationViewModel.mutableLiveData.value?.fatherAddress = "address"

        registrationViewModel.mutableLiveData.value?.motherName = "name"
        registrationViewModel.mutableLiveData.value?.motherAge = 32
        registrationViewModel.mutableLiveData.value?.motherReligion = "religion"
        registrationViewModel.mutableLiveData.value?.motherTribe = "tribe"
        registrationViewModel.mutableLiveData.value?.motherEducation = "education"
        registrationViewModel.mutableLiveData.value?.motherOccupation = "occupation"
        registrationViewModel.mutableLiveData.value?.motherAddress = "address"

        assertTrue(registrationViewModel.thirdPageIsValid())
    }

    fun givenInvalidThirdPage_shouldReturnFalse() {
        shadowOf(Looper.getMainLooper()).idle()
        assertFalse(registrationViewModel.thirdPageIsValid())
    }

    @Test
    fun givenValidSibling1Name_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling1NameEditText("Name")
        assertEquals("Name", registrationViewModel.mutableLiveData.value?.sibling1Name)
    }

    @Test
    fun givenContainingSpecialCharSibling1Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling1NameEditText(",,")
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errSibling1Name.get())
    }

    @Test
    fun givenInvalidSibling1Gender_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling1GenderRadioButton("asd")
        assertEquals(RegistrationError.GENDER_NOT_CHOSEN.errName, registrationViewModel.errSibling1Gender.get().toString())
    }

    @Test
    fun givenValidSibling1Gender_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling1GenderRadioButton(Gender.L.code)
        assertEquals(Gender.L.code, registrationViewModel.mutableLiveData.value?.sibling1Gender)
    }

    @Test
    fun givenBlankSibling1Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling1NameEditText("   ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSibling1Name.get())
    }

    @Test
    fun givenValidSibling1Age_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling1AgeEditText("32")
        assertEquals(32, registrationViewModel.mutableLiveData.value?.sibling1Age)
    }

    @Test
    fun givenNonDigitSibling1Age_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling1AgeEditText("32a")
        assertEquals(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName, registrationViewModel.errSibling1Age.get())
    }

    @Test
    fun givenSelectedSibling1EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = Education.BACHELOR.educationType
        registrationViewModel.handleSibling1EducationSpinner(education, 0)
        assertEquals(education, registrationViewModel.mutableLiveData.value?.sibling1Education)
    }

    @Test
    fun givenNullSibling1EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = null
        registrationViewModel.handleSibling1EducationSpinner(education, 0)
        assertEquals(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName, registrationViewModel.errSibling1Education.get())
    }

    @Test
    fun givenValidSibling2Name_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling2NameEditText("Name")
        assertEquals("Name", registrationViewModel.mutableLiveData.value?.sibling2Name)
    }

    @Test
    fun givenContainingSpecialCharSibling2Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling2NameEditText(",,")
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errSibling2Name.get())
    }

    @Test
    fun givenInvalidSibling2Gender_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling2GenderRadioButton("asd")
        assertEquals(RegistrationError.GENDER_NOT_CHOSEN.errName, registrationViewModel.errSibling2Gender.get().toString())
    }

    @Test
    fun givenValidSibling2Gender_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling2GenderRadioButton(Gender.L.code)
        assertEquals(Gender.L.code, registrationViewModel.mutableLiveData.value?.sibling2Gender)
    }

    @Test
    fun givenBlankSibling2Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling2NameEditText("   ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSibling2Name.get())
    }

    @Test
    fun givenValidSibling2Age_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling2AgeEditText("32")
        assertEquals(32, registrationViewModel.mutableLiveData.value?.sibling2Age)
    }

    @Test
    fun givenNonDigitSibling2Age_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling2AgeEditText("32a")
        assertEquals(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName, registrationViewModel.errSibling2Age.get())
    }

    @Test
    fun givenSelectedSibling2EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = Education.BACHELOR.educationType
        registrationViewModel.handleSibling2EducationSpinner(education, 0)
        assertEquals(education, registrationViewModel.mutableLiveData.value?.sibling2Education)
    }

    @Test
    fun givenNullSibling2EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = null
        registrationViewModel.handleSibling2EducationSpinner(education, 0)
        assertEquals(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName, registrationViewModel.errSibling2Education.get())
    }

    @Test
    fun givenValidSibling3Name_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling3NameEditText("Name")
        assertEquals("Name", registrationViewModel.mutableLiveData.value?.sibling3Name)
    }

    @Test
    fun givenContainingSpecialCharSibling3Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling3NameEditText(",,")
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errSibling3Name.get())
    }

    @Test
    fun givenInvalidSibling3Gender_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling3GenderRadioButton("asd")
        assertEquals(RegistrationError.GENDER_NOT_CHOSEN.errName, registrationViewModel.errSibling3Gender.get().toString())
    }

    @Test
    fun givenValidSibling3Gender_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling3GenderRadioButton(Gender.L.code)
        assertEquals(Gender.L.code, registrationViewModel.mutableLiveData.value?.sibling3Gender)
    }

    @Test
    fun givenBlankSibling3Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling3NameEditText("   ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSibling3Name.get())
    }

    @Test
    fun givenValidSibling3Age_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling3AgeEditText("32")
        assertEquals(32, registrationViewModel.mutableLiveData.value?.sibling3Age)
    }

    @Test
    fun givenNonDigitSibling3Age_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling3AgeEditText("32a")
        assertEquals(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName, registrationViewModel.errSibling3Age.get())
    }

    @Test
    fun givenSelectedSibling3EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = Education.BACHELOR.educationType
        registrationViewModel.handleSibling3EducationSpinner(education, 0)
        assertEquals(education, registrationViewModel.mutableLiveData.value?.sibling3Education)
    }

    @Test
    fun givenNullSibling3EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = null
        registrationViewModel.handleSibling3EducationSpinner(education, 0)
        assertEquals(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName, registrationViewModel.errSibling3Education.get())
    }

    @Test
    fun givenValidSibling4Name_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling4NameEditText("Name")
        assertEquals("Name", registrationViewModel.mutableLiveData.value?.sibling4Name)
    }

    @Test
    fun givenContainingSpecialCharSibling4Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling4NameEditText(",,")
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errSibling4Name.get())
    }

    @Test
    fun givenInvalidSibling4Gender_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling4GenderRadioButton("asd")
        assertEquals(RegistrationError.GENDER_NOT_CHOSEN.errName, registrationViewModel.errSibling4Gender.get().toString())
    }

    @Test
    fun givenValidSibling4Gender_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling4GenderRadioButton(Gender.L.code)
        assertEquals(Gender.L.code, registrationViewModel.mutableLiveData.value?.sibling4Gender)
    }

    @Test
    fun givenBlankSibling4Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling4NameEditText("   ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSibling4Name.get())
    }

    @Test
    fun givenValidSibling4Age_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling4AgeEditText("32")
        assertEquals(32, registrationViewModel.mutableLiveData.value?.sibling4Age)
    }

    @Test
    fun givenNonDigitSibling4Age_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling4AgeEditText("32a")
        assertEquals(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName, registrationViewModel.errSibling4Age.get())
    }

    @Test
    fun givenSelectedSibling4EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = Education.BACHELOR.educationType
        registrationViewModel.handleSibling4EducationSpinner(education, 0)
        assertEquals(education, registrationViewModel.mutableLiveData.value?.sibling4Education)
    }

    @Test
    fun givenNullSibling4EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = null
        registrationViewModel.handleSibling4EducationSpinner(education, 0)
        assertEquals(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName, registrationViewModel.errSibling4Education.get())
    }

    @Test
    fun givenValidSibling5Name_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling5NameEditText("Name")
        assertEquals("Name", registrationViewModel.mutableLiveData.value?.sibling5Name)
    }

    @Test
    fun givenContainingSpecialCharSibling5Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling5NameEditText(",,")
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errSibling5Name.get())
    }

    @Test
    fun givenInvalidSibling5Gender_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling5GenderRadioButton("asd")
        assertEquals(RegistrationError.GENDER_NOT_CHOSEN.errName, registrationViewModel.errSibling5Gender.get().toString())
    }

    @Test
    fun givenValidSibling5Gender_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling5GenderRadioButton(Gender.L.code)
        assertEquals(Gender.L.code, registrationViewModel.mutableLiveData.value?.sibling5Gender)
    }

    @Test
    fun givenBlankSibling5Name_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling5NameEditText("   ")
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSibling5Name.get())
    }

    @Test
    fun givenValidSibling5Age_shouldUpdateViewModelLiveDataAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling5AgeEditText("32")
        assertEquals(32, registrationViewModel.mutableLiveData.value?.sibling5Age)
    }

    @Test
    fun givenNonDigitSibling5Age_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling5AgeEditText("32a")
        assertEquals(RegistrationError.SHOULD_CONTAIN_DIGIT_ONLY.errName, registrationViewModel.errSibling5Age.get())
    }

    @Test
    fun givenSelectedSibling5EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = Education.BACHELOR.educationType
        registrationViewModel.handleSibling5EducationSpinner(education, 0)
        assertEquals(education, registrationViewModel.mutableLiveData.value?.sibling5Education)
    }

    @Test
    fun givenNullSibling5EducationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = null
        registrationViewModel.handleSibling5EducationSpinner(education, 0)
        assertEquals(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName, registrationViewModel.errSibling5Education.get())
    }

    @Test
    fun givenNonEmptyFirstSibling_shouldReturnFourthPageIsValid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSibling1NameEditText("name")
        registrationViewModel.handleSibling1AgeEditText(32.toString())
        registrationViewModel.handleSibling1GenderRadioButton(Gender.L.code)
        registrationViewModel.handleSibling1EducationSpinner(Education.BACHELOR.educationType, 0)
        assertTrue(registrationViewModel.fourthPageIsValid())
    }

    @Test
    fun givenEmptyFirstSibling_shouldReturnFourthPageIsInvalid() {
        shadowOf(Looper.getMainLooper()).idle()
        assertFalse(registrationViewModel.fourthPageIsValid())
    }

    @Test
    fun givenInvalidHasConsultedBefore_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleHasConsultedBeforeRadioButton(null)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errHasConsultedBefore.get().toString())
    }

    @Test
    fun givenValidHasConsultedBefore_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleHasConsultedBeforeRadioButton(true)
        assertTrue(registrationViewModel.mutableLiveData.value?.hasConsultedBefore!!)
    }

    @Test
    fun givenValidHasNotConsultedBefore_shouldUpdateViewModelAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleHasConsultedBeforeRadioButton(false)
        assertFalse(registrationViewModel.mutableLiveData.value?.hasConsultedBefore!!)
    }

    @Test
    fun givenValidMonthConsulted_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthMonth = "9"
        registrationViewModel.handleDateConsultedEditText(birthMonth, FormType.MONTH_CONSULTED.typeName)
        assertEquals(birthMonth, registrationViewModel.mutableLiveData.value?.monthConsulted.toString())
    }

    @Test
    fun givenInvalidMonthConsulted_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthMonth = "13"
        registrationViewModel.handleDateConsultedEditText(birthMonth, FormType.MONTH_CONSULTED.typeName)
        assertEquals(RegistrationError.INVALID_MONTH.errName, registrationViewModel.errDateConsulted.get())
    }

    @Test
    fun givenValidYearConsulted_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthYear = "1999"
        registrationViewModel.handleDateConsultedEditText(birthYear, FormType.YEAR_CONSULTED.typeName)
        assertEquals(birthYear, registrationViewModel.mutableLiveData.value?.yearConsulted.toString())
    }

    @Test
    fun givenInvalidYearConsulted_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val birthDay = Int.MAX_VALUE.toString()
        registrationViewModel.handleDateConsultedEditText(birthDay, FormType.YEAR_CONSULTED.typeName)
        assertEquals(RegistrationError.INVALID_YEAR.errName, registrationViewModel.errDateConsulted.get())
    }

    @Test
    fun givenValidComplaint_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val complaint = "Complaint St., Jakarta Selatan"
        registrationViewModel.handleComplaintEditText(complaint)
        assertEquals(complaint, registrationViewModel.mutableLiveData.value?.complaint.toString())
    }

    @Test
    fun givenInvalidComplaint_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val complaint = "        "
        registrationViewModel.handleComplaintEditText(complaint)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errComplaint.get())
    }

    @Test
    fun givenValidSolution_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val solution = "Solution St., Jakarta Selatan"
        registrationViewModel.handleSolutionEditText(solution)
        assertEquals(solution, registrationViewModel.mutableLiveData.value?.solution.toString())
    }

    @Test
    fun givenInvalidSolution_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val solution = "        "
        registrationViewModel.handleSolutionEditText(solution)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSolution.get())
    }

    @Test
    fun givenValidProblem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val solution = "Problem St., Jakarta Selatan"
        registrationViewModel.handleProblemEditText(solution)
        assertEquals(solution, registrationViewModel.mutableLiveData.value?.problem.toString())
    }

    @Test
    fun givenInvalidProblem_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val solution = "        "
        registrationViewModel.handleProblemEditText(solution)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errProblem.get())
    }

    @Test
    fun givenValidEffortDone_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val solution = "EffortDone St., Jakarta Selatan"
        registrationViewModel.handleEffortDoneEditText(solution)
        assertEquals(solution, registrationViewModel.mutableLiveData.value?.effortDone.toString())
    }

    @Test
    fun givenInvalidEffortDone_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val solution = "        "
        registrationViewModel.handleEffortDoneEditText(solution)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errEffortDone.get())
    }

    @Test
    fun givenValidPlaceConsulted_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val placeConsulted = "placeConsulted"
        registrationViewModel.handlePlaceConsultedEditText(placeConsulted)
        assertEquals(placeConsulted, registrationViewModel.mutableLiveData.value?.placeConsulted.toString())
    }

    @Test
    fun givenInvalidPlaceConsulted_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val placeConsulted = "        "
        registrationViewModel.handlePlaceConsultedEditText(placeConsulted)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errPlaceConsulted.get())
    }

    @Test
    fun givenHasConsultedBeforeWithValidData_shouldReturnFifthPageIsValid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleHasConsultedBeforeRadioButton(true)
        registrationViewModel.handlePlaceConsultedEditText("place")
        registrationViewModel.handleDateConsultedEditText(1.toString(), FormType.MONTH_CONSULTED.typeName)
        registrationViewModel.handleDateConsultedEditText(1.toString(), FormType.YEAR_CONSULTED.typeName)
        registrationViewModel.handleComplaintEditText("complaint")
        registrationViewModel.handleSolutionEditText("solution")
        registrationViewModel.handleProblemEditText("problem")
        registrationViewModel.handleEffortDoneEditText("effortDone")
        assertTrue(registrationViewModel.fifthPageIsValid())
    }

    @Test
    fun givenHasConsultedBeforeWithInvalidData_shouldReturnFifthPageIsInvalid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleHasConsultedBeforeRadioButton(true)
        registrationViewModel.handlePlaceConsultedEditText("place")
        registrationViewModel.handleDateConsultedEditText(1.toString(), FormType.MONTH_CONSULTED.typeName)
        registrationViewModel.handleDateConsultedEditText(1.toString(), FormType.YEAR_CONSULTED.typeName)
        registrationViewModel.handleProblemEditText("problem")
        registrationViewModel.handleEffortDoneEditText("effortDone")
        assertFalse(registrationViewModel.fifthPageIsValid())
    }

    fun givenHasNotConsultedBeforeWithValidData_shouldReturnFifthPageIsValid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleHasConsultedBeforeRadioButton(false)
        registrationViewModel.handleProblemEditText("problem")
        registrationViewModel.handleEffortDoneEditText("effortDone")
        assertTrue(registrationViewModel.fifthPageIsValid())
    }

    fun givenHasNotConsultedBeforeWithInvalidData_shouldReturnFifthPageIsInvalid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleHasConsultedBeforeRadioButton(false)
        assertFalse(registrationViewModel.fifthPageIsValid())
    }

    fun givenInvalidData_shouldReturnFifthPageIsInvalid() {
        shadowOf(Looper.getMainLooper()).idle()
        assertFalse(registrationViewModel.fifthPageIsValid())
    }

    @Test
    fun givenValidData_whenPostingRegistration_shouldUpdateLiveDataTo200() {
        shadowOf(Looper.getMainLooper()).idle()

        val registration = Registration()
        `when`(registrationService.registerUser(registration, "token")).thenReturn(basicCall)
        registrationViewModel.mutableLiveData.value = registration
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(BasicResponse(200)))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        registrationViewModel.postRegistrationData("token")

        verify(registrationService, times(1)).registerUser(registration, "token")
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        assertEquals(ResponseType.SUCCESS.code, registrationViewModel.postRegistrationResponseCode.value)
    }

    @Test
    fun givenInvalidData_whenPostingRegistration_shouldUpdateLiveDataToErrorCode() {
        shadowOf(Looper.getMainLooper()).idle()

        val registration = Registration()
        `when`(registrationService.registerUser(registration, "not-token")).thenReturn(basicCall)
        registrationViewModel.mutableLiveData.value = registration
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)

            callback.onResponse(basicCall, Response.success(BasicResponse(ResponseType.BAD_REQUEST.code)))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))

        registrationViewModel.postRegistrationData("not-token")
        verify(registrationService, times(1)).registerUser(registration, "not-token")
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))

        assertEquals(ResponseType.BAD_REQUEST.code, registrationViewModel.postRegistrationResponseCode.value)
    }

    @Test
    fun givenValidIpipKeyAndAnswer_whenHandlingIpipAnswer_shouldUpdateLiveData() {
        registrationViewModel.handleIpipQuestion(IpipQuestion.QUESTION_1.question, 2)
        val answer = registrationViewModel.getIpipAnswer(IpipQuestion.QUESTION_1.question)
        assertEquals(answer, 2)
    }

    @Test
    fun givenInvalidIpipKey_whenGettingIpipValue_shouldReturnNull() {
        val answer = registrationViewModel.getIpipAnswer("invalid")
        assertEquals(answer, null)
    }

    @Test
    fun givenValidSrqKeyAndAnswer_whenHandlingSrqAnswer_shouldUpdateLiveData() {
        registrationViewModel.handleSrqQuestion(SrqQuestion.QUESTION_1.question, 1)
        val answer = registrationViewModel.getSrqAnswer(SrqQuestion.QUESTION_1.question)
        assertEquals(1, answer!!)
    }

    @Test
    fun givenInvalidSrqKey_whenGettingSrqValue_shouldReturnNull() {
        val answer = registrationViewModel.getSrqAnswer("invalid")
        assertEquals(answer, -1)
    }

    @Test
    fun givenValidToken_whenPostingIpipAndSrqData_shouldUpdateLiveData() {
        val response = BasicResponse(200, "OK")
        `when`(
            registrationService.postIpipAndSrqSurvey(
                any(String::class.java),
                any(IpipAndSrqRequest::class.java)
            )
        ).thenReturn(basicCall)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        registrationViewModel.postIpipAndSrqData("token")

        verify(registrationService, times(1))
            .postIpipAndSrqSurvey(any(String::class.java), any(IpipAndSrqRequest::class.java))
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        assertEquals(200, registrationViewModel.postIpipAndSrqStatusCode.value)
    }
}