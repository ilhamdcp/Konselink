package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.binder

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewholder.MonthViewHolder
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.TextStyle
import java.util.*

class ScheduleMonthHeaderBinder(private val context: Context, private val daysOfWeek: Array<DayOfWeek>): MonthHeaderFooterBinder<MonthViewHolder> {
    override fun bind(container: MonthViewHolder, month: CalendarMonth) {
        if (container.legendLayout.tag == null) {
            container.legendLayout.tag = month.yearMonth
            container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                tv.text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    .toUpperCase(Locale.ENGLISH)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setTextColor(context.resources.getColor(R.color.fade_gray, null))
                } else {
                    tv.setTextColor(context.resources.getColor(R.color.fade_gray))
                }
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            }
            month.yearMonth
        }
    }

    override fun create(view: View): MonthViewHolder {
        return MonthViewHolder(view)
    }
}