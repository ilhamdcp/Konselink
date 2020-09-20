package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.stfalcon.chatkito.utils.DateFormatter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.PanasQuestion
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.OptionSurvey
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.PreConsultationSurveyRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Consultation
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository.ConsultationRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.ConsultationService
import java.util.*
import kotlin.collections.HashMap

class ConsultationViewModel(
    context: Context,
    consultationService: ConsultationService,
    db: ApplicationDatabase
) : ViewModel() {
    private val consultationRepository = ConsultationRepository(context, consultationService, db)
    val ongoingScheduleLiveData = consultationRepository.ongoingScheduleLiveData
    val upcomingScheduleLiveData = consultationRepository.upcomingScheduleLiveData
    val upcomingScheduleListLiveData = consultationRepository.upcomingScheduleListLiveData
    val chatListLiveData = consultationRepository.chatListLiveData
    val ongoingStatusCodeLiveData = consultationRepository.ongoingStatusCodeLiveData
    val upcomingStatusCodeLiveData = consultationRepository.upcomingStatusCodeLiveData
    val postPreConsultationCodeLiveData = consultationRepository.postPreConsultationCodeLiveData

    var panasValues =
        HashMap<String, Int>(
            hashMapOf(
                PanasQuestion.QUESTION_2.question to -1,
                PanasQuestion.QUESTION_3.question to -1,
                PanasQuestion.QUESTION_4.question to -1,
                PanasQuestion.QUESTION_5.question to -1,
                PanasQuestion.QUESTION_6.question to -1,
                PanasQuestion.QUESTION_7.question to -1,
                PanasQuestion.QUESTION_8.question to -1,
                PanasQuestion.QUESTION_9.question to -1,
                PanasQuestion.QUESTION_10.question to -1,
                PanasQuestion.QUESTION_11.question to -1,
                PanasQuestion.QUESTION_12.question to -1,
                PanasQuestion.QUESTION_13.question to -1,
                PanasQuestion.QUESTION_14.question to -1,
                PanasQuestion.QUESTION_15.question to -1,
                PanasQuestion.QUESTION_16.question to -1,
                PanasQuestion.QUESTION_17.question to -1,
                PanasQuestion.QUESTION_18.question to -1,
                PanasQuestion.QUESTION_19.question to -1,
                PanasQuestion.QUESTION_20.question to -1)
        )

    fun getOngoingSchedule(token: String) {
        consultationRepository.getOngoingSchedule(token)
    }

    fun getUpcomingSchedule(token: String, entrySize: Int, pageNo: Int) {
        consultationRepository.getUpcomingSchedule(token, entrySize, pageNo)
    }

    fun getChatHistory(token: String, scheduleId: Int, timestamp: Date?) {
        val timestampString = if (DateFormatter.format(timestamp, "yyyy-MM-dd'T'HH:mm:ss.SSSZ").isNotBlank())
            DateFormatter.format(timestamp, "yyyy-MM-dd'T'HH:mm:ss.SSSZ") else null
        consultationRepository.retrieveNewChat(token, scheduleId, timestampString)
    }

    fun preConsultationPageIsValid(): Boolean {
        panasValues.entries.forEach {
            if (it.value == -1) {
                return false
            }
        }
        return true
    }

    fun postPreConsultationData(token: String, scheduleId: Int) {
       val surveys = ArrayList<OptionSurvey>()
        panasValues.entries.forEach {
            val survey = OptionSurvey(it.key, it.value)
            surveys.add(survey)
        }

        val preConsultationSurveyRequest = PreConsultationSurveyRequest(scheduleId, surveys)
        consultationRepository.postPreConsultationSurvey(token, preConsultationSurveyRequest)
    }

    fun getConsultation(scheduleId: Int): LiveData<Consultation> {
        return consultationRepository.getConsultation(scheduleId)
    }

    fun insertOrUpdateConsultation(scheduleId: Int, status: Int) {
        val consultation = Consultation(scheduleId = scheduleId, status = status)
        consultationRepository.insertOrUpdateConsultation(consultation)
    }
    
    fun getPanasAnswer(question: String): Int? {
        if (panasValues.containsKey(question)) {
            return panasValues[question]
        }

        return null
    }

    fun handlePanasQuestion(question: String, answer: Int) {
        panasValues[question] = answer
    }

}
