package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository

import android.os.Build
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.Chat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.ChatHistoryListResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ResponseType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import org.junit.After
import org.junit.Before
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
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ConsultationRepositoryTest {
    private lateinit var consultationRepository: ConsultationRepository
    private lateinit var consultationService: ConsultationService
    private lateinit var activity: MainActivity
    private lateinit var db: ApplicationDatabase
    private lateinit var call: Call<ConsultationScheduleListResponse>
    private lateinit var basicCall: Call<BasicResponse>
    private lateinit var upcomingCall: Call<UpcomingConsultationListResponse>
    private lateinit var ongoingCall: Call<OngoingConsultationResponse>
    private lateinit var diagnosisCodeCall: Call<DiagnosisCodeListResponse>
    private lateinit var preConsultationCall: Call<PreConsultationSurveyListResponse>
    private lateinit var chatHistoryCall: Call<ChatHistoryListResponse>
    private lateinit var clientRecordCall: Call<ClientRecordListResponse>

    @Before
    fun setUp() {
        consultationService = mock(ConsultationService::class.java)
        activity = buildActivity(MainActivity::class.java).create().get()
        db = Room.inMemoryDatabaseBuilder(activity, ApplicationDatabase::class.java).allowMainThreadQueries().build()
        consultationRepository = ConsultationRepository(activity, consultationService, db)
        db.clearAllTables()

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
        db.close()
        verifyNoMoreInteractions(consultationService, call, basicCall, upcomingCall, ongoingCall, diagnosisCodeCall, preConsultationCall, clientRecordCall)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Test
    fun givenValidParameter_whenRetrievingScheduleList_shouldUpdateLiveData() {
        val date1 = Date()
        val date2 = Date()
        val consultationScheduleListResponse = ConsultationScheduleListResponse(200, listOf(
            ConsultationSchedule(day = date1.toString(), session = listOf(ConsultationSession())),
            ConsultationSchedule(day = date2.toString(), session = listOf(ConsultationSession()))
        ))
        `when`(consultationService.retrieveConsultationSchedule("token", 4, 2004)).thenReturn(call)
        doAnswer {
            val callback: Callback<ConsultationScheduleListResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(consultationScheduleListResponse))
        }.`when`(call).enqueue(any(ConsultationRepository.ScheduleListCallbackHandler::class.java))
        consultationRepository.retrieveConsultationScheduleList("token", 4, 2004)
        val liveData = consultationRepository.consultationScheduleListLiveData

        verify(call, times(1)).enqueue(any(ConsultationRepository.ScheduleListCallbackHandler::class.java))
        verify(consultationService, times(1)).retrieveConsultationSchedule("token", 4, 2004)
        assertThat(liveData.value?.size).isEqualTo(2)
    }

    @Test
    fun givenValidParameter_whenPostingNewSchedule_shouldUpdateLiveData() {
        val newScheduleRequest = NewScheduleRequest("8/4/2020", "9/4/2020", 1, "1,2,4,5", "09:00", 5)
        val basicResponse = BasicResponse(200, "success")

        `when`(consultationService.postNewScheduleData(anyString(), any(NewScheduleRequest::class.java))).thenReturn(basicCall)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(basicResponse))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))

        consultationRepository.postNewScheduleData("token", "8/4/2020", "9/4/2020", 1, "1,2,4,5", "09:00", 5)


        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        verify(consultationService, times(1)).postNewScheduleData(anyString(), any())
        assertThat(consultationRepository.statusCode.value).isEqualTo(ResponseType.SUCCESS.code)
    }


    @Test
    fun givenValidToken_whenRetrievingUpcomingSchedule_shouldUpdateLiveData() {
        val response = UpcomingConsultationListResponse(200, listOf(ConsultationSession(scheduleId = 99, clientName = "Macross")))
        `when`(consultationService.retrieveUpcomingSchedule("token", 1 , 1)).thenReturn(upcomingCall)
        doAnswer {
            val callback: Callback<UpcomingConsultationListResponse> = it.getArgument(0)
            callback.onResponse(upcomingCall, Response.success(response))
        }.`when`(upcomingCall).enqueue(any(ConsultationRepository.UpcomingConsultationCallbackHandler::class.java))

        consultationRepository.getUpcomingSchedule("token", 1, 1)
        verify(upcomingCall, times(1)).enqueue(any(ConsultationRepository.UpcomingConsultationCallbackHandler::class.java))
        verify(consultationService, times(1)).retrieveUpcomingSchedule("token", 1 , 1)

        assertThat(consultationRepository.upcomingScheduleLiveData.value?.clientName).isEqualTo("Macross")
    }

    @Test
    fun givenValidToken_whenRetrievingOngoingSchedule_shouldUpdateLiveData() {
        val response = OngoingConsultationResponse(200, ConsultationSession(scheduleId = 99, clientName = "Macross"))
        `when`(consultationService.retrieveOngoingSchedule("token")).thenReturn(ongoingCall)
        doAnswer {
            val callback: Callback<OngoingConsultationResponse> = it.getArgument(0)
            callback.onResponse(ongoingCall, Response.success(response))
        }.`when`(ongoingCall).enqueue(any(ConsultationRepository.OngoingConsultationCallbackHandler::class.java))

        consultationRepository.getOngoingSchedule("token")
        verify(ongoingCall, times(1)).enqueue(any(ConsultationRepository.OngoingConsultationCallbackHandler::class.java))
        verify(consultationService, times(1)).retrieveOngoingSchedule("token")

        assertThat(consultationRepository.ongoingScheduleLiveData.value?.clientName).isEqualTo("Macross")
    }

    @Test
    fun givenValidParameter_whenRetrievingDiagnosisCode_shouldUpdateLiveData() {
        val response = DiagnosisCodeListResponse(200, listOf(IcdDiagnosisCode(codeId = 123)))
        `when`(consultationService.retrieveIcdDiagnosis("token", 1, 1, "keyword")).thenReturn(diagnosisCodeCall)
        doAnswer {
            val callback: Callback<DiagnosisCodeListResponse> = it.getArgument(0)
            callback.onResponse(diagnosisCodeCall, Response.success(response))
        }.`when`(diagnosisCodeCall).enqueue(any(ConsultationRepository.IcdDiagnosisCallbackHandler::class.java))
        consultationRepository.retrieveIcdDiagnosis("token", 1, 1, "keyword")
        verify(consultationService, times(1)).retrieveIcdDiagnosis("token", 1, 1, "keyword")
        verify(diagnosisCodeCall, times(1)).enqueue(any(ConsultationRepository.IcdDiagnosisCallbackHandler::class.java))

        assertThat(consultationRepository.icdDiagnosisListLiveData.value?.get(0)?.codeId).isEqualTo(123)

    }

    @Test
    fun givenValidTokenAndScheduleId_whenRetrievingPreConsultationSurvey_shouldUpdateLiveData() {
        val response = PreConsultationSurveyListResponse(200, listOf(PreConsultationSurvey(answerKey = "key", answerValue = 3)))
        `when`(consultationService.retrievePreConsultationSurvey("token", 1)).thenReturn(preConsultationCall)
        doAnswer {
            val callback: Callback<PreConsultationSurveyListResponse> = it.getArgument(0)
            callback.onResponse(preConsultationCall, Response.success(response))
        }.`when`(preConsultationCall).enqueue(any(ConsultationRepository.PreConsultationSurveyCallbackHandler::class.java))
        consultationRepository.retrievePreConsultationSurvey("token", 1)
        verify(consultationService, times(1)).retrievePreConsultationSurvey("token", 1)
        verify(preConsultationCall, times(1)).enqueue(any(ConsultationRepository.PreConsultationSurveyCallbackHandler::class.java))

        assertThat(consultationRepository.preConsultationSurveyListLiveData.value?.get(0)?.answerKey).isEqualTo("key")
    }

    @Test
    fun givenValidToken_whenRetrievingClientRecord_shouldUpdateLiveData() {
        val response = ClientRecordListResponse(200, listOf(ClientRecord(scheduleId = 123)))
        `when`(consultationService.retrieveClientRecord("token", 1)).thenReturn(clientRecordCall)
        doAnswer {
            val callback: Callback<ClientRecordListResponse> = it.getArgument(0)
            callback.onResponse(clientRecordCall, Response.success(response))
        }.`when`(clientRecordCall).enqueue(any(ConsultationRepository.ClientRecordListCallbackHandler::class.java))

        consultationRepository.retrieveClientRecord("token", 1)

        verify(consultationService, times(1)).retrieveClientRecord("token", 1)
        verify(clientRecordCall, times(1)).enqueue(any(ConsultationRepository.ClientRecordListCallbackHandler::class.java))

        assertThat(consultationRepository.clientRecordListLiveData.value?.get(0)?.scheduleId).isEqualTo(123)
    }

    @Test
    fun givenValidTokenAndScheduleId_whenDeletingSchedule_shouldUpdateLiveData() {
        val response = BasicResponse(200, "OK")
        `when`(consultationService.deleteScheduleData("token", 1)).thenReturn(basicCall)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        consultationRepository.deleteScheduleData("token", 1)

        verify(consultationService, times(1)).deleteScheduleData("token", 1)
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        assertThat(consultationRepository.deleteScheduleStatusCodeLiveData.value).isEqualTo(200)
    }

    @Test
    fun givenValidParameter_whenRetrievingChatHistory_shouldUpdateChatLiveData() {
        val response = ChatHistoryListResponse(200, listOf(Chat(scheduleId = 1, message = "chat")))
        `when`(consultationService.retrieveChatHistory(anyString(), anyInt(), anyString())).thenReturn(chatHistoryCall)
        doAnswer{
            val callback: Callback<ChatHistoryListResponse> = it.getArgument(0)
            callback.onResponse(chatHistoryCall, Response.success(response))
        }.`when`(chatHistoryCall).enqueue(any(ConsultationRepository.ChatHistoryCallbackHandler::class.java))

        consultationRepository.retrieveNewChat("token", 1, "timestamp")
        verify(chatHistoryCall, times(1)).enqueue(any(ConsultationRepository.ChatHistoryCallbackHandler::class.java))
        verify(consultationService, times(1)).retrieveChatHistory(anyString(), anyInt(), anyString())

        assertThat(consultationRepository.chatListLiveData.value?.size).isEqualTo(1)
    }
}