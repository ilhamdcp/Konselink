package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.util.Log
import android.widget.TimePicker
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.parseTimeString
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel

class ConsultationTimeListener(private val consultationViewModel: ConsultationViewModel): TimePicker.OnTimeChangedListener {

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        Log.d("STARTTIME", "$hourOfDay:$minute")
        consultationViewModel.startSessionTime.value = "${parseTimeString(hourOfDay.toString())}:${parseTimeString(minute.toString())}"
    }
}