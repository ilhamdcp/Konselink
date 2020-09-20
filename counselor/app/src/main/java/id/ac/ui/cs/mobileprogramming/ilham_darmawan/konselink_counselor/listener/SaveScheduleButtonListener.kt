package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.AddNewScheduleFirstPageActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.AddNewScheduleSecondPageActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import org.threeten.bp.format.DateTimeFormatter

class SaveScheduleButtonListener(private val activity: AddNewScheduleFirstPageActivity, private val consultationViewModel: ConsultationViewModel): View.OnClickListener {
    val SECOND_PAGE_NEW_SCHEDULE = 100

    override fun onClick(v: View?) {
        val startDate = consultationViewModel.startDate
        val endDate = consultationViewModel.endDate
        if (startDate.value != null && endDate.value != null) {
            val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
            val intent = Intent(activity, AddNewScheduleSecondPageActivity::class.java)
            intent.putExtra(BuildConfig.START_DATE, formatter.format(consultationViewModel.startDate.value))
            intent.putExtra(BuildConfig.END_DATE, formatter.format(consultationViewModel.endDate.value))
            activity.startActivityForResult(intent, SECOND_PAGE_NEW_SCHEDULE)
        } else {
            Toast.makeText(
                activity,
                "Jadwal awal dan akhir praktik belum dipilih",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}