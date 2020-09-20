package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewholder

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.ViewContainer
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import kotlinx.android.synthetic.main.item_activity_add_new_schedule_calendar_day.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class NewScheduleDayViewHolder(
    view: View,
    private val context: Context,
    private val calendarView: CalendarView,
    private val startDate: MutableLiveData<LocalDate>,
    private val endDate: MutableLiveData<LocalDate>,
    private val today: LocalDate,
    private val textStartDate: TextView,
    private val textEndDate: TextView,
    private val saveButton: Button
) : ViewContainer(view) {
    lateinit var day: CalendarDay // Will be set when this container is bound.
    val textView = view.exFourDayText
    val roundBgView = view.exFourRoundBgView
    private val headerDateFormatter = DateTimeFormatter.ofPattern("EEE'\n'd MMM")

    init {
        view.setOnClickListener {
            if (day.owner == DayOwner.THIS_MONTH && (day.date == today || day.date.isAfter(today))) {
                val date = day.date
                if (startDate.value != null || endDate.value != null) {
                    if (date < startDate.value || endDate.value != null) {
                        startDate.value = date
                        endDate.value = null
                    } else if (date != startDate.value) {
                        endDate.value = date
                    }
                } else {
                    startDate.value = date
                }
                calendarView.notifyCalendarChanged()
                bindSummaryViews()
            }
        }
    }

    private fun bindSummaryViews() {
        if (startDate.value != null) {
            textStartDate.text = headerDateFormatter.format(startDate.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textStartDate.setTextColor(context.resources.getColor(R.color.gray, null))
            } else {
                textStartDate.setTextColor(context.resources.getColor(R.color.gray))
            }
        } else {
            textStartDate.text = context.getString(R.string.start_date)
            textStartDate.setTextColor(Color.GRAY)
        }
        if (endDate.value != null) {
            textEndDate.text = headerDateFormatter.format(endDate.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textEndDate.setTextColor(context.resources.getColor(R.color.gray, null))
            } else {
                textEndDate.setTextColor(context.resources.getColor(R.color.gray))
            }
        } else {
            textEndDate.text = context.getString(R.string.end_date)
            textEndDate.setTextColor(Color.GRAY)
        }

        // Enable save button if a range is selected or no date is selected at all, Airbnb style.
        saveButton.isEnabled = endDate.value != null || (startDate.value == null && endDate.value == null)
    }


}