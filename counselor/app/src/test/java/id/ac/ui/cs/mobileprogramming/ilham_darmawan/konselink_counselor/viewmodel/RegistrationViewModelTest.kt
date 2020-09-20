package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.os.Build
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.registration.RegistrationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.Gender
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.RegistrationError
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.Specialization
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.Registration
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository.RegistrationRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.RegistrationService
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
    private val successResponse = "{\"code\":200, \"message\": \"Success\"}"
    private val errorResponse = "{\"code\":400, \"message\": \"Bad Request\"}"

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var registrationService: RegistrationService
    private lateinit var mockedCall: Call<BasicResponse>
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
        mockWebServer = MockWebServer()
        val gson = GsonBuilder().setLenient().create()
        registrationService = mock(RegistrationService::class.java)
        mockedCall= mock(Call::class.java) as Call<BasicResponse>
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
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun givenSuccessfulInsert_shouldIncreaseSize() {
        shadowOf(Looper.getMainLooper()).idle()
        val registration = Registration(fullname = "registration")
        registrationViewModel.mutableLiveData.value = registration
        registrationViewModel.saveCurrentRegistration()
        registrationViewModel.registrationLiveData.observeForever {
            assertEquals(registration.fullname, it.fullname)
        }
    }

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
        registrationViewModel.handleFullNameEditText(name)
        assertEquals(name, registrationViewModel.mutableLiveData.value?.fullname)
    }

    @Test
    fun givenBlankName_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val name = "    "
        registrationViewModel.handleFullNameEditText(name)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errFullName.get().toString())
    }

    @Test
    fun givenNameContainingSpecialCharOrNumber_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val name = "999,,"
        registrationViewModel.handleFullNameEditText(name)
        assertEquals(RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName, registrationViewModel.errFullName.get().toString())
    }

    @Test
    fun givenSelectedSpecializationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val specialization = Specialization.CLINICAL.specializationName
        registrationViewModel.handleSpecializationSpinner(specialization, 0)
        assertEquals(specialization, registrationViewModel.mutableLiveData.value?.specialization)
    }

    @Test
    fun givenNullSpecializationSpinnerItem_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val education = null
        registrationViewModel.handleSpecializationSpinner(education, 0)
        assertEquals(RegistrationError.CURRENT_EDUCATION_NOT_SELECTED.errName, registrationViewModel.errSpecialization.get())
    }

    @Test
    fun givenValidData_shouldReturnFirstPageIsValid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleFullNameEditText("fullname")
        registrationViewModel.handleGenderRadioButton(Gender.P.code)
        registrationViewModel.handleStrNumber("999/00/ini/str")
        registrationViewModel.handleStrPhotoPath("/file/photo/str.jpg")
        registrationViewModel.handleSpecializationSpinner(Specialization.CLINICAL.specializationName, 0)
        assertTrue(registrationViewModel.firstPageIsValid())
    }

    @Test
    fun givenUninitializedLiveData_shouldReturnFirstPageIsInvalid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.mutableLiveData.value = null
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
    fun givenValidStrNumber_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val strNumber = "999/00/ini/str"
        registrationViewModel.handleStrNumber(strNumber)
        assertEquals(strNumber, registrationViewModel.mutableLiveData.value?.strNumber)
    }

    @Test
    fun givenInvalidStrNumber_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val strNumber = "      "
        registrationViewModel.handleStrNumber(strNumber)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errStrNumber.get())
    }

    @Test
    fun givenValidSipNumber_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val sipNumber = "999/00/ini/sip"
        registrationViewModel.handleSipNumber(sipNumber)
        assertEquals(sipNumber, registrationViewModel.mutableLiveData.value?.sipNumber)
    }

    @Test
    fun givenInvalidSipNumber_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val sipNumber = "      "
        registrationViewModel.handleSipNumber(sipNumber)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSipNumber.get())
    }

    @Test
    fun givenValidSspNumber_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val sspNumber = "999/00/ini/ssp"
        registrationViewModel.handleSspNumber(sspNumber)
        assertEquals(sspNumber, registrationViewModel.mutableLiveData.value?.sspNumber)
    }

    @Test
    fun givenInvalidSspNumber_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val sspNumber = "      "
        registrationViewModel.handleSspNumber(sspNumber)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSspNumber.get())
    }

    @Test
    fun givenValidData_shouldReturnSecondPageIsValid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.handleSipNumber("999/00/ini/sip")
        registrationViewModel.handleSspNumber("999/00/ini/ssp")
        registrationViewModel.handleSipPhotoPath(("/path/sip/"))
        registrationViewModel.handleSspPhotoPath(("/path/ssp/"))
        assertTrue(registrationViewModel.secondPageIsValid())
    }

    @Test
    fun givenUninitializedLiveData_shouldReturnSecondPageIsInvalid() {
        shadowOf(Looper.getMainLooper()).idle()
        registrationViewModel.mutableLiveData.value = Registration()
        assertFalse(registrationViewModel.secondPageIsValid())
    }

    @Test
    fun givenValidStrPhotoPath_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val strPhotoPath = "/file/photo/str.jpg"
        registrationViewModel.handleStrPhotoPath(strPhotoPath)
        assertEquals(strPhotoPath, registrationViewModel.mutableLiveData.value?.strPhotoPath)
    }

    @Test
    fun givenBlankStrPhotoPath_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val strPhotoPath = "      "
        registrationViewModel.handleStrPhotoPath(strPhotoPath)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errStrPhotoPath.get())
    }

    @Test
    fun givenValidSipPhotoPath_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val sipPhotoPath = "/file/photo/sip.jpg"
        registrationViewModel.handleSipPhotoPath(sipPhotoPath)
        assertEquals(sipPhotoPath, registrationViewModel.mutableLiveData.value?.sipPhotoPath)
    }

    @Test
    fun givenBlankSipPhotoPath_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val sipPhotoPath = "      "
        registrationViewModel.handleSipPhotoPath(sipPhotoPath)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSipPhotoPath.get())
    }

    @Test
    fun givenValidSspPhotoPath_shouldUpdateViewModelLiveData() {
        shadowOf(Looper.getMainLooper()).idle()
        val sspPhotoPath = "/file/photo/ssp.jpg"
        registrationViewModel.handleSspPhotoPath(sspPhotoPath)
        assertEquals(sspPhotoPath, registrationViewModel.mutableLiveData.value?.sspPhotoPath)
    }

    @Test
    fun givenBlankSspPhotoPath_shouldUpdateViewModelErrAttribute() {
        shadowOf(Looper.getMainLooper()).idle()
        val sspPhotoPath = "      "
        registrationViewModel.handleSspPhotoPath(sspPhotoPath)
        assertEquals(RegistrationError.SHOULD_NOT_EMPTY.errName, registrationViewModel.errSspPhotoPath.get())
    }

    @Test
    fun givenValidData_whenPostingRegistration_shouldUpdateLiveDataTo200() {
        shadowOf(Looper.getMainLooper()).idle()

        val registration = Registration()
        `when`(registrationService.registerUser(registration, "token")).thenReturn(mockedCall)
        registrationViewModel.mutableLiveData.value = registration
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)

            callback.onResponse(mockedCall, Response.success(BasicResponse(200)))
        }.`when`(mockedCall).enqueue(any(RegistrationRepository.PostRegistrationCallbackHandler::class.java))

        registrationViewModel.postRegistrationData("token")

        assertEquals(200, registrationViewModel.postRegistrationResponseCode.value)
    }

    @Test
    fun givenInvalidData_whenPostingRegistration_shouldUpdateLiveDataToErrorCode() {
        shadowOf(Looper.getMainLooper()).idle()

        val registration = Registration()
        `when`(registrationService.registerUser(registration, "token")).thenReturn(mockedCall)
        registrationViewModel.mutableLiveData.value = registration
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)

            callback.onResponse(mockedCall, Response.success(BasicResponse(400)))
        }.`when`(mockedCall).enqueue(any(RegistrationRepository.PostRegistrationCallbackHandler::class.java))

        registrationViewModel.postRegistrationData("token")

        assertEquals(400, registrationViewModel.postRegistrationResponseCode.value)
    }
}