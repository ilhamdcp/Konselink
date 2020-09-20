package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.binder

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter.ConsultationScheduleListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.ConsultationSchedule
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewholder.DayViewHolder
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class ScheduleDayBinder(
    private val context: Context,
    private val calendar: CalendarView,
    private var selectedDate: MutableLiveData<LocalDate>,
    private val scheduleList: LiveData<List<ConsultationSchedule>>,
    private val adapter: ConsultationScheduleListAdapter
) : DayBinder<DayViewHolder> {
    override fun create(view: View) = DayViewHolder(view, calendar, selectedDate, scheduleList, adapter)
    override fun bind(container: DayViewHolder, day: CalendarDay) {
        container.day = day
        val textView = container.textView
        val layout = container.layout
        textView.text = day.date.dayOfMonth.toString()

        val scheduleExistIcon = container.scheduleExistIcon
        scheduleExistIcon.setImageDrawable(null)

        if (day.owner == DayOwner.THIS_MONTH) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(context.resources.getColor(R.color.bg_dark_blue, null))
            } else {
                textView.setTextColor(context.resources.getColor(R.color.bg_dark_blue))
            }

            layout.setBackgroundResource(if (selectedDate.value == day.date) R.drawable.color_selected_cell else 0)
            val todaySchedule = scheduleList.value?.filter {
                formatInconsistentDate(it.day) == day.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }

            if (todaySchedule != null && todaySchedule.isNotEmpty()) {
                scheduleExistIcon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_circle_blue, null))
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(context.resources.getColor(R.color.fade_gray, null))
            } else {
                textView.setTextColor(context.resources.getColor(R.color.fade_gray))
            }
        }
    }

    private fun formatInconsistentDate(day: String?): String? {
        if (day != null && day.isNotBlank()) {
            val splitDay = day.split("/")
            return splitDay.map {
                if (it.length < 2) "0$it"
                else it
            }.joinToString("/")
        }
        return day
    }
}