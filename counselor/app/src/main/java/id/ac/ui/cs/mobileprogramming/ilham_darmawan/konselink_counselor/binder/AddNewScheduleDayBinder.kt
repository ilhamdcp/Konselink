package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.binder

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewholder.NewScheduleDayViewHolder
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class AddNewScheduleDayBinder(
    private val context: Context,
    private val calendar: CalendarView,
    private val startDate: MutableLiveData<LocalDate>,
    private val endDate: MutableLiveData<LocalDate>,
    private val today: LocalDate,
    private val startBackground: GradientDrawable,
    private val endBackground: GradientDrawable,
    val textStartDate: TextView,
    val textEndDate: TextView,
    private val saveButton: Button
) : DayBinder<NewScheduleDayViewHolder> {
    private val headerDateFormatter = DateTimeFormatter.ofPattern("EEE'\n'd MMM")

    override fun create(view: View) = NewScheduleDayViewHolder(
        view,
        context,
        calendar,
        startDate,
        endDate,
        today,
        textStartDate,
        textEndDate,
        saveButton
    )

    override fun bind(holderNewSchedule: NewScheduleDayViewHolder, day: CalendarDay) {
        holderNewSchedule.day = day
        val textView = holderNewSchedule.textView
        val roundBgView = holderNewSchedule.roundBgView

        textView.text = null
        textView.background = null
        roundBgView.visibility = View.INVISIBLE

        if (day.owner == DayOwner.THIS_MONTH) {
            textView.text = day.day.toString()

            if (day.date.isBefore(today)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextColor(context.resources.getColor(R.color.fade_gray, null))
                } else {
                    textView.setTextColor(context.resources.getColor(R.color.fade_gray))
                }
            } else {
                when {
                    startDate.value == day.date && endDate.value == null -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            textView.setTextColor(context.resources.getColor(R.color.white, null))
                        } else {
                            textView.setTextColor(context.resources.getColor(R.color.white))
                        }
                        roundBgView.visibility = View.VISIBLE
                        roundBgView.setBackgroundResource(R.drawable.item_cell_single_selected_bg)
                    }
                    day.date == startDate.value -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            textView.setTextColor(context.resources.getColor(R.color.white, null))
                        } else {
                            textView.setTextColor(context.resources.getColor(R.color.white))
                        }
                        textView.background = startBackground
                    }
                    startDate.value != null && endDate.value != null && (day.date > startDate.value && day.date < endDate.value) -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            textView.setTextColor(context.resources.getColor(R.color.white, null))
                        } else {
                            textView.setTextColor(context.resources.getColor(R.color.white))
                        }
                        textView.setBackgroundResource(R.drawable.item_cell_continuous_selected_bg_middle)
                    }
                    day.date == endDate.value -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            textView.setTextColor(context.resources.getColor(R.color.white, null))
                        } else {
                            textView.setTextColor(context.resources.getColor(R.color.white))
                        }
                        textView.background = endBackground
                    }
                    day.date == today -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            textView.setTextColor(
                                context.resources.getColor(
                                    R.color.bg_dark_blue,
                                    null
                                )
                            )
                        } else {
                            textView.setTextColor(context.resources.getColor(R.color.bg_dark_blue))
                        }
                        roundBgView.visibility = View.VISIBLE
                        roundBgView.setBackgroundResource(R.drawable.item_cell_today_bg)
                    }
                    else -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            textView.setTextColor(context.resources.getColor(R.color.gray, null))
                        } else {
                            textView.setTextColor(context.resources.getColor(R.color.gray))
                        }
                    }
                }
            }
        } else {

            // This part is to make the coloured selection background continuous
            // on the blank in and out dates across various months and also on dates(months)
            // between the start and end dates if the selection spans across multiple months.

            val startDate = startDate
            val endDate = endDate
            if (startDate.value != null && endDate.value != null) {
                // Mimic selection of inDates that are less than the startDate.
                // Example: When 26 Feb 2019 is startDate and 5 Mar 2019 is endDate,
                // this makes the inDates in Mar 2019 for 24 & 25 Feb 2019 look selected.
                if ((day.owner == DayOwner.PREVIOUS_MONTH
                            && startDate.value?.monthValue == day.date.monthValue
                            && endDate.value?.monthValue != day.date.monthValue) ||
                    // Mimic selection of outDates that are greater than the endDate.
                    // Example: When 25 Apr 2019 is startDate and 2 May 2019 is endDate,
                    // this makes the outDates in Apr 2019 for 3 & 4 May 2019 look selected.
                    (day.owner == DayOwner.NEXT_MONTH
                            && startDate.value?.monthValue != day.date.monthValue
                            && endDate.value?.monthValue == day.date.monthValue) ||

                    // Mimic selection of in and out dates of intermediate
                    // months if the selection spans across multiple months.
                    (startDate.value!! < day.date && endDate.value!! > day.date
                            && startDate.value?.monthValue != day.date.monthValue
                            && endDate.value?.monthValue != day.date.monthValue)
                ) {
                    textView.setBackgroundResource(R.drawable.item_cell_continuous_selected_bg_middle)
                }
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