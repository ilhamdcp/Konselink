package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository.ClientRepository
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
class ClientViewModelTest {
    private lateinit var clientViewModel: ClientViewModel
    private lateinit var activity: MainActivity
    private lateinit var clientService: ClientService
    private lateinit var call: Call<ClientRequestListResponse>
    private lateinit var basicResponseCall: Call<BasicResponse>
    private lateinit var clientDetailCall: Call<ClientDetailResponse>
    private lateinit var db: ApplicationDatabase
    private lateinit var ipipCall: Call<IpipResponse>
    private lateinit var srqCall: Call<SrqResponse>


    @Before
    fun setUp() {
        activity = buildActivity(MainActivity::class.java).create().get()
        clientService = mock(ClientService::class.java)
        db = mock(ApplicationDatabase::class.java)
        clientViewModel = ViewModelProvider(activity, ClientViewModelFactory(activity, clientService, db)).get(ClientViewModel::class.java)

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
    fun givenValidParameter_whenRetrievingClientRequestList_shouldUpdateLiveData() {
        val client = ClientRequest(name = "counselor-1", clientId = 1)
        val response = ClientRequestListResponse(200, 1, 2, listOf(client))
        `when`(clientService.retrieveClientRequestList("token", 1, 2, null)).thenReturn(call)

        doAnswer {
            val callback: Callback<ClientRequestListResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(response))
        }
            .`when`(call).enqueue(any(ClientRepository.ClientRequestListCallbackHandler::class.java))

        clientViewModel.getClientRequestList("token", 1, 2)

        verify(clientService, times(1))
            .retrieveClientRequestList("token", 1, 2, null)
        verify(call, times(1))
            .enqueue(any(ClientRepository.ClientRequestListCallbackHandler::class.java))
        val updatedLiveData = clientViewModel.clientRequestListLiveData.value?.get(0)
        val currentPage = clientViewModel.currentPageLiveData
        val totalPage = clientViewModel.totalPageLiveData
        assertThat(updatedLiveData?.name).isEqualTo(client.name)
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

        clientViewModel.approveClientRequest("token", 1)

        verify(clientService, times(1)).approveClientRequest("token", 1)
        verify(basicResponseCall, times(1)).enqueue(any(ClientRepository.ClientRequestUpdateCallbackHandler::class.java))
        val statusCode = clientViewModel.statusCode
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

        clientViewModel.rejectClientRequest("token", 1)

        verify(clientService, times(1)).rejectClientRequest("token", 1)
        verify(basicResponseCall, times(1)).enqueue(any(ClientRepository.ClientRequestUpdateCallbackHandler::class.java))
        val statusCode = clientViewModel.statusCode
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

        clientViewModel.rejectClientRequest("token", 1)
        val statusCode = clientViewModel.statusCode
        verify(clientService, times(1)).rejectClientRequest("token", 1)
        verify(basicResponseCall, times(1)).enqueue(any(ClientRepository.ClientRequestUpdateCallbackHandler::class.java))
        assertThat(statusCode.value).isEqualTo(200)

        clientViewModel.resetStatusCode()
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
        clientViewModel.getClientDetail("token", 1)

        verify(clientService, times(1)).retrieveClientDetail("token", 1)
        verify(clientDetailCall, times(1)).enqueue(any(ClientRepository.ClientDetailCallbackHandler::class.java))
        assertThat(clientViewModel.clientDetailLiveData.value?.name).isEqualTo("Setsuna F. Seiei")
    }

    @Test
    fun givenValidTokenAndClientId_whenRetrevingSrqSurvey_shouldUpdateLiveData() {
        val response = SrqResponse(200, Srq(true))
        `when`(clientService.retrieveSrqSurvey(anyString(), anyInt(), anyString())).thenReturn(srqCall)
        doAnswer {
            val callback: Callback<SrqResponse> = it.getArgument(0)
            callback.onResponse(srqCall, Response.success(response))
        }.`when`(srqCall).enqueue(any(ClientRepository.SrqSurveyCallbackHandler::class.java))
        clientViewModel.getSrqSurvey("token", 1)

        verify(clientService, times(1)).retrieveSrqSurvey(anyString(), anyInt(), anyString())
        verify(srqCall, times(1)).enqueue(any(ClientRepository.SrqSurveyCallbackHandler::class.java))
        assertThat(clientViewModel.srqLiveData.value?.cautionResult).isTrue()
    }

    @Test
    fun givenValidTokenAndClientId_whenRetrevingIpipSurvey_shouldUpdateLiveData() {
        val response = IpipResponse(200, Ipip(5,5,5,5,5))
        `when`(clientService.retrieveIpipSurvey(anyString(), anyInt(), anyString())).thenReturn(ipipCall)
        doAnswer {
            val callback: Callback<IpipResponse> = it.getArgument(0)
            callback.onResponse(ipipCall, Response.success(response))
        }.`when`(ipipCall).enqueue(any(ClientRepository.IpipSurveyCallbackHandler::class.java))
        clientViewModel.getIpipSurvey("token", 1)

        verify(clientService, times(1)).retrieveIpipSurvey(anyString(), anyInt(), anyString())
        verify(ipipCall, times(1)).enqueue(any(ClientRepository.IpipSurveyCallbackHandler::class.java))
        assertThat(clientViewModel.ipipLiveData.value?.extraversion).isEqualTo(5)
    }
}