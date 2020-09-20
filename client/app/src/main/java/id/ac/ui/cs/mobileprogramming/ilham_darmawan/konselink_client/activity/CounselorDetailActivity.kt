package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.adapter.CounselorScheduleTimeListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.ActivityCounselorDetailBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.CounselorService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.CounselorViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.CounselorViewModelFactory

class CounselorDetailActivity : AppCompatActivity() {
    private lateinit var counselorListRecyclerView: RecyclerView
    private val counselorViewModel by lazy {
        ViewModelProvider(
            this, CounselorViewModelFactory(
                this,
                CounselorService.create(),
                ApplicationDatabase.getInstance(this)!!
            )
        ).get(CounselorViewModel::class.java)
    }

    private val counselorScheduleTimeListAdapter by lazy {
        CounselorScheduleTimeListAdapter(this, counselorViewModel)
    }

    private lateinit var sharedPref: SharedPreferences
    private var counselorId: Int? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityCounselorDetailBinding>(
            this,
            R.layout.activity_counselor_detail
        ).apply {
            lifecycleOwner = this@CounselorDetailActivity
            vm = counselorViewModel
        }

        counselorListRecyclerView = binding.counselorScheduleIntervalRecyclerview
        sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        token = sharedPref.getString(TOKEN, null)

        counselorScheduleTimeListAdapter.token = token
        counselorScheduleTimeListAdapter.counselorId = intent.getIntExtra(COUNSELOR_ID, -1)

        assignElements()
    }


    private fun assignElements() {
        val scheduleDayScrollView = findViewById<LinearLayout>(R.id.inner_schedule_day)
        counselorListRecyclerView.layoutManager = LinearLayoutManager(this)
        counselorListRecyclerView.adapter = counselorScheduleTimeListAdapter
        counselorViewModel.counselorDetailLiveData.observe(this) {
            counselorViewModel.bookedSchedules.value = ArrayList()
            it.schedule?.forEach {counselorSchedule ->
                val horizontalItemView = LayoutInflater.from(this).inflate(R.layout.item_counselor_schedule_day, null)
                val button = horizontalItemView.findViewById<Button>(R.id.text_item_counselor_schedule_day)
                button.text = counselorSchedule.day
                button.setOnClickListener {
                    counselorViewModel.selectedButton.value = counselorSchedule.day
                    button.background = ContextCompat.getDrawable(this, R.drawable.button_blue_gradient)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        button.setTextColor(resources.getColor(R.color.white, null))
                        counselorScheduleTimeListAdapter.updateInterval(counselorSchedule.day, counselorSchedule.session)
                    }
                    toggleButtons(scheduleDayScrollView)
                }
                scheduleDayScrollView.addView(horizontalItemView)
                }
            }

        if (intent.extras != null && intent.extras?.containsKey(COUNSELOR_ID)!!) {
            counselorId = intent.getIntExtra(COUNSELOR_ID, -1)
            counselorViewModel.getCounselorDetail(token, intent.getIntExtra(COUNSELOR_ID, -1))
        } else {
            finish()
        }
    }

    private fun toggleButtons(scheduleDayScrollView: LinearLayout?) {
        scheduleDayScrollView?.children?.iterator()?.forEach {
            val button = it.findViewById<Button>(R.id.text_item_counselor_schedule_day)
            if (button.text != counselorViewModel.selectedButton.value) {
                button.background = ContextCompat.getDrawable(this, R.drawable.ellipsis_white_blue)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    button.setTextColor(resources.getColor(R.color.bg_dark_blue, null))
                }
            }
        }
    }

}