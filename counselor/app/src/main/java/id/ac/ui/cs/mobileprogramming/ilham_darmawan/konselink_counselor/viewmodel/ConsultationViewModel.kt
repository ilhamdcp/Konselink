package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.stfalcon.chatkito.utils.DateFormatter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.ClientRecord
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.Consultation
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository.ConsultationRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import org.threeten.bp.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class ConsultationViewModel(
    private val context: Context,
    private val consultationService: ConsultationService,
    private val db: ApplicationDatabase
) : ViewModel() {
    private val COMMA = ","
    val disabledDrawable = ContextCompat.getDrawable(context, R.drawable.button_disabled)
    val enabledDrawable = ContextCompat.getDrawable(context, R.drawable.button_blue_gradient)
    private val consultationRepository = ConsultationRepository(context, consultationService, db)
    val consultationScheduleList = consultationRepository.consultationScheduleListLiveData
    val statusCode: LiveData<Int> = consultationRepository.statusCode
    val chatListLiveData = consultationRepository.chatListLiveData
    val ongoingScheduleLiveData = consultationRepository.ongoingScheduleLiveData
    val upcomingScheduleLiveData = consultationRepository.upcomingScheduleLiveData
    val ongoingStatusCodeLiveData = consultationRepository.ongoingStatusCodeLiveData
    val upcomingStatusCodeLiveData = consultationRepository.upcomingStatusCodeLiveData
    val postClientRecordStatusCodeLiveData = consultationRepository.postClientRecordStatusCodeLiveData
    val icdDiagnosisListLiveData = consultationRepository.icdDiagnosisListLiveData
    val icdCurrentPageLiveData = consultationRepository.icdDiagnosisCurrentPageLiveData
    val icdTotalPageLiveData = consultationRepository.icdDiagnosisTotalPageLiveData
    val preConsultationSurveyListLiveData = consultationRepository.preConsultationSurveyListLiveData
    val clientRecordListLiveData = consultationRepository.clientRecordListLiveData
    val deleteScheduleStatusCodeLiveData = consultationRepository.deleteScheduleStatusCodeLiveData
    var deletedSchedule = MutableLiveData<ArrayList<Int>>(ArrayList())

    private val _workDays =
        MutableLiveData<Array<Boolean>>(arrayOf(false, false, false, false, false, false, false))
    val workDays: LiveData<Array<Boolean>> = _workDays
    var selectedDate: MutableLiveData<LocalDate> = MutableLiveData()
    var currentMonth: MutableLiveData<Int> = MutableLiveData()
    var currentYear: MutableLiveData<Int> = MutableLiveData()
    var clientRecordLiveData: MutableLiveData<ClientRecord> = MutableLiveData(ClientRecord())

    val startDate = MutableLiveData<LocalDate>()
    val endDate = MutableLiveData<LocalDate>()
    val startDateString = MutableLiveData<String>()
    val endDateString = MutableLiveData<String>()
    val sessionInterval = MutableLiveData(0)
    val useDiagnosis = MutableLiveData(true)

    val startSessionTime = MutableLiveData<String>()
    val sessionNum = MutableLiveData<Int>()

    fun getConsultationSchedule(token: String, month: Int, year: Int) {
        consultationRepository.retrieveConsultationScheduleList(token, month, year)
    }

    fun newSchedulePageIsValid(): Boolean {
        return startDateString.value != null && startDateString.value!!.isNotBlank() &&
                endDateString.value != null && endDateString.value!!.isNotBlank() &&
                _workDays.value?.filter { it } != null && _workDays.value?.filter { it }!!.isNotEmpty() &&
                startSessionTime.value != null && startSessionTime.value!!.isNotBlank() &&
                sessionNum.value != null && sessionNum.value!! > 0
    }

    fun updateWorkDays(index: Int, checked: Boolean) {
        val temp = _workDays.value
        temp?.set(index, checked)
        _workDays.value = temp
    }

    fun postNewScheduleData(token: String) {
        consultationRepository.postNewScheduleData(
            token,
            startDateString.value!!,
            endDateString.value!!,
            sessionNum.value!!,
            generateWorkDays(),
            startSessionTime.value!!,
            sessionInterval.value!!

        )
    }

    fun generateWorkDays(): String {
        var workDaysString = ""
        for (i in 0 until workDays.value?.size!!) {
            if (workDays.value!![i]) {
                workDaysString = if (workDaysString.isEmpty()) {
                    workDaysString.plus((i + 1).toString())
                } else {
                    workDaysString.plus(COMMA).plus((i + 1).toString())
                }
            }
        }

        return workDaysString
    }

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

    fun getConsultation(scheduleId: Int): LiveData<Consultation> {
        return consultationRepository.getConsultation(scheduleId)
    }

    fun insertOrUpdateConsultation(scheduleId: Int, status: Int) {
        val consultation = Consultation(scheduleId = scheduleId, status = status)
        consultationRepository.insertOrUpdateConsultation(consultation)
    }

    fun postConsultationPageIsValid(): Boolean {
        val clientRecord = clientRecordLiveData.value!!
        var condition = true
        condition = if (useDiagnosis.value!!) {
            condition.and(clientRecord.diagnosis != null && clientRecord.diagnosis!!.isNotBlank() &&
                    clientRecord.diagnosisCode != null && clientRecord.diagnosisCode!! > 0)
        } else {
            condition.and(clientRecord.problemDescription != null && clientRecord.problemDescription!!.isNotBlank())
        }
        condition = condition.and(clientRecord.physicalHealthHistory != null && clientRecord.physicalHealthHistory!!.isNotBlank() &&
                clientRecord.medicalConsumption != null && clientRecord.medicalConsumption!!.isNotBlank() &&
                clientRecord.suicideRisk != null && clientRecord.suicideRisk!!.isNotBlank() &&
                clientRecord.selfHarmRisk != null && clientRecord.selfHarmRisk!!.isNotBlank() &&
                clientRecord.othersHarmRisk != null && clientRecord.othersHarmRisk!!.isNotBlank() &&
                clientRecord.assessment != null && clientRecord.assessment!!.isNotBlank() &&
                clientRecord.consultationPurpose != null && clientRecord.consultationPurpose!!.isNotBlank())
        return condition
    }
    fun handleDiagnosisEditText(diagnosis: String) {
        if (diagnosis.isBlank()){
            val temp = clientRecordLiveData.value
            temp?.diagnosis = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.diagnosis = diagnosis
            clientRecordLiveData.value = temp
        }
    }

    fun handleDiagnosisCodeEditText(diagnosisCode: String) {
        if (diagnosisCode.isBlank() || !diagnosisCode.isDigitsOnly()){
            val temp = clientRecordLiveData.value
            temp?.diagnosisCode = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.diagnosisCode = diagnosisCode.toInt()
            clientRecordLiveData.value = temp
        }
    }

    fun handlePhysicalHealthHistoryEditText(physicalHealthHistory: String) {
        if (physicalHealthHistory.isBlank()){
            val temp = clientRecordLiveData.value
            temp?.physicalHealthHistory = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.physicalHealthHistory = physicalHealthHistory
            clientRecordLiveData.value = temp
        }
    }

    fun handleMedicalConsumptionEditText(medicalConsumption: String) {
        if (medicalConsumption.isBlank()){
            val temp = clientRecordLiveData.value
            temp?.medicalConsumption = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.medicalConsumption = medicalConsumption
            clientRecordLiveData.value = temp
        }
    }

    fun handleAssessmentEditText(assessment: String) {
        if (assessment.isBlank()){
            val temp = clientRecordLiveData.value
            temp?.assessment = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.assessment = assessment
            clientRecordLiveData.value = temp
        }
    }

    fun handleConsultationPurposeEditText(consultationPurpose: String) {
        if (consultationPurpose.isBlank()){
            val temp = clientRecordLiveData.value
            temp?.consultationPurpose = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.consultationPurpose = consultationPurpose
            clientRecordLiveData.value = temp
        }
    }

    fun handleTreatmentPlanEditText(treatmentPlan: String) {
        if (treatmentPlan.isBlank()){
            val temp = clientRecordLiveData.value
            temp?.treatmentPlan = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.treatmentPlan = treatmentPlan
            clientRecordLiveData.value = temp
        }
    }

    fun handleMeetingsEditText(meetings: String) {
        if (meetings.isBlank() || !meetings.isDigitsOnly()){
            val temp = clientRecordLiveData.value
            temp?.meetings = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.meetings = Integer.parseInt(meetings)
            clientRecordLiveData.value = temp
        }
    }

    fun handleSelfHarmSpinner(risk: String?) {
        if (risk.isNullOrBlank()) {
            val temp = clientRecordLiveData.value
            temp?.selfHarmRisk = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.selfHarmRisk = risk
            clientRecordLiveData.value = temp
        }
    }

    fun handleSuicideSpinner(risk: String?) {
        if (risk.isNullOrBlank()) {
            val temp = clientRecordLiveData.value
            temp?.suicideRisk = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.suicideRisk = risk
            clientRecordLiveData.value = temp
        }
    }

    fun handleOthersHarmSpinner(risk: String?) {
        if (risk.isNullOrBlank()) {
            val temp = clientRecordLiveData.value
            temp?.othersHarmRisk = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.othersHarmRisk = risk
            clientRecordLiveData.value = temp
        }
    }

    fun handleNotesEditText(notes: String) {
        if (notes.isBlank()){
            val temp = clientRecordLiveData.value
            temp?.notes = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.notes = notes
            clientRecordLiveData.value = temp
        }
    }

    fun handleProblemDescriptionEditText(problemDescription: String) {
        if (problemDescription.isBlank()){
            val temp = clientRecordLiveData.value
            temp?.problemDescription = null
            clientRecordLiveData.value = temp
        }

        else {
            val temp = clientRecordLiveData.value
            temp?.problemDescription = problemDescription
            clientRecordLiveData.value = temp
        }
    }

    fun updateUseDiagnosis(useDiagnosis: Boolean) {
        this.useDiagnosis.value = useDiagnosis
    }

    fun postClientRecordData(token: String) {
        Log.d("JSON", Gson().toJson(clientRecordLiveData.value))
        consultationRepository.postClientRecordData(token, clientRecordLiveData.value!!)
    }

    fun getIcdDiagnosis(token: String, entrySize: Int, pageNo: Int, keyword: String) {
        consultationRepository.retrieveIcdDiagnosis(token, entrySize, pageNo, keyword)
    }

    fun getPreConsultationSurvey(token: String, scheduleId: Int) {
        consultationRepository.retrievePreConsultationSurvey(token, scheduleId)
    }

    fun getClientRecord(token: String, clientId: Int) {
        consultationRepository.retrieveClientRecord(token, clientId)
    }

    fun deleteScheduleData(token: String, scheduleId: Int) {
        consultationRepository.deleteScheduleData(token, scheduleId)
    }
}
