package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.CounselorListResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.RequestScheduleRequest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CounselorServiceTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var activity: MainActivity
    private lateinit var counselorService: CounselorService
    private lateinit var mockCounselorService: CounselorService
    private lateinit var call: Call<CounselorListResponse>
    private lateinit var basicCall: Call<BasicResponse>
    private val mockWebServer = MockWebServer()
    private val successResponse = "{'code':200, message: 'nice', data: [{'fullname': 'Fullname, M.Psi', 'specialization': 'Psikologi Industri & Organisasi', 'displayPictureUrl': 'www.displaypicture.com'}]}"
    private val counselorDetailSuccessResponse = "{'code': 200, 'data': {'counselorId': 1, 'fullName': 'Mr. Psikolog', 'specialization': 'Psikologi Pendidikan', 'strNumber': '123123123', 'sipNumber': '123123123', 'sspNumber': '123123123', 'displayPictureUrl': 'www.url.com'}}"
    private val errorResponse = "{'code':503, 'message': 'Bad Request'}"
    private lateinit var mockResponse: MockResponse
    private lateinit var gson: Gson

    @Before
    fun setUp() {
        activity = buildActivity(MainActivity::class.java).create().get()
        mockCounselorService = mock(CounselorService::class.java)
        gson = GsonBuilder().setLenient().create()
        counselorService = Retrofit.Builder()
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(CounselorService::class.java)

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>

        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<CounselorListResponse>
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        verifyNoMoreInteractions(basicCall, call, mockCounselorService)
    }

    @Test
    fun givenValidRequestParameter_whenRetrievingCounselorList_shouldReturnMockedCall() {
        `when`(mockCounselorService.retrieveCounselorList("token", 1, 10, null)).thenReturn(call)
        val response = mockCounselorService.retrieveCounselorList("token", 1, 10, null)
        verify(mockCounselorService, times(1)).retrieveCounselorList("token", 1, 10, null)
        assertEquals(call, response)
    }

    @Test
    fun givenValidRequestParameterAndNonEmptyCounselor_whenRetrievingCounselorList_shouldReturnCounselorList() {
        mockResponse = MockResponse().setResponseCode(200).setBody(successResponse).setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = counselorService.retrieveCounselorList("token", 1, 10, null).execute()
        mockWebServer.takeRequest()

        assertEquals(1, response.body()?.counselorList?.size)
    }

    @Test
    fun givenInvalidRequestParameter_whenRetrievingCounselorList_shouldReturnErrorResponse() {
        mockResponse = MockResponse().setResponseCode(503).setBody(errorResponse).setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = counselorService.retrieveCounselorList("not-token", 1, 10, null).execute()
        mockWebServer.takeRequest()

        assertEquals(503, response.code())
    }

    @Test
    fun givenValidRequestParameter_whenRetrievingCounselorDetail_shouldReturnCounselorDetailResponse() {
        mockResponse = MockResponse().setResponseCode(200).setBody(counselorDetailSuccessResponse).setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = counselorService.retrieveCounselorDetail("token", 1).execute()
        mockWebServer.takeRequest()

        assertEquals(200, response.code())
        assertEquals(1, response.body()?.counselorDetail?.counselorId)
    }


    @Test
    fun whenCallingCreateMethod_shouldReturnCounselorServiceInstance() {
        val counselorServiceTest = CounselorService.create()
        assertTrue(counselorServiceTest is CounselorService)
    }

    @Test
    fun givenValidParameter_whenRequestingSchedule_shouldReturnSuccessResponse() {
        `when`(mockCounselorService.requestSchedule(anyString(), anyInt())).thenReturn(basicCall)
        mockCounselorService.requestSchedule("token",1)
        verify(mockCounselorService, times(1)).requestSchedule(anyString(), anyInt())
    }
}