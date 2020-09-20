package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.IpipAndSrqRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Registration
import junit.framework.Assert.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RegistrationServiceTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockWebServer = MockWebServer()
    private val successResponse = "{\"code\":200, \"message\": \"Success\"}"
    private val errorResponse = "{\"code\":400, \"message\": \"Bad Request\"}"
    private lateinit var mockResponse: MockResponse
    private lateinit var registrationService: RegistrationService
    private lateinit var mockRegistrationService: RegistrationService
    private lateinit var basicCall: Call<BasicResponse>

    @Before
    fun setUp() {
        val gson = GsonBuilder().setLenient().create()
        registrationService = Retrofit.Builder()
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(RegistrationService::class.java)
        mockRegistrationService = mock(RegistrationService::class.java)

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        verifyNoMoreInteractions(mockRegistrationService, basicCall)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Test
    fun givenValidData_whenCallingRegistrationService_shouldReturnSuccessResponse() {
        mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(successResponse)
            .setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = registrationService.registerUser(Registration(name = "name"), "token").execute()
        mockWebServer.takeRequest()
        assertEquals("Success", response.body()?.message)
    }

    @Test
    fun givenInvalidData_whenCallingRegistrationService_shouldReturn400Response() {
        mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody(successResponse)
            .setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = registrationService.registerUser(Registration(name = null), "invalidToken").execute()
        mockWebServer.takeRequest()
        assertEquals(400, response.code())
    }

    @Test
    fun whenCallingCreateMethod_shouldReturnRegistrationServiceInstance() {
        val registrations = RegistrationService.create()
        assertTrue("", registrations is RegistrationService)
    }

    @Test
    fun givenValidTokenAndRequest_whenPostingIpipAndSrq_shouldReturnResponse() {
        `when`(mockRegistrationService.postIpipAndSrqSurvey(any(String::class.java), any(IpipAndSrqRequest::class.java))).thenReturn(basicCall)
        mockRegistrationService.postIpipAndSrqSurvey("token", IpipAndSrqRequest()).execute()

        verify(mockRegistrationService, times(1)).postIpipAndSrqSurvey(any(String::class.java), any(IpipAndSrqRequest::class.java))
        verify(basicCall, times(1)).execute()
    }

}