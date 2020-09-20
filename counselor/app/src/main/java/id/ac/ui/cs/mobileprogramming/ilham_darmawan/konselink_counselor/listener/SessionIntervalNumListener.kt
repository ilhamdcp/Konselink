package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.widget.NumberPicker
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel

class SessionIntervalNumListener(private val consultationViewModel: ConsultationViewModel): NumberPicker.OnValueChangeListener {
    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        consultationViewModel.sessionInterval.value = newVal
    }
}