package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.registration.RegistrationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.Registration
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.RegistrationService
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RegistrationRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var registrationService: RegistrationService
    private lateinit var activity: RegistrationActivity
    private lateinit var db: ApplicationDatabase
    private lateinit var registrationRepository: RegistrationRepository
    private lateinit var call: Call<BasicResponse>

    @Before
    fun setUp() {
        registrationService = mock(RegistrationService::class.java)
        activity = buildActivity(RegistrationActivity::class.java).create().get()
        db = Room.inMemoryDatabaseBuilder(activity, ApplicationDatabase::class.java).allowMainThreadQueries().build()

        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<BasicResponse>
        registrationRepository = RegistrationRepository(registrationService, db)
        db.clearAllTables()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(registrationService)
    }

    @Test
    fun givenInitializedRegistrationData_whenCallingGetRegistrationData_shouldReturnLiveData() {
        val registration = Registration(fullname = "fullname")
        db.registrationDao().insert(registration)
        registrationRepository.getRegistrationData().observeForever {
            assertThat(it.fullname).isEqualTo("fullname")
        }
    }

    @Test
        fun givenValidRegistrationData_whenInsertingRegistrationData_shouldIncreaseRowSize() {
        val registration = MutableLiveData(Registration(fullname = "fullname"))
        registrationRepository.insertRegistration(registration)

        val dbSize = db.registrationDao().getAllRegistrations()
        dbSize.observeForever {
            assertThat(it.size).isEqualTo(1)
        }
    }

    @Test
    fun givenValidParameter_whenPostRegistrationData_shouldUpdateRegistrationResultLiveData() {
        val registration = Registration(fullname = "fullname")
        val basicResponse = BasicResponse(code = 200, message = "succcess")

        `when`(registrationService.registerUser(registration, "token")).thenReturn(call)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(basicResponse))

        }.`when`(call)
            .enqueue(any(RegistrationRepository.PostRegistrationCallbackHandler::class.java))

        registrationRepository.postRegistrationData(registration, "token")

        verify(call, times(1)).enqueue(any(RegistrationRepository.PostRegistrationCallbackHandler::class.java))
        verify(registrationService, times(1)).registerUser(registration, "token")

        assertThat(registrationRepository._registrationResult.value).isEqualTo(200)
    }
}