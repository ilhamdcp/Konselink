package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit

import android.os.Build
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ConsultationServiceTest {
    private lateinit var ongoingCall: Call<OngoingConsultationResponse>
    private lateinit var upcomingCall: Call<UpcomingConsultationListResponse>
    private lateinit var chatHistoryCall: Call<ChatHistoryListResponse>
    private lateinit var basicCall: Call<BasicResponse>
    private lateinit var consultationService: ConsultationService

    @Before
    fun setUp() {
        @Suppress("UNCHECKED_CAST")
        ongoingCall = mock(Call::class.java) as Call<OngoingConsultationResponse>

        @Suppress("UNCHECKED_CAST")
        upcomingCall = mock(Call::class.java) as Call<UpcomingConsultationListResponse>

        @Suppress("UNCHECKED_CAST")
        chatHistoryCall = mock(Call::class.java) as Call<ChatHistoryListResponse>

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>
        consultationService = mock(ConsultationService::class.java)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @After
    fun tearDown() {
        verifyNoMoreInteractions(ongoingCall, consultationService, upcomingCall, chatHistoryCall, basicCall)
    }

    @Test
    fun whenCreatingConsultationService_shouldReturnInstance() {
        assertThat(ConsultationService.create()::class.java).isAssignableTo(ConsultationService::class.java)
    }

    @Test
    fun givenValidToken_whenRetrievingUpcomingSchedule_shouldReturnResponse() {
        `when`(consultationService.retrieveUpcomingSchedule("token", 1 , 1)).thenReturn(upcomingCall)
        consultationService.retrieveUpcomingSchedule("token", 1 , 1).execute()
        verify(consultationService, times(1)).retrieveUpcomingSchedule("token", 1 , 1)
        verify(upcomingCall, times(1)).execute()
    }

    @Test
    fun givenValidToken_whenRetrievingOngoingSchedule_shouldReturnResponse() {
        `when`(consultationService.retrieveOngoingSchedule("token")).thenReturn(ongoingCall)
        consultationService.retrieveOngoingSchedule("token").execute()
        verify(consultationService, times(1)).retrieveOngoingSchedule("token")
        verify(ongoingCall, times(1)).execute()
    }

    @Test
    fun givenValidParameter_whenRetrievingChatHistory_shouldReturnResponse() {
        `when`(consultationService.retrieveChatHistory(anyString(), anyInt(), anyString())).thenReturn(chatHistoryCall)
        consultationService.retrieveChatHistory("token", 1, "timestamp").execute()
        verify(consultationService, times(1)).retrieveChatHistory(anyString(), anyInt(), anyString())
        verify(chatHistoryCall, times(1)).execute()
    }

    @Test
    fun givenValidTokenAndPreConsultationData_whenPostingPreConsultationSurvey_shouldReturnResponse() {
        `when`(consultationService.postPreConsultationSurvey(anyString(), any(PreConsultationSurveyRequest::class.java))).thenReturn(basicCall)
        consultationService.postPreConsultationSurvey("token", PreConsultationSurveyRequest()).execute()
        verify(consultationService, times(1)).postPreConsultationSurvey(anyString(), any(PreConsultationSurveyRequest::class.java))
        verify(basicCall, times(1)).execute()
    }
}