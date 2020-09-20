package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.view.View
import android.widget.AdapterView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ClientRecordFormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.SymptomVisibility
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel

class ClientRecordSpinnerFormListener (private val consultationViewModel: ConsultationViewModel, private val formType: String): AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        when(formType) {
            ClientRecordFormType.SELF_HARM_RISK.typeName -> consultationViewModel.handleSelfHarmSpinner(null)
            ClientRecordFormType.SUICIDE_RISK.typeName -> consultationViewModel.handleSuicideSpinner(null)
            ClientRecordFormType.OTHERS_HARM_RISK.typeName -> consultationViewModel.handleOthersHarmSpinner(null)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(formType) {
            ClientRecordFormType.SELF_HARM_RISK.typeName -> consultationViewModel.handleSelfHarmSpinner((parent?.selectedItem as SymptomVisibility).level)
            ClientRecordFormType.SUICIDE_RISK.typeName -> consultationViewModel.handleSuicideSpinner((parent?.selectedItem as SymptomVisibility).level)
            ClientRecordFormType.OTHERS_HARM_RISK.typeName -> consultationViewModel.handleOthersHarmSpinner((parent?.selectedItem as SymptomVisibility).level)
        }
    }

}