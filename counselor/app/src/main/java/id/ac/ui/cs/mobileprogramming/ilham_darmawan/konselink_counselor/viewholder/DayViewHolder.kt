package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewholder

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.ViewContainer
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter.ConsultationScheduleListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.ConsultationSchedule
import kotlinx.android.synthetic.main.item_calendar_day.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*


class DayViewHolder(
    view: View,
    private val calendarView: CalendarView,
    private val selectedDate: MutableLiveData<LocalDate>,
    private val consultationScheduleList: LiveData<List<ConsultationSchedule>>,
    private val adapter: ConsultationScheduleListAdapter
) : ViewContainer(view) {
    lateinit var day: CalendarDay // Will be set when this container is bound.
    val textView = view.text_day
    val layout = view.exFiveDayLayout
    val scheduleExistIcon = view.icon_schedule_set

    init {
        view.setOnClickListener {
            if (day.owner == DayOwner.THIS_MONTH) {
                if (selectedDate.value != day.date) {
                    val oldDate = selectedDate.value
                    selectedDate.value = day.date
                    calendarView.notifyDateChanged(day.date)
                    oldDate?.let { calendarView.notifyDateChanged(it) }
                    updateAdapterForDate(calendarView, day.date)
                }
            }
        }
    }

    private fun updateAdapterForDate(calendarView: CalendarView, date: LocalDate?) {
        selectedDate.value = date
        val consultationSchedule = consultationScheduleList.value?.filter {
         formatInconsistentDate(it.day) == date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }

        if (consultationSchedule != null && consultationSchedule.isNotEmpty()) {
            adapter.updateScheduleList(consultationSchedule[0].session!!)
        } else {
            adapter.clearScheduleList()
        }

        calendarView.notifyCalendarChanged()
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