package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.view.View
import android.widget.AdapterView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.FormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.Specialization
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.RegistrationViewModel

class SpinnerFormListener(val registrationViewModel: RegistrationViewModel, val formType: String) :
    AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        when (formType) {
            FormType.SPECIALIZATION.typeName -> {
                registrationViewModel.handleSpecializationSpinner(null, -1)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (formType) {
            FormType.SPECIALIZATION.typeName -> {
                registrationViewModel.handleSpecializationSpinner(
                    (parent?.selectedItem as Specialization).specializationName, position)
            }
        }
    }
}