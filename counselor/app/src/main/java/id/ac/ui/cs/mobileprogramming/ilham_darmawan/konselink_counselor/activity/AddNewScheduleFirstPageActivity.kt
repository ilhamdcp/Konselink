package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.END_DATE
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.START_DATE
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.binder.AddNewScheduleDayBinder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.binder.AddNewScheduleMonthHeaderBinder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ResponseType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.daysOfWeekFromLocale
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.SaveScheduleButtonListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewholder.NewScheduleMonthViewHolder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.item_activity_add_new_schedule_calendar_header.view.*
import kotlinx.android.synthetic.main.activity_add_new_schedule_first_page.*
import kotlinx.android.synthetic.main.item_calendar_day_legend.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*

class AddNewScheduleFirstPageActivity : AppCompatActivity() {
    private val consultationViewModel by lazy {
        ViewModelProvider(
            this,
            ConsultationViewModelFactory(
                this,
                ConsultationService.create(),
                ApplicationDatabase.getInstance(this)!!
            )
        ).get(ConsultationViewModel::class.java)
    }
    private val today = LocalDate.now()
    private val headerDateFormatter = DateTimeFormatter.ofPattern("EEE'\n'd MMM")
    private val startBackground: GradientDrawable by lazy {
        this.getDrawable(R.drawable.item_cell_continuous_selected_bg_start) as GradientDrawable
    }
    private val endBackground: GradientDrawable by lazy {
        this.getDrawable(R.drawable.item_cell_continuous_selected_bg_end) as GradientDrawable
    }
    val SECOND_PAGE_NEW_SCHEDULE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_schedule_first_page)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // We set the radius of the continuous selection background drawable dynamically
        // since the view size is `match parent` hence we cannot determine the appropriate
        // radius value which would equal half of the view's size beforehand.
        calendar_add_new_schedule.post {
            val radius = ((calendar_add_new_schedule.width / 7) / 2).toFloat()
            // {mTopLeftRadius, mTopLeftRadius, mTopRightRadius, mTopRightRadius, mBottomRightRadius, mBottomRightRadius, mBottomLeftRadius, mBottomLeftRadius}
            startBackground.cornerRadii =
                floatArrayOf(radius, radius, 0F, 0F, 0F, 0F, radius, radius)
            endBackground.cornerRadii = floatArrayOf(0F, 0F, radius, radius, radius, radius, 0F, 0F)

        }

        // Set the First day of week depending on Locale
        val daysOfWeek = daysOfWeekFromLocale()
        legend_layout.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setTextColor(resources.getColor(R.color.gray, null))
                } else {
                    setTextColor(resources.getColor(R.color.gray))
                }
            }
        }

        val currentMonth = YearMonth.now()
        calendar_add_new_schedule.setup(
            currentMonth,
            currentMonth.plusMonths(12),
            daysOfWeek.first()
        )
        calendar_add_new_schedule.scrollToMonth(currentMonth)
        calendar_add_new_schedule.dayBinder = AddNewScheduleDayBinder(
            this,
            calendar_add_new_schedule,
            consultationViewModel.startDate,
            consultationViewModel.endDate,
            today,
            startBackground,
            endBackground,
            text_start_date,
            text_end_date,
            exFourSaveButton
        )
        calendar_add_new_schedule.monthHeaderBinder = AddNewScheduleMonthHeaderBinder()

        exFourSaveButton.setOnClickListener(SaveScheduleButtonListener(this, consultationViewModel))

        bindSummaryViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SECOND_PAGE_NEW_SCHEDULE) {
            if (resultCode == ResponseType.SUCCESS.code) {
                finish()
            }
        }
    }

    private fun bindSummaryViews() {
        if (consultationViewModel.startDate.value != null) {
            text_start_date.text = headerDateFormatter.format(consultationViewModel.startDate.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                text_start_date.setTextColor(resources.getColor(R.color.gray, null))
            } else {
                text_start_date.setTextColor(resources.getColor(R.color.gray))
            }
        } else {
            text_start_date.text = getString(R.string.start_date)
            text_start_date.setTextColor(Color.GRAY)
        }
        if (consultationViewModel.startDate.value != null) {
            text_end_date.text = headerDateFormatter.format(consultationViewModel.startDate.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                text_end_date.setTextColor(resources.getColor(R.color.gray, null))
            } else {
                text_end_date.setTextColor(resources.getColor(R.color.gray))
            }
        } else {
            text_end_date.text = getString(R.string.end_date)
            text_end_date.setTextColor(Color.GRAY)
        }

        // Enable save button if a range is selected or no date is selected at all, Airbnb style.
        exFourSaveButton.isEnabled = consultationViewModel.startDate.value != null || (consultationViewModel.startDate.value == null && consultationViewModel.startDate.value == null)
    }

    override fun onStart() {
        super.onStart()
        window.apply {
            // Update statusbar color to match toolbar color.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBarColor = getColor(R.color.white)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                statusBarColor = Color.GRAY
            }
        }
    }

    override fun onStop() {
        super.onStop()
        window.apply {
            // Reset statusbar color.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBarColor = getColor(R.color.colorPrimaryDark)
            } else {
                statusBarColor = resources.getColor(R.color.colorPrimaryDark)
            }
            decorView.systemUiVisibility = 0
        }
    }
}
