package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.widget.CompoundButton
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.RegistrationViewModel

class CheckboxListener(val viewmodel: RegistrationViewModel) : CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        viewmodel._dataConfirmed.value = isChecked
    }

}