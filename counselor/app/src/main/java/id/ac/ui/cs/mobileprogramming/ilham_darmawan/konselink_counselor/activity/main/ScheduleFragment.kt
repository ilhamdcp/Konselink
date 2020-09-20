package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.AddNewScheduleFirstPageActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter.ConsultationScheduleListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.binder.ScheduleDayBinder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.binder.ScheduleMonthHeaderBinder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.daysOfWeekFromLocale
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.ScheduleMonthScrollListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.SwipeToDeleteCallback
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.fragment_main_schedule_page.*
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter

class ScheduleFragment : Fragment() {
    private lateinit var sharedPref: SharedPreferences
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val consultationViewModel by lazy {
        ViewModelProvider(
            this,
            ConsultationViewModelFactory(
                requireContext(),
                ConsultationService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(ConsultationViewModel::class.java)
    }
    private lateinit var consultationScheduleListAdapter: ConsultationScheduleListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidThreeTen.init(activity?.applicationContext)
        return inflater.inflate(R.layout.fragment_main_schedule_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!
        consultationScheduleListAdapter = ConsultationScheduleListAdapter(requireContext(), consultationViewModel)
        consultationScheduleListAdapter.token = sharedPref.getString(TOKEN, "")!!
        itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(consultationScheduleListAdapter))
        consultationViewModel.currentMonth.value = YearMonth.now().monthValue
        consultationViewModel.currentYear.value = YearMonth.now().year
        recyclerview_consultation_schedule.layoutManager = LinearLayoutManager(requireContext())
        recyclerview_consultation_schedule.adapter = consultationScheduleListAdapter
        itemTouchHelper.attachToRecyclerView(recyclerview_consultation_schedule)
        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        calendar_consultation_schedule.setup(
            currentMonth.minusMonths(10),
            currentMonth.plusMonths(10),
            daysOfWeek.first()
        )
        calendar_consultation_schedule.scrollToMonth(currentMonth)

        calendar_consultation_schedule.dayBinder =
            ScheduleDayBinder(requireContext(), calendar_consultation_schedule, consultationViewModel.selectedDate, consultationViewModel.consultationScheduleList, consultationScheduleListAdapter)

        calendar_consultation_schedule.monthHeaderBinder =
            ScheduleMonthHeaderBinder(requireContext(), daysOfWeek)

        calendar_consultation_schedule.monthScrollListener = ScheduleMonthScrollListener(
            monthTitleFormatter,
            header_month_year,
            calendar_consultation_schedule,
            consultationViewModel,
            sharedPref.getString(TOKEN, "")!!,
            consultationScheduleListAdapter
        )

        button_next_month.setOnClickListener {
            calendar_consultation_schedule.findFirstVisibleMonth()?.let {
                consultationViewModel.currentMonth.value = it.yearMonth.next.monthValue
                consultationViewModel.currentYear.value = it.yearMonth.next.year
                calendar_consultation_schedule.smoothScrollToMonth(it.yearMonth.next)
            }
        }

        button_previous_month.setOnClickListener {
            calendar_consultation_schedule.findFirstVisibleMonth()?.let {
                consultationViewModel.currentMonth.value = it.yearMonth.previous.monthValue
                consultationViewModel.currentYear.value = it.yearMonth.previous.year
                calendar_consultation_schedule.smoothScrollToMonth(it.yearMonth.previous)
            }
        }

        button_add_new_schedule.setOnClickListener {
            val intent = Intent(context, AddNewScheduleFirstPageActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        consultationViewModel.selectedDate.value = null
        calendar_consultation_schedule.notifyCalendarChanged()
        consultationScheduleListAdapter.clearScheduleList()
        initializeViewModel()
    }

    private fun initializeViewModel() {
        consultationViewModel.consultationScheduleList.observe(viewLifecycleOwner) {
            Log.d("ConsultationSchedule", it.size.toString())
            calendar_consultation_schedule.notifyCalendarChanged()
        }
        consultationViewModel.getConsultationSchedule(sharedPref.getString(TOKEN, "")!!,
            consultationViewModel.currentMonth.value!!,
            consultationViewModel.currentYear.value!!)
    }
}
