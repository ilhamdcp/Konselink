package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.text.Editable
import android.text.TextWatcher
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ClientRecordFormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel

class ClientRecordTextFormListener(private val consultationViewModel: ConsultationViewModel, private val formType: String) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        when(formType) {
            ClientRecordFormType.DIAGNOSIS.typeName -> consultationViewModel.handleDiagnosisEditText(s.toString())
            ClientRecordFormType.DIAGNOSIS_CODE.typeName -> consultationViewModel.handleDiagnosisCodeEditText(s.toString())
            ClientRecordFormType.PHYSICAL_HEALTH_HISTORY.typeName -> consultationViewModel.handlePhysicalHealthHistoryEditText(s.toString())
            ClientRecordFormType.MEDICAL_CONSUMPTION.typeName -> consultationViewModel.handleMedicalConsumptionEditText(s.toString())
            ClientRecordFormType.ASSESSMENT.typeName -> consultationViewModel.handleAssessmentEditText(s.toString())
            ClientRecordFormType.CONSULTATION_PURPOSE.typeName -> consultationViewModel.handleConsultationPurposeEditText(s.toString())
            ClientRecordFormType.TREATMENT_PLAN.typeName -> consultationViewModel.handleTreatmentPlanEditText(s.toString())
            ClientRecordFormType.MEETINGS.typeName -> consultationViewModel.handleMeetingsEditText(s.toString())
            ClientRecordFormType.NOTES.typeName -> consultationViewModel.handleNotesEditText(s.toString())
            ClientRecordFormType.PROBLEM_DESCRIPTION.typeName -> consultationViewModel.handleProblemDescriptionEditText(s.toString())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}