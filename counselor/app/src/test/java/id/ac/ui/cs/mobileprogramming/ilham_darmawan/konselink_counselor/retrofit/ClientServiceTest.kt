package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.ClientDetailResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.ClientRequestListResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.IpipResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.SrqResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ClientServiceTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var activity: MainActivity
    private lateinit var clientService: ClientService
    private lateinit var mockClientService: ClientService
    private lateinit var clientRequestCall: Call<ClientRequestListResponse>
    private val mockWebServer = MockWebServer()
    private val successResponse =
        "{'code':200, message: 'nice', data: [{'clientId': 1, 'name': 'Fullname, M.Psi', 'displayPictureUrl': 'www.displaypicture.com'}]}"
    private val errorResponse = "{'code':503, 'message': 'Bad Request'}"
    private val successPostResponse = "{'code':200, message: 'nice'}"
    private lateinit var mockResponse: MockResponse
    private lateinit var gson: Gson
    private lateinit var clientDetailCall: Call<ClientDetailResponse>
    private lateinit var srqCall: Call<SrqResponse>
    private lateinit var ipipCall: Call<IpipResponse>

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        mockClientService = mock(ClientService::class.java)
        gson = GsonBuilder().setLenient().create()
        clientService = Retrofit.Builder()
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(ClientService::class.java)

        @Suppress("UNCHECKED_CAST")
        clientRequestCall = mock(Call::class.java) as Call<ClientRequestListResponse>

        @Suppress("UNCHECKED_CAST")
        clientDetailCall = Mockito.mock(Call::class.java) as Call<ClientDetailResponse>

        @Suppress("UNCHECKED_CAST")
        ipipCall = mock(Call::class.java) as Call<IpipResponse>

        @Suppress("UNCHECKED_CAST")
        srqCall = mock(Call::class.java) as Call<SrqResponse>
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        verifyNoMoreInteractions(mockClientService, clientRequestCall, clientDetailCall, ipipCall, srqCall)
    }

    @Test
    fun givenValidRequestParameter_whenRetrievingCounselorList_shouldReturnMockedCall() {
        `when`(mockClientService.retrieveClientRequestList("token", 1, 10, null)).thenReturn(clientRequestCall)
        val response = mockClientService.retrieveClientRequestList("token", 1, 10, null)
        verify(mockClientService, times(1)).retrieveClientRequestList("token", 1, 10, null)
        Assert.assertEquals(clientRequestCall, response)
    }

    @Test
    fun givenValidRequestParameterAndNonEmptyClient_whenRetrievingCounselorList_shouldReturnCounselorList() {
        mockResponse = MockResponse().setResponseCode(200).setBody(successResponse).setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = clientService.retrieveClientRequestList("token", 1, 10, null).execute()
        mockWebServer.takeRequest()

        Assert.assertEquals(1, response.body()?.clientRequestList?.size)
    }

    @Test
    fun givenValidParameter_whenApprovingClientRequest_shouldReturnSuccess() {
        mockResponse = MockResponse().setResponseCode(200).setBody(successPostResponse).setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = clientService.approveClientRequest("token", 1).execute()
        mockWebServer.takeRequest()

        Assert.assertEquals(200, response.body()?.code)
    }

    @Test
    fun givenValidParameter_whenRejectingClientRequest_shouldReturnSuccess() {
        mockResponse = MockResponse().setResponseCode(200).setBody(successPostResponse).setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = clientService.rejectClientRequest("token", 1).execute()
        mockWebServer.takeRequest()

        Assert.assertEquals(200, response.body()?.code)
    }

    @Test
    fun givenInvalidRequestParameter_whenRetrievingCounselorList_shouldReturnErrorResponse() {
        mockResponse = MockResponse().setResponseCode(503).setBody(errorResponse).setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = clientService.retrieveClientRequestList("not-token", 1, 10, null).execute()
        mockWebServer.takeRequest()

        Assert.assertEquals(503, response.code())
    }


    @Test
    fun whenCallingCreateMethod_shouldReturnCounselorServiceInstance() {
        val clientServiceTest = ClientService.create()
        assertTrue(clientServiceTest is ClientService)
    }

    @Test
    fun givenValidTokenAndClientId_whenRetrievingClientDetail_shouldReturnResponse() {
        `when`(mockClientService.retrieveClientDetail("token", 1)).thenReturn(clientDetailCall)
        mockClientService.retrieveClientDetail("token", 1).execute()

        verify(mockClientService, times(1)).retrieveClientDetail("token", 1)
        verify(clientDetailCall, times(1)).execute()
    }

    @Test
    fun givenValidTokenAndClientId_whenRetrievingIpipData_shouldReturnResponse() {
        `when`(mockClientService.retrieveIpipSurvey("token", 1)).thenReturn(ipipCall)
        mockClientService.retrieveIpipSurvey("token", 1).execute()

        verify(mockClientService, times(1)).retrieveIpipSurvey("token", 1)
        verify(ipipCall, times(1)).execute()
    }

    @Test
    fun givenValidTokenAndClientId_whenRetrievingSrqData_shouldReturnResponse() {
        `when`(mockClientService.retrieveSrqSurvey("token", 1)).thenReturn(srqCall)
        mockClientService.retrieveSrqSurvey("token", 1).execute()

        verify(mockClientService, times(1)).retrieveSrqSurvey("token", 1)
        verify(srqCall, times(1)).execute()
    }
}