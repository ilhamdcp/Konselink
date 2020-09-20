package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ResponseType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ActivityAddNewScheduleSecondPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.ConsultationSessionNumListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.ConsultationTimeListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.SessionIntervalNumListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.activity_add_new_schedule_second_page.*

class AddNewScheduleSecondPageActivity : AppCompatActivity() {
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
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityAddNewScheduleSecondPageBinding>(
            this,
            R.layout.activity_add_new_schedule_second_page
        ).apply {
            vm = consultationViewModel
            lifecycleOwner = this@AddNewScheduleSecondPageActivity
        }
        sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        getExtra()
        initializeViewModel()
        assignElements()
    }

    private fun initializeViewModel() {
        consultationViewModel.statusCode.observe(this) {
            if (it == 200) {
                setResult(ResponseType.SUCCESS.code)
                finish()
            } else {
                layout_loading.visibility = View.GONE
            }
        }
    }

    private fun getExtra() {
        val startDate = intent.getStringExtra(START_DATE)
        val endDate = intent.getStringExtra(END_DATE)
        if (startDate.isNullOrBlank() || endDate.isNullOrBlank()) {
            finish()
        } else {
            consultationViewModel.startDateString.value = startDate
            consultationViewModel.endDateString.value = endDate
        }
    }

    private fun assignElements() {
        layout_loading.visibility = View.GONE
        consultation_time_picker.setIs24HourView(true)
        consultation_time_picker.setOnTimeChangedListener(ConsultationTimeListener(consultationViewModel))

        session_num_picker.setOnValueChangedListener(ConsultationSessionNumListener(consultationViewModel))
        interval_picker.setOnValueChangedListener(SessionIntervalNumListener(consultationViewModel))
        interval_picker.minValue = 0
        interval_picker.maxValue = 60

        val sundayCheckbox = findViewById<CheckBox>(R.id.checkbox_sunday)
        val mondayCheckBox = findViewById<CheckBox>(R.id.checkbox_monday)
        val tuesdayCheckbox = findViewById<CheckBox>(R.id.checkbox_tuesday)
        val wednesdayCheckbox = findViewById<CheckBox>(R.id.checkbox_wednesday)
        val thursdayCheckbox = findViewById<CheckBox>(R.id.checkbox_thursday)
        val fridayCheckbox = findViewById<CheckBox>(R.id.checkbox_friday)
        val saturdayCheckbox = findViewById<CheckBox>(R.id.checkbox_saturday)

        sundayCheckbox.setOnCheckedChangeListener { _, isChecked ->
            consultationViewModel.updateWorkDays(0, isChecked)
        }

        mondayCheckBox.setOnCheckedChangeListener { _, isChecked ->
            consultationViewModel.updateWorkDays(1, isChecked)
        }

        tuesdayCheckbox.setOnCheckedChangeListener { _, isChecked ->
            consultationViewModel.updateWorkDays(2, isChecked)
        }

        wednesdayCheckbox.setOnCheckedChangeListener { _, isChecked ->
            consultationViewModel.updateWorkDays(3, isChecked)
        }

        thursdayCheckbox.setOnCheckedChangeListener { _, isChecked ->
            consultationViewModel.updateWorkDays(4, isChecked)
        }

        fridayCheckbox.setOnCheckedChangeListener { _, isChecked ->
            consultationViewModel.updateWorkDays(5, isChecked)
        }

        saturdayCheckbox.setOnCheckedChangeListener { _, isChecked ->
            consultationViewModel.updateWorkDays(6, isChecked)
        }

        button_save_schedule.setOnClickListener {
            if (consultationViewModel.newSchedulePageIsValid()) {
                layout_loading.visibility = View.VISIBLE
                consultationViewModel.postNewScheduleData(sharedPref.getString(TOKEN, "")!!)
            } else {
                Toast.makeText(this, "Data belum lengkap/tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

    }


}