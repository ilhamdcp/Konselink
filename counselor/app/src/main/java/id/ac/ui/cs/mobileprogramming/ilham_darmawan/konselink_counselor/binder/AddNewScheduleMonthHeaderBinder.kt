package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.binder

import android.view.View
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewholder.NewScheduleMonthViewHolder

class AddNewScheduleMonthHeaderBinder: MonthHeaderFooterBinder<NewScheduleMonthViewHolder> {
    override fun create(view: View) = NewScheduleMonthViewHolder(view)
    override fun bind(container: NewScheduleMonthViewHolder, month: CalendarMonth) {
        val monthTitle =
            "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
        container.textView.text = monthTitle
    }
}
