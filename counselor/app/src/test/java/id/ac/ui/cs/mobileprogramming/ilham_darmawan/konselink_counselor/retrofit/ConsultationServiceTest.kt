package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.ChatHistoryListResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ConsultationServiceTest {
    private val lisOfScheduleResponse = "{'code': 200, 'schedule': [{'day': '28/04/2020', 'time': [{'scheduleId': 1, 'time': '09:00-10:30', 'clientId': 3}]}]}"
    private lateinit var mockConsultationService: ConsultationService
    private lateinit var consultationService: ConsultationService
    private lateinit var call: Call<ConsultationScheduleListResponse>
    private lateinit var upcomingCall: Call<UpcomingConsultationListResponse>
    private lateinit var ongoingCall: Call<OngoingConsultationResponse>
    private lateinit var basicCall: Call<BasicResponse>
    private lateinit var diagnosisCodeCall: Call<DiagnosisCodeListResponse>
    private lateinit var preConsultationCall: Call<PreConsultationSurveyListResponse>
    private lateinit var clientRecordCall: Call<ClientRecordListResponse>
    private lateinit var chatHistoryCall: Call<ChatHistoryListResponse>
    private lateinit var mockResponse: MockResponse
    private lateinit var gson: Gson
    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        gson = GsonBuilder().setLenient().setDateFormat("dd/MM/yyyy").create()
        consultationService = Retrofit.Builder()
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(ConsultationService::class.java)
        mockConsultationService = mock(ConsultationService::class.java)

        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<ConsultationScheduleListResponse>

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>

        @Suppress("UNCHECKED_CAST")
        upcomingCall = mock(Call::class.java) as Call<UpcomingConsultationListResponse>

        @Suppress("UNCHECKED_CAST")
        ongoingCall = mock(Call::class.java) as Call<OngoingConsultationResponse>

        @Suppress("UNCHECKED_CAST")
        diagnosisCodeCall = mock(Call::class.java) as Call<DiagnosisCodeListResponse>

        @Suppress("UNCHECKED_CAST")
        preConsultationCall = mock(Call::class.java) as Call<PreConsultationSurveyListResponse>

        @Suppress("UNCHECKED_CAST")
        clientRecordCall = mock(Call::class.java) as Call<ClientRecordListResponse>

        @Suppress("UNCHECKED_CAST")
        chatHistoryCall = mock(Call::class.java) as Call<ChatHistoryListResponse>
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        verifyNoMoreInteractions(mockConsultationService, call, basicCall, upcomingCall, ongoingCall, diagnosisCodeCall, preConsultationCall, clientRecordCall)
    }

    @Test
    fun givenValidParameter_whenRetrievingConsultationSchedule_shouldReturnMockedCall() {
        `when`(mockConsultationService.retrieveConsultationSchedule("token", 4, 2004)).thenReturn(call)
        mockConsultationService.retrieveConsultationSchedule("token", 4, 2004).execute()
        verify(mockConsultationService, times(1)).retrieveConsultationSchedule("token", 4, 2004)
        verify(call, times(1)).execute()
    }

    @Test
    fun givenValidMethodCall_whenInstantiatingConsultationService_shouldReturnConsultationServiceInstance() {
        assertThat(ConsultationService.create()::class.java).isAssignableTo(ConsultationService::class.java)
    }

    @Test
    fun givenValidParameter_whenRetrievingConsultationSchedule_shouldReturnResponse() {
        mockResponse = MockResponse().setResponseCode(200).setBody(lisOfScheduleResponse).setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = consultationService.retrieveConsultationSchedule("token", 4, 2004).execute()
        mockWebServer.takeRequest()

        assertThat(response.body()?.code).isEqualTo(200)
        assertThat(response.body()?.consultationSchedule?.size).isEqualTo(1)
        assertThat(response.body()?.consultationSchedule?.get(0)?.session?.get(0)?.time).isEqualTo("09:00-10:30")
    }

    @Test
    fun givenValidParameter_whenPostingNewScheduleData_shouldReturnBasicMockedCall() {
        val newScheduleRequest = NewScheduleRequest("8/4/2020", "9/4/2020", 1, "1,2,4,5", "09:00", 5)
        `when`(mockConsultationService.postNewScheduleData("token", newScheduleRequest)).thenReturn(basicCall)
        mockConsultationService.postNewScheduleData("token", newScheduleRequest).execute()

        verify(mockConsultationService, times(1)).postNewScheduleData("token", newScheduleRequest)
        verify(basicCall, times(1)).execute()
    }

    @Test
    fun givenValidToken_whenRetrievingUpcomingSchedule_shouldReturnResponse() {
        `when`(mockConsultationService.retrieveUpcomingSchedule("token", 1 , 1)).thenReturn(upcomingCall)
        mockConsultationService.retrieveUpcomingSchedule("token", 1 , 1).execute()
        verify(mockConsultationService, times(1)).retrieveUpcomingSchedule("token", 1 , 1)
        verify(upcomingCall, times(1)).execute()
    }

    @Test
    fun givenValidToken_whenRetrievingOngoingSchedule_shouldReturnResponse() {
        `when`(mockConsultationService.retrieveOngoingSchedule("token")).thenReturn(ongoingCall)
        mockConsultationService.retrieveOngoingSchedule("token").execute()
        verify(mockConsultationService, times(1)).retrieveOngoingSchedule("token")
        verify(ongoingCall, times(1)).execute()
    }

    @Test
    fun givenValidParameter_whenRetrievingDiagnosisCode_shouldReturnResponse() {
        `when`(mockConsultationService.retrieveIcdDiagnosis("token", 1, 1, "keyword")).thenReturn(diagnosisCodeCall)
        mockConsultationService.retrieveIcdDiagnosis("token", 1, 1, "keyword").execute()
        verify(mockConsultationService, times(1)).retrieveIcdDiagnosis("token", 1, 1, "keyword")
        verify(diagnosisCodeCall, times(1)).execute()
    }

    @Test
    fun givenValidTokenAndScheduleId_whenRetrievingPreConsultationSurvey_shouldReturnResponse() {
        `when`(mockConsultationService.retrievePreConsultationSurvey("token", 1)).thenReturn(preConsultationCall)
        mockConsultationService.retrievePreConsultationSurvey("token", 1).execute()
        verify(mockConsultationService, times(1)).retrievePreConsultationSurvey("token", 1)
        verify(preConsultationCall, times(1)).execute()
    }

    @Test
    fun givenValidToken_whenRetrievingClientRecord_shouldReturnResponse() {
        `when`(mockConsultationService.retrieveClientRecord("token", 1)).thenReturn(clientRecordCall)
        mockConsultationService.retrieveClientRecord("token", 1).execute()
        verify(mockConsultationService, times(1)).retrieveClientRecord("token", 1)
        verify(clientRecordCall, times(1)).execute()
    }

    @Test
    fun givenValidTokenAndScheduleId_whenDeletingSchedule_shouldReturnBasicResponse() {
        `when`(mockConsultationService.deleteScheduleData("token", 1)).thenReturn(basicCall)
        mockConsultationService.deleteScheduleData("token", 1).execute()
        verify(mockConsultationService, times(1)).deleteScheduleData("token", 1)
        verify(basicCall, times(1)).execute()
    }

    @Test
    fun givenValidParameter_whenRetrievingChatHistory_shouldReturnResponse() {
        `when`(mockConsultationService.retrieveChatHistory(anyString(), anyInt(), anyString())).thenReturn(chatHistoryCall)
        mockConsultationService.retrieveChatHistory("token", 1, "timestamp").execute()
        verify(mockConsultationService, times(1)).retrieveChatHistory(anyString(), anyInt(), anyString())
        verify(chatHistoryCall, times(1)).execute()
    }
}