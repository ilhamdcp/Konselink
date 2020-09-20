package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository

import android.os.Build
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.ConsultationService
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
    private lateinit var consultationService: ConsultationService
    private lateinit var consultationRepository: ConsultationRepository
    private lateinit var db: ApplicationDatabase
    private lateinit var activity: MainActivity
    private lateinit var ongoingCall: Call<OngoingConsultationResponse>
    private lateinit var upcomingCall: Call<UpcomingConsultationListResponse>
    private lateinit var chatHistoryCall: Call<ChatHistoryListResponse>
    private lateinit var basicCall: Call<BasicResponse>

    @Before
    fun setUp() {
        consultationService = mock(ConsultationService::class.java)
        activity = buildActivity(MainActivity::class.java).create().get()
        db = mock(ApplicationDatabase::class.java)
        consultationRepository = ConsultationRepository(activity, consultationService, db)

        @Suppress("UNCHECKED_CAST")
        ongoingCall = mock(Call::class.java) as Call<OngoingConsultationResponse>

        @Suppress("UNCHECKED_CAST")
        upcomingCall = mock(Call::class.java) as Call<UpcomingConsultationListResponse>

        @Suppress("UNCHECKED_CAST")
        chatHistoryCall = mock(Call::class.java) as Call<ChatHistoryListResponse>

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(consultationService, db, ongoingCall, upcomingCall, chatHistoryCall)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Test
    fun givenValidToken_whenRetrievingUpcomingSchedule_shouldUpdateLiveData() {
        val response = UpcomingConsultationListResponse(200, listOf(ConsultationSession(scheduleId = 99, counselorName = "Macross")))
        `when`(consultationService.retrieveUpcomingSchedule("token", 1 , 1)).thenReturn(upcomingCall)
        doAnswer {
            val callback: Callback<UpcomingConsultationListResponse> = it.getArgument(0)
            callback.onResponse(upcomingCall, Response.success(response))
        }.`when`(upcomingCall).enqueue(any(ConsultationRepository.UpcomingConsultationCallbackHandler::class.java))

        consultationRepository.getUpcomingSchedule("token", 1, 1)
        verify(upcomingCall, times(1)).enqueue(any(ConsultationRepository.UpcomingConsultationCallbackHandler::class.java))
        verify(consultationService, times(1)).retrieveUpcomingSchedule("token", 1 , 1)
        verify(db, times(1)).consultationDao()

        assertThat(consultationRepository.upcomingScheduleLiveData.value?.counselorName).isEqualTo("Macross")
    }

    @Test
    fun givenValidToken_whenRetrievingOngoingSchedule_shouldUpdateLiveData() {
        val response = OngoingConsultationResponse(200, ConsultationSession(scheduleId = 99, counselorName = "Macross"))
        `when`(consultationService.retrieveOngoingSchedule("token")).thenReturn(ongoingCall)
        doAnswer {
            val callback: Callback<OngoingConsultationResponse> = it.getArgument(0)
            callback.onResponse(ongoingCall, Response.success(response))
        }.`when`(ongoingCall).enqueue(any(ConsultationRepository.OngoingConsultationCallbackHandler::class.java))

        consultationRepository.getOngoingSchedule("token")
        verify(ongoingCall, times(1)).enqueue(any(ConsultationRepository.OngoingConsultationCallbackHandler::class.java))
        verify(consultationService, times(1)).retrieveOngoingSchedule("token")
        verify(db, times(1)).consultationDao()

        assertThat(consultationRepository.ongoingScheduleLiveData.value?.counselorName).isEqualTo("Macross")
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
        verify(db, times(1)).consultationDao()

        assertThat(consultationRepository.chatListLiveData.value?.size).isEqualTo(1)
    }

    @Test
    fun givenValidTokenAndValidPreConsultationSurvey_whenPostingPreConsultationSurvey_shouldUpdateStatusCodeLiveData() {
        val response = BasicResponse(200)
        `when`(consultationService.postPreConsultationSurvey(anyString(), any(PreConsultationSurveyRequest::class.java))).thenReturn(basicCall)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        consultationRepository.postPreConsultationSurvey("token", PreConsultationSurveyRequest())
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        verify(consultationService, times(1)).postPreConsultationSurvey(anyString(), any(PreConsultationSurveyRequest::class.java))
        verify(db, times(1)).consultationDao()

        assertThat(consultationRepository.postPreConsultationCodeLiveData.value).isEqualTo(200)
    }
}