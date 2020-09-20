package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.Registration
import junit.framework.Assert.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
class RegistrationServiceTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockWebServer = MockWebServer()
    private val successResponse = "{\"code\":200, \"message\": \"Success\"}"
    private val errorResponse = "{\"code\":400, \"message\": \"Bad Request\"}"
    private lateinit var mockResponse: MockResponse
    private lateinit var registrationService: RegistrationService

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
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun givenValidData_whenCallingRegistrationService_shouldReturnSuccessResponse() {
        mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(successResponse)
            .setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = registrationService.registerUser(Registration(fullname = "name"), "token").execute()
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

        val response = registrationService.registerUser(Registration(fullname = null), "invalidToken").execute()
        mockWebServer.takeRequest()
        assertEquals(400, response.code())
    }

    @Test
    fun whenCallingCreateMethod_shouldReturnRegistrationServiceInstance() {
        val registrations = RegistrationService.create()
        Assert.assertTrue(registrations is RegistrationService)
    }
}