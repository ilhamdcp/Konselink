package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.text.Editable
import android.text.TextWatcher
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.FormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.RegistrationViewModel

class TextFormListener(
    val registrationViewModel: RegistrationViewModel,
    val formType: String
) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        when (formType) {
            FormType.FULLNAME.typeName -> {
                registrationViewModel.handleFullNameEditText(s.toString())
            }

            FormType.STR_NUMBER.typeName -> {
                registrationViewModel.handleStrNumber(s.toString())
            }

            FormType.SIP_NUMBER.typeName -> {
                registrationViewModel.handleSipNumber(s.toString())
            }

            FormType.SSP_NUMBER.typeName -> {
                registrationViewModel.handleSspNumber(s.toString())
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}