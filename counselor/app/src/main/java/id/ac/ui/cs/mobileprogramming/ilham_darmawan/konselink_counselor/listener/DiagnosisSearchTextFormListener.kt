package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.text.Editable
import android.text.TextWatcher
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter.DiagnosisCodeListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import java.util.*


class DiagnosisSearchTextFormListener(
    private val consultationViewModel: ConsultationViewModel,
    private val adapter: DiagnosisCodeListAdapter,
    private val token: String,
    private val entrySize: Int
) : TextWatcher {
    private val DELAY: Long = 1000
    private var timer = Timer()

    override fun afterTextChanged(s: Editable?) {
        timer.cancel()
        timer = Timer()
        adapter.reset()
        timer.schedule(object: TimerTask() {
            override fun run() {
                consultationViewModel.getIcdDiagnosis(token, entrySize, 1, s.toString())
            }

        }, DELAY)

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

}