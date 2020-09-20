package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration.RegistrationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.IpipAndSrqRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Registration
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RegistrationRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var registrationService: RegistrationService
    private lateinit var activity: RegistrationActivity
    private lateinit var db: ApplicationDatabase
    private lateinit var registrationRepository: RegistrationRepository
    private lateinit var basicCall: Call<BasicResponse>

    @Before
    fun setUp() {
        registrationService = mock(RegistrationService::class.java)
        activity = buildActivity(RegistrationActivity::class.java).create().get()
        db = Room.inMemoryDatabaseBuilder(activity, ApplicationDatabase::class.java).allowMainThreadQueries().build()
        registrationRepository = RegistrationRepository(activity, registrationService, db)

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(registrationService, basicCall)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    private fun <T> LiveData<T>.blockingObserve(): T? {
        var value: T? = null
        val latch = CountDownLatch(1)

        val observer = Observer<T> { t ->
            value = t
            latch.countDown()
        }

        observeForever(observer)

        latch.await(2, TimeUnit.SECONDS)
        return value
    }

    @Test
    fun givenValidTokenAndRegistrationData_whenPostingRegistrationData_shouldUpdateLiveData() {
        val response = BasicResponse(200, "OK")
        `when`(registrationService.registerUser(any(Registration::class.java), any(String::class.java))).thenReturn(basicCall)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        registrationRepository.postRegistrationData(Registration(), "token")

        verify(registrationService, times(1)).registerUser(any(Registration::class.java), any(String::class.java))
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        assertThat(registrationRepository.postRegistrationCode.value).isEqualTo(200)
    }

    @Test
    fun givenValidRegistration_whenInsertingRegistration_shouldIncreaseDbSize() {
        registrationRepository.insertRegistration(MutableLiveData(Registration(name = "Setsuna F. Seiei")))
        val result = db.registrationDao().getCurrentRegistration().blockingObserve()
        assertThat(result?.name).isEqualTo("Setsuna F. Seiei")
    }

    @Test
    fun givenValidTokenAndRequestBody_whenPositngIpipAndSrq_shouldUpdateLiveData() {
        val response = BasicResponse(200, "OK")
        `when`(registrationService.postIpipAndSrqSurvey(any(String::class.java), any(IpipAndSrqRequest::class.java))).thenReturn(basicCall)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        registrationRepository.postIpipAndSrqData("token", IpipAndSrqRequest())

        verify(registrationService, times(1)).postIpipAndSrqSurvey(any(String::class.java), any(IpipAndSrqRequest::class.java))
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        assertThat(registrationRepository.postIpipAndSrqStatusCode.value).isEqualTo(200)
    }
}