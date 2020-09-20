package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.Chat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.ChatHistoryListResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ResponseType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.SymptomVisibility
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository.ConsultationRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository.UserRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ConsultationViewModelTest {
    private lateinit var consultationViewModelFactory: ConsultationViewModelFactory
    private lateinit var consultationViewModel: ConsultationViewModel
    private lateinit var activity: MainActivity
    private lateinit var consultationService: ConsultationService
    private lateinit var db: ApplicationDatabase
    private lateinit var call: Call<ConsultationScheduleListResponse>
    private lateinit var basicCall: Call<BasicResponse>
    private lateinit var upcomingCall: Call<UpcomingConsultationListResponse>
    private lateinit var ongoingCall: Call<OngoingConsultationResponse>
    private lateinit var icdDiagnosisCall: Call<DiagnosisCodeListResponse>
    private lateinit var preConsultationCall: Call<PreConsultationSurveyListResponse>
    private lateinit var clientRecordCall: Call<ClientRecordListResponse>
    private lateinit var chatHistoryCall: Call<ChatHistoryListResponse>

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        consultationService = mock(ConsultationService::class.java)
        db = Room.inMemoryDatabaseBuilder(activity, ApplicationDatabase::class.java)
            .allowMainThreadQueries().build()
        consultationViewModelFactory =
            ConsultationViewModelFactory(activity, consultationService, db)
        consultationViewModel = ViewModelProvider(
            activity,
            ConsultationViewModelFactory(activity, consultationService, db)
        ).get(ConsultationViewModel::class.java)

        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<ConsultationScheduleListResponse>

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>

        @Suppress("UNCHECKED_CAST")
        upcomingCall = mock(Call::class.java) as Call<UpcomingConsultationListResponse>

        @Suppress("UNCHECKED_CAST")
        ongoingCall = mock(Call::class.java) as Call<OngoingConsultationResponse>
        
        @Suppress("UNCHECKED_CAST")
        icdDiagnosisCall = mock(Call::class.java) as Call<DiagnosisCodeListResponse>

        @Suppress("UNCHECKED_CAST")
        preConsultationCall = mock(Call::class.java) as Call<PreConsultationSurveyListResponse>

        @Suppress("UNCHECKED_CAST")
        clientRecordCall = mock(Call::class.java) as Call<ClientRecordListResponse>

        @Suppress("UNCHECKED_CAST")
        chatHistoryCall = mock(Call::class.java) as Call<ChatHistoryListResponse>

    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(call, basicCall, upcomingCall, ongoingCall, preConsultationCall, clientRecordCall)
    }

    @Test
    fun givenValidParameter_whenCallingGetConsultationSchedule_shouldUpdateLiveData() {
        val date1 = Date()
        val date2 = Date()
        val consultationScheduleListResponse = ConsultationScheduleListResponse(
            200, listOf(
                ConsultationSchedule(day = date1.toString(), session = listOf(ConsultationSession())),
                ConsultationSchedule(day = date2.toString(), session = listOf(ConsultationSession()))
            )
        )
        `when`(consultationService.retrieveConsultationSchedule("token", 1, 2004)).thenReturn(call)
        doAnswer {
            val callback: Callback<ConsultationScheduleListResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(consultationScheduleListResponse))
        }.`when`(call).enqueue(any(ConsultationRepository.ScheduleListCallbackHandler::class.java))

        consultationViewModel.getConsultationSchedule("token", 1, 2004)
        verify(consultationService, times(1)).retrieveConsultationSchedule("token", 1, 2004)
        verify(
            call,
            times(1)
        ).enqueue(any(ConsultationRepository.ScheduleListCallbackHandler::class.java))

        assertThat(consultationViewModel.consultationScheduleList.value?.size).isEqualTo(2)
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Test
    fun givenUninitializedAttribute_whenCheckingPageValidity_shouldReturnFalse() {
        assertThat(consultationViewModel.newSchedulePageIsValid()).isFalse()
    }

    @Test
    fun givenInitializedAttribute_whenCheckingPageValidity_shouldReturnTrue() {
        consultationViewModel.startDateString.value = "date"
        consultationViewModel.endDateString.value = "date"
        consultationViewModel.updateWorkDays(0, true)
        consultationViewModel.startSessionTime.value = "start"
        consultationViewModel.sessionNum.value = 3

        assertThat(consultationViewModel.newSchedulePageIsValid()).isTrue()
    }

    @Test
    fun givenTrueBooleanAndValidIndex_whenUpdatingWorkDays_shouldUpdateLiveData() {
        consultationViewModel.updateWorkDays(2, true)
        assertThat(consultationViewModel.workDays.value?.get(2)).isTrue()
    }

    @Test
    fun givenAllFalseWorkDays_whenGeneratingWorkDaysString_shouldReturnEmptyString() {
        assertThat(consultationViewModel.generateWorkDays()).isEmpty()
    }

    @Test
    fun givenExistTrueWorkDays_whenGeneratingWorkDaysString_shouldReturnNonEmptyString() {
        consultationViewModel.updateWorkDays(2, true)
        assertThat(consultationViewModel.generateWorkDays()).isEqualTo("3")
    }

    @Test
    fun givenValidTokenAndNewRequestSchedule_whenPostingScheduleData_shouldUpdateStatusCodeLiveData() {
        consultationViewModel.startDateString.value = "date"
        consultationViewModel.endDateString.value = "date"
        consultationViewModel.updateWorkDays(0, true)
        consultationViewModel.startSessionTime.value = "start"
        consultationViewModel.sessionNum.value = 3

        val response = BasicResponse(ResponseType.SUCCESS.code, "success")

        `when`(consultationService.postNewScheduleData(ArgumentMatchers.anyString(), any(NewScheduleRequest::class.java))).thenReturn(basicCall)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))

        consultationViewModel.postNewScheduleData("token")
        verify(consultationService, times(1)).postNewScheduleData(ArgumentMatchers.anyString(), any(NewScheduleRequest::class.java))
        verify(
            basicCall,
            times(1)
        ).enqueue(any(UserRepository.BasicCallbackHandler::class.java))

        assertThat(consultationViewModel.statusCode.value).isEqualTo(ResponseType.SUCCESS.code)
    }


    @Test
    fun givenValidToken_whenRetrievingOngoingSchedule_shouldUpdateLiveData() {
        val response = OngoingConsultationResponse(200, ConsultationSession(scheduleId = 99, clientName = "Macross"))
        `when`(consultationService.retrieveOngoingSchedule("token")).thenReturn(ongoingCall)
        doAnswer {
            val callback: Callback<OngoingConsultationResponse> = it.getArgument(0)
            callback.onResponse(ongoingCall, Response.success(response))
        }.`when`(ongoingCall).enqueue(Mockito.any(ConsultationRepository.OngoingConsultationCallbackHandler::class.java))

        consultationViewModel.getOngoingSchedule("token")
        verify(ongoingCall, times(1))
            .enqueue(Mockito.any(ConsultationRepository.OngoingConsultationCallbackHandler::class.java))
        verify(consultationService, times(1)).retrieveOngoingSchedule("token")

        assertThat(consultationViewModel.ongoingScheduleLiveData.value?.clientName).isEqualTo("Macross")
    }

    @Test
    fun givenValidToken_whenRetrievingUpcomingSchedule_shouldUpdateLiveData() {
        val response = UpcomingConsultationListResponse(200, listOf(ConsultationSession(scheduleId = 99, clientName = "Macross")))
        `when`(consultationService.retrieveUpcomingSchedule("token", 1 , 1)).thenReturn(upcomingCall)
        doAnswer {
            val callback: Callback<UpcomingConsultationListResponse> = it.getArgument(0)
            callback.onResponse(upcomingCall, Response.success(response))
        }.`when`(upcomingCall).enqueue(Mockito.any(ConsultationRepository.UpcomingConsultationCallbackHandler::class.java))

        consultationViewModel.getUpcomingSchedule("token", 1, 1)
        verify(upcomingCall, times(1))
            .enqueue(Mockito.any(ConsultationRepository.UpcomingConsultationCallbackHandler::class.java))
        verify(consultationService, times(1)).retrieveUpcomingSchedule("token", 1 , 1)

        assertThat(consultationViewModel.upcomingScheduleLiveData.value?.clientName).isEqualTo("Macross")
    }
    
    @Test
    fun givenValidParameter_whenRetrievingDiagnosisCode_shouldUpdateLiveData() {
        val response = DiagnosisCodeListResponse(200, listOf(IcdDiagnosisCode(codeId = 123)))
        `when`(consultationService.retrieveIcdDiagnosis("token", 1, 1, "keyword")).thenReturn(icdDiagnosisCall)
        doAnswer {
            val callback: Callback<DiagnosisCodeListResponse> = it.getArgument(0)
            callback.onResponse(icdDiagnosisCall, Response.success(response))
        }.`when`(icdDiagnosisCall).enqueue(any(ConsultationRepository.IcdDiagnosisCallbackHandler::class.java))
        consultationViewModel.getIcdDiagnosis("token", 1, 1, "keyword")
        verify(consultationService, times(1)).retrieveIcdDiagnosis("token", 1, 1, "keyword")
        verify(icdDiagnosisCall, times(1)).enqueue(any(ConsultationRepository.IcdDiagnosisCallbackHandler::class.java))

        assertThat(consultationViewModel.icdDiagnosisListLiveData.value?.get(0)?.codeId).isEqualTo(123)

    }

    @Test
    fun givenValidDiagnosis_whenHandlingDiagnosisEditText_shouldUpdateLiveDataValue() {
        val diagnosis = "diagnosis"
        consultationViewModel.handleDiagnosisEditText(diagnosis)
        assertThat(consultationViewModel.clientRecordLiveData.value?.diagnosis).isEqualTo("diagnosis")
    }

    @Test
    fun givenInvalidDiagnosis_whenHandlingDiagnosisEditText_shouldSetLiveDataValueToNull() {
        val diagnosis = ""
        consultationViewModel.handleDiagnosisEditText(diagnosis)
        assertThat(consultationViewModel.clientRecordLiveData.value?.diagnosis).isNull()
    }

    @Test
    fun givenValidProblemDescription_whenHandlingProblemDescriptionEditText_shouldUpdateLiveDataValue() {
        val problem = "problem"
        consultationViewModel.handleProblemDescriptionEditText(problem)
        assertThat(consultationViewModel.clientRecordLiveData.value?.problemDescription).isEqualTo("problem")
    }

    @Test
    fun givenInvalidProblemDescriptions_whenHandlingProblemDescriptionEditText_shouldSetLiveDataValueToNull() {
        val problem = ""
        consultationViewModel.handleProblemDescriptionEditText(problem)
        assertThat(consultationViewModel.clientRecordLiveData.value?.diagnosis).isNull()
    }

    @Test
    fun givenValidDiagnosisCode_whenHandlingDiagnosisCodeEditText_shouldUpdateLiveDataValue() {
        val diagnosisCode = "123"
        consultationViewModel.handleDiagnosisCodeEditText(diagnosisCode)
        assertThat(consultationViewModel.clientRecordLiveData.value?.diagnosisCode).isEqualTo(123)
    }

    @Test
    fun givenInvalidDiagnosisCode_whenHandlingDiagnosisCodeEditText_shouldSetLiveDataValueToNull() {
        val diagnosis = ""
        consultationViewModel.handleDiagnosisCodeEditText(diagnosis)
        assertThat(consultationViewModel.clientRecordLiveData.value?.diagnosisCode).isNull()
    }

    @Test
    fun givenValidPhysicalHealthHistory_whenHandlingPhysicalHealthHistoryEditText_shouldUpdateLiveDataValue() {
        val physicalHealthHistory = "physicalHealthHistory"
        consultationViewModel.handlePhysicalHealthHistoryEditText(physicalHealthHistory)
        assertThat(consultationViewModel.clientRecordLiveData.value?.physicalHealthHistory).isEqualTo("physicalHealthHistory")
    }

    @Test
    fun givenInvalidPhysicalHealthHistory_whenHandlingPhysicalHealthHistoryEditText_shouldSetLiveDataValueToNull() {
        val physicalHealthHistory = ""
        consultationViewModel.handlePhysicalHealthHistoryEditText(physicalHealthHistory)
        assertThat(consultationViewModel.clientRecordLiveData.value?.physicalHealthHistory).isNull()
    }

    @Test
    fun givenValidMedicalConsumption_whenHandlingMedicalConsumptionEditText_shouldUpdateLiveDataValue() {
        val medicalConsumption = "medicalConsumption"
        consultationViewModel.handleMedicalConsumptionEditText(medicalConsumption)
        assertThat(consultationViewModel.clientRecordLiveData.value?.medicalConsumption).isEqualTo("medicalConsumption")
    }

    @Test
    fun givenInvalidMedicalConsumption_whenHandlingMedicalConsumptionEditText_shouldSetLiveDataValueToNull() {
        val medicalConsumption = ""
        consultationViewModel.handleMedicalConsumptionEditText(medicalConsumption)
        assertThat(consultationViewModel.clientRecordLiveData.value?.medicalConsumption).isNull()
    }

    @Test
    fun givenValidAssessment_whenHandlingAssessmentEditText_shouldUpdateLiveDataValue() {
        val assessment = "assessment"
        consultationViewModel.handleAssessmentEditText(assessment)
        assertThat(consultationViewModel.clientRecordLiveData.value?.assessment).isEqualTo("assessment")
    }

    @Test
    fun givenInvalidAssessment_whenHandlingAssessmentEditText_shouldSetLiveDataValueToNull() {
        val assessment = ""
        consultationViewModel.handleAssessmentEditText(assessment)
        assertThat(consultationViewModel.clientRecordLiveData.value?.assessment).isNull()
    }

    @Test
    fun givenValidConsultationPurpose_whenHandlingConsultationPurposeEditText_shouldUpdateLiveDataValue() {
        val consultationPurpose = "consultationPurpose"
        consultationViewModel.handleConsultationPurposeEditText(consultationPurpose)
        assertThat(consultationViewModel.clientRecordLiveData.value?.consultationPurpose).isEqualTo("consultationPurpose")
    }

    @Test
    fun givenInvalidConsultationPurpose_whenHandlingConsultationPurposeEditText_shouldSetLiveDataValueToNull() {
        val consultationPurpose = ""
        consultationViewModel.handleConsultationPurposeEditText(consultationPurpose)
        assertThat(consultationViewModel.clientRecordLiveData.value?.consultationPurpose).isNull()
    }

    @Test
    fun givenValidTreatmentPlan_whenHandlingTreatmentPlanEditText_shouldUpdateLiveDataValue() {
        val treatmentPlan = "treatmentPlan"
        consultationViewModel.handleTreatmentPlanEditText(treatmentPlan)
        assertThat(consultationViewModel.clientRecordLiveData.value?.treatmentPlan).isEqualTo("treatmentPlan")
    }

    @Test
    fun givenInvalidTreatmentPlan_whenHandlingTreatmentPlanEditText_shouldSetLiveDataValueToNull() {
        val treatmentPlan = ""
        consultationViewModel.handleTreatmentPlanEditText(treatmentPlan)
        assertThat(consultationViewModel.clientRecordLiveData.value?.treatmentPlan).isNull()
    }

    @Test
    fun givenValidMeetings_whenHandlingMeetingsEditText_shouldUpdateLiveDataValue() {
        val meetings = "123"
        consultationViewModel.handleMeetingsEditText(meetings)
        assertThat(consultationViewModel.clientRecordLiveData.value?.meetings).isEqualTo(123)
    }

    @Test
    fun givenInvalidMeetings_whenHandlingMeetingsEditText_shouldSetLiveDataValueToNull() {
        val meetings = ""
        consultationViewModel.handleMeetingsEditText(meetings)
        assertThat(consultationViewModel.clientRecordLiveData.value?.meetings).isNull()
    }

    @Test
    fun givenValidSelfHarm_whenHandlingSelfHarmSpinner_shouldUpdateLiveDataValue() {
        val selfHarm = SymptomVisibility.IN_MIND.level
        consultationViewModel.handleSelfHarmSpinner(selfHarm)
        assertThat(consultationViewModel.clientRecordLiveData.value?.selfHarmRisk).isEqualTo(SymptomVisibility.IN_MIND.level)
    }

    @Test
    fun givenInvalidSelfHarm_whenHandlingSelfHarmSpinner_shouldSetLiveDataValueToNull() {
        val selfHarm = ""
        consultationViewModel.handleSelfHarmSpinner(selfHarm)
        assertThat(consultationViewModel.clientRecordLiveData.value?.selfHarmRisk).isNull()
    }

    @Test
    fun givenValidSuicide_whenHandlingSuicideSpinner_shouldUpdateLiveDataValue() {
        val suicide = SymptomVisibility.IN_MIND.level
        consultationViewModel.handleSuicideSpinner(suicide)
        assertThat(consultationViewModel.clientRecordLiveData.value?.suicideRisk).isEqualTo(SymptomVisibility.IN_MIND.level)
    }

    @Test
    fun givenInvalidSuicide_whenHandlingSuicideSpinner_shouldSetLiveDataValueToNull() {
        val suicide = ""
        consultationViewModel.handleSuicideSpinner(suicide)
        assertThat(consultationViewModel.clientRecordLiveData.value?.suicideRisk).isNull()
    }

    @Test
    fun givenValidOthersHarm_whenHandlingOthersHarmSpinner_shouldUpdateLiveDataValue() {
        val othersHarm = SymptomVisibility.IN_MIND.level
        consultationViewModel.handleOthersHarmSpinner(othersHarm)
        assertThat(consultationViewModel.clientRecordLiveData.value?.othersHarmRisk).isEqualTo(SymptomVisibility.IN_MIND.level)
    }

    @Test
    fun givenInvalidOthersHarm_whenHandlingOthersHarmSpinner_shouldSetLiveDataValueToNull() {
        val othersHarm = ""
        consultationViewModel.handleOthersHarmSpinner(othersHarm)
        assertThat(consultationViewModel.clientRecordLiveData.value?.othersHarmRisk).isNull()
    }

    @Test
    fun givenValidNotes_whenHandlingMeetingsEditText_shouldUpdateLiveDataValue() {
        val notes = "notes"
        consultationViewModel.handleNotesEditText(notes)
        assertThat(consultationViewModel.clientRecordLiveData.value?.notes).isEqualTo("notes")
    }

    @Test
    fun givenInvalidNotes_whenHandlingNotesEditText_shouldSetLiveDataValueToNull() {
        val notes = ""
        consultationViewModel.handleNotesEditText(notes)
        assertThat(consultationViewModel.clientRecordLiveData.value?.notes).isNull()
    }

    @Test
    fun givenBlankClientRecord_whenValidatingPostConsultationPage_shouldReturnFalse() {
        assertThat(consultationViewModel.postConsultationPageIsValid()).isFalse()
    }

    @Test
    fun givenValidClientRecord_whenValidatingPostConsultationPage_shouldReturnTrue() {
        consultationViewModel.handleDiagnosisEditText("diagnosis")
        consultationViewModel.handleDiagnosisCodeEditText("123")
        consultationViewModel.handleAssessmentEditText("assessment")
        consultationViewModel.handleTreatmentPlanEditText("treatment")
        consultationViewModel.handleMeetingsEditText("123")
        consultationViewModel.handleConsultationPurposeEditText("consultationPurpose")
        consultationViewModel.handlePhysicalHealthHistoryEditText("physicalHealthHistory")
        consultationViewModel.handleMedicalConsumptionEditText("medicalConsumption")
        consultationViewModel.handleSelfHarmSpinner(SymptomVisibility.IN_MIND.level)
        consultationViewModel.handleSuicideSpinner(SymptomVisibility.IN_MIND.level)
        consultationViewModel.handleOthersHarmSpinner(SymptomVisibility.IN_MIND.level)

        assertThat(consultationViewModel.postConsultationPageIsValid()).isTrue()
    }

    @Test
    fun givenValidClientRecordWithProblemDescription_whenValidatingPostConsultationPage_shouldReturnTrue() {
        consultationViewModel.useDiagnosis.value = false
        consultationViewModel.handleProblemDescriptionEditText("problemDescription")
        consultationViewModel.handleAssessmentEditText("assessment")
        consultationViewModel.handleTreatmentPlanEditText("treatment")
        consultationViewModel.handleMeetingsEditText("123")
        consultationViewModel.handleConsultationPurposeEditText("consultationPurpose")
        consultationViewModel.handlePhysicalHealthHistoryEditText("physicalHealthHistory")
        consultationViewModel.handleMedicalConsumptionEditText("medicalConsumption")
        consultationViewModel.handleSelfHarmSpinner(SymptomVisibility.IN_MIND.level)
        consultationViewModel.handleSuicideSpinner(SymptomVisibility.IN_MIND.level)
        consultationViewModel.handleOthersHarmSpinner(SymptomVisibility.IN_MIND.level)

        assertThat(consultationViewModel.postConsultationPageIsValid()).isTrue()
    }

    @Test
    fun givenValidTokenAndScheduleId_whenRetrievingPreConsultationSurvey_shouldUpdateLiveData() {
        val response = PreConsultationSurveyListResponse(200, listOf(PreConsultationSurvey(answerKey = "key", answerValue = 3)))
        `when`(consultationService.retrievePreConsultationSurvey("token", 1)).thenReturn(preConsultationCall)
        doAnswer {
            val callback: Callback<PreConsultationSurveyListResponse> = it.getArgument(0)
            callback.onResponse(preConsultationCall, Response.success(response))
        }.`when`(preConsultationCall).enqueue(any(ConsultationRepository.PreConsultationSurveyCallbackHandler::class.java))
        consultationViewModel.getPreConsultationSurvey("token", 1)
        verify(consultationService, times(1)).retrievePreConsultationSurvey("token", 1)
        verify(preConsultationCall, times(1)).enqueue(any(ConsultationRepository.PreConsultationSurveyCallbackHandler::class.java))

        assertThat(consultationViewModel.preConsultationSurveyListLiveData.value?.get(0)?.answerKey).isEqualTo("key")
    }

    @Test
    fun givenValidToken_whenRetrievingClientRecord_shouldUpdateLiveData() {
        val response = ClientRecordListResponse(200, listOf(ClientRecord(scheduleId = 123)))
        `when`(consultationService.retrieveClientRecord("token", 1)).thenReturn(clientRecordCall)
        doAnswer {
            val callback: Callback<ClientRecordListResponse> = it.getArgument(0)
            callback.onResponse(clientRecordCall, Response.success(response))
        }.`when`(clientRecordCall).enqueue(any(ConsultationRepository.ClientRecordListCallbackHandler::class.java))

        consultationViewModel.getClientRecord("token", 1)

        verify(consultationService, times(1)).retrieveClientRecord("token", 1)
        verify(clientRecordCall, times(1)).enqueue(any(ConsultationRepository.ClientRecordListCallbackHandler::class.java))

        assertThat(consultationViewModel.clientRecordListLiveData.value?.get(0)?.scheduleId).isEqualTo(123)
    }

    @Test
    fun givenValidTokenAndScheduleId_whenDeletingSchedule_shouldUpdateLiveData() {
        val response = BasicResponse(200, "OK")
        `when`(consultationService.deleteScheduleData("token", 1)).thenReturn(basicCall)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        consultationViewModel.deleteScheduleData("token", 1)

        verify(consultationService, times(1)).deleteScheduleData("token", 1)
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        assertThat(consultationViewModel.deleteScheduleStatusCodeLiveData.value).isEqualTo(200)
    }

    @Test
    fun givenValidParameter_whenRetrievingChatHistory_shouldUpdateChatLiveData() {
        val response = ChatHistoryListResponse(200, listOf(Chat(scheduleId = 1, message = "chat")))
        `when`(consultationService.retrieveChatHistory(anyString(), anyInt(), anyString())).thenReturn(chatHistoryCall)
        doAnswer{
            val callback: Callback<ChatHistoryListResponse> = it.getArgument(0)
            callback.onResponse(chatHistoryCall, Response.success(response))
        }.`when`(chatHistoryCall).enqueue(any(ConsultationRepository.ChatHistoryCallbackHandler::class.java))

        consultationViewModel.getChatHistory("token", 1, Date())
        verify(chatHistoryCall, times(1)).enqueue(any(ConsultationRepository.ChatHistoryCallbackHandler::class.java))
        verify(consultationService, times(1)).retrieveChatHistory(anyString(), anyInt(), anyString())

        assertThat(consultationViewModel.chatListLiveData.value?.size).isEqualTo(1)
    }
}