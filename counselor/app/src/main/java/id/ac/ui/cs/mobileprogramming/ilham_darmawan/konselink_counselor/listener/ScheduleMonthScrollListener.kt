package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener

import android.os.Build
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthScrollListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter.ConsultationScheduleListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class ScheduleMonthScrollListener(
    private val monthTitleFormatter: DateTimeFormatter,
    private val monthYearHeader: TextView,
    private val calendarView: CalendarView,
    private val consultationViewModel: ConsultationViewModel,
    private val token: String,
    private val adapter: ConsultationScheduleListAdapter
): MonthScrollListener {
    override fun invoke(month: CalendarMonth) {
        val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
        monthYearHeader.text = title
        consultationViewModel.getConsultationSchedule(token, month.month, month.year)
        consultationViewModel.currentMonth.value = month.month
        consultationViewModel.currentYear.value = month.year

        consultationViewModel.selectedDate.value?.let {
            // Clear selection if we scroll to a new month.
            consultationViewModel.selectedDate.value = null
            consultationViewModel.currentMonth.value = month.month
            adapter.clearScheduleList()
            calendarView.notifyDateChanged(it)
        }
    }
}