package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository

import android.os.Build
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.ClientDetailActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ClientService
import org.junit.After
import org.junit.Before
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
class ClientRepositoryTest {
    private lateinit var activity: ClientDetailActivity
    private lateinit var clientService: ClientService
    private lateinit var call: Call<ClientRequestListResponse>
    private lateinit var clientDetailCall: Call<ClientDetailResponse>
    private lateinit var basicResponseCall: Call<BasicResponse>
    private lateinit var db: ApplicationDatabase
    private lateinit var clientRepository: ClientRepository
    private lateinit var ipipCall: Call<IpipResponse>
    private lateinit var srqCall: Call<SrqResponse>

    @Before
    fun setUp() {
        activity = buildActivity(ClientDetailActivity::class.java).create().get()
        clientService = mock(ClientService::class.java)
        db = mock(ApplicationDatabase::class.java)
        clientRepository = ClientRepository(clientService, db, activity)

        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<ClientRequestListResponse>

        @Suppress("UNCHECKED_CAST")
        basicResponseCall = mock(Call::class.java) as Call<BasicResponse>

        @Suppress("UNCHECKED_CAST")
        clientDetailCall = mock(Call::class.java) as Call<ClientDetailResponse>

        @Suppress("UNCHECKED_CAST")
        ipipCall = mock(Call::class.java) as Call<IpipResponse>

        @Suppress("UNCHECKED_CAST")
        srqCall = mock(Call::class.java) as Call<SrqResponse>
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(clientService, call, basicResponseCall, clientDetailCall, ipipCall, srqCall)
    }

    @Test
    fun givenValidParameter_whenRetrievingCounselorList_shouldUpdateCounselorListLiveData() {
        val firstCounselor = ClientRequest(name = "client-1", displayPictureUrl = "url-1")
        val secondCounselor = ClientRequest(name = "client-2", displayPictureUrl = "url-2")
        val counselors = listOf(firstCounselor, secondCounselor)
        val counselorListResponse = ClientRequestListResponse(200, 1, 2, counselors)
        `when`(clientService.retrieveClientRequestList("token", 1, 2, null)).thenReturn(call)

        doAnswer {
            val callback: Callback<ClientRequestListResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(counselorListResponse))
        }.`when`(call).enqueue(any(ClientRepository.ClientRequestListCallbackHandler::class.java))

        clientRepository.getClientRequestList("token", 1, 2, null)

        verify(clientService, times(1)).retrieveClientRequestList("token", 1, 2, null)
        verify(call, times(1)).enqueue(any(ClientRepository.ClientRequestListCallbackHandler::class.java))
        val liveData = clientRepository.clientRequestListLiveData
        val currentPage = clientRepository.currentPage
        val totalPage = clientRepository.totalPage
        assertThat(liveData.value?.size).isEqualTo(2)
        assertThat(liveData.value?.get(0)?.name).isEqualTo("client-1")
        assertThat(currentPage.value).isEqualTo(1)
        assertThat(totalPage.value).isEqualTo(2)

    }

    @Test
    fun givenValidParameter_whenApprovingClientRequest_shouldUpdateCounselorListLiveData() {
        `when`(clientService.approveClientRequest("token", 1)).thenReturn(basicResponseCall)
        val postResponse = BasicResponse(200, "success")
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicResponseCall, Response.success(postResponse))
        }.`when`(basicResponseCall).enqueue(any(ClientRepository.ClientRequestUpdateCallbackHandler::class.java))

        clientRepository.approveClientRequest("token", 1)

        verify(clientService, times(1)).approveClientRequest("token", 1)
        verify(basicResponseCall, times(1)).enqueue(any(ClientRepository.ClientRequestUpdateCallbackHandler::class.java))
        val statusCode = clientRepository.statusCode
        assertThat(statusCode.value).isEqualTo(200)
    }

    @Test
    fun givenValidParameter_whenRejectingClientRequest_shouldUpdateCounselorListLiveData() {
        `when`(clientService.rejectClientRequest("token", 1)).thenReturn(basicResponseCall)
        val postResponse = BasicResponse(200, "success")
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicResponseCall, Response.success(postResponse))
        }.`when`(basicResponseCall).enqueue(any(ClientRepository.ClientRequestUpdateCallbackHandler::class.java))

        clientRepository.rejectClientRequest("token", 1)

        verify(clientService, times(1)).rejectClientRequest("token", 1)
        verify(basicResponseCall, times(1)).enqueue(any(ClientRepository.ClientRequestUpdateCallbackHandler::class.java))
        val statusCode = clientRepository.statusCode
        assertThat(statusCode.value).isEqualTo(200)
    }

    @Test
    fun whenResettingStatusCode_shouldUpdateStatusCode() {
        `when`(clientService.rejectClientRequest("token", 1)).thenReturn(basicResponseCall)
        val postResponse = BasicResponse(200, "success")
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicResponseCall, Response.success(postResponse))
        }.`when`(basicResponseCall).enqueue(any(ClientRepository.ClientRequestUpdateCallbackHandler::class.java))

        clientRepository.rejectClientRequest("token", 1)
        verify(clientService, times(1)).rejectClientRequest("token", 1)
        verify(basicResponseCall, times(1)).enqueue(any(ClientRepository.ClientRequestUpdateCallbackHandler::class.java))
        val statusCode = clientRepository.statusCode
        assertThat(statusCode.value).isEqualTo(200)
        clientRepository.resetStatusCode()
        assertThat(statusCode.value).isEqualTo(0)
    }

    @Test
    fun givenValidTokenAndClientId_whenRetrievingClientDetail_shouldUpdateLiveData() {
        val response = ClientDetailResponse(200, ClientDetail(name = "Setsuna F. Seiei"))
        `when`(clientService.retrieveClientDetail("token", 1)).thenReturn(clientDetailCall)
        doAnswer {
            val callback: Callback<ClientDetailResponse> = it.getArgument(0)
            callback.onResponse(clientDetailCall, Response.success(response))
        }.`when`(clientDetailCall).enqueue(any(ClientRepository.ClientDetailCallbackHandler::class.java))
        clientRepository.retrieveClientDetail("token", 1)

        verify(clientService, times(1)).retrieveClientDetail("token", 1)
        verify(clientDetailCall, times(1)).enqueue(any(ClientRepository.ClientDetailCallbackHandler::class.java))
        assertThat(clientRepository.clientDetailLiveData.value?.name).isEqualTo("Setsuna F. Seiei")
    }

    @Test
    fun givenValidTokenAndClientId_whenRetrevingSrqSurvey_shouldUpdateLiveData() {
        val response = SrqResponse(200, Srq(true))
        `when`(clientService.retrieveSrqSurvey(anyString(), anyInt(), anyString())).thenReturn(srqCall)
        doAnswer {
            val callback: Callback<SrqResponse> = it.getArgument(0)
            callback.onResponse(srqCall, Response.success(response))
        }.`when`(srqCall).enqueue(any(ClientRepository.SrqSurveyCallbackHandler::class.java))
        clientRepository.retrieveSrqSurvey("token", 1)

        verify(clientService, times(1)).retrieveSrqSurvey(anyString(), anyInt(), anyString())
        verify(srqCall, times(1)).enqueue(any(ClientRepository.SrqSurveyCallbackHandler::class.java))
        assertThat(clientRepository.srqLiveData.value?.cautionResult).isTrue()
    }

    @Test
    fun givenValidTokenAndClientId_whenRetrevingIpipSurvey_shouldUpdateLiveData() {
        val response = IpipResponse(200, Ipip(5,5,5,5,5))
        `when`(clientService.retrieveIpipSurvey(anyString(), anyInt(), anyString())).thenReturn(ipipCall)
        doAnswer {
            val callback: Callback<IpipResponse> = it.getArgument(0)
            callback.onResponse(ipipCall, Response.success(response))
        }.`when`(ipipCall).enqueue(any(ClientRepository.IpipSurveyCallbackHandler::class.java))
        clientRepository.retrieveIpipSurvey("token", 1)

        verify(clientService, times(1)).retrieveIpipSurvey(anyString(), anyInt(), anyString())
        verify(ipipCall, times(1)).enqueue(any(ClientRepository.IpipSurveyCallbackHandler::class.java))
        assertThat(clientRepository.ipipLiveData.value?.extraversion).isEqualTo(5)
    }
}