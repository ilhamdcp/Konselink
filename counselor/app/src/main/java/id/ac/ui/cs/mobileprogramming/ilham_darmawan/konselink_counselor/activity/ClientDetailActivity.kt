package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.DASH
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ActivityClientDetailBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.EducationData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ClientService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ClientViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ClientViewModelFactory
import kotlinx.android.synthetic.main.activity_client_detail.*

class ClientDetailActivity : AppCompatActivity() {
    private val clientViewModel by lazy {
        ViewModelProvider(
            this, ClientViewModelFactory(
                this,
                ClientService.create(),
                ApplicationDatabase.getInstance(this)!!
            )
        ).get(ClientViewModel::class.java)
    }

    private lateinit var sharedPref: SharedPreferences
    private lateinit var token: String
    private var clientId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityClientDetailBinding>(
            this,
            R.layout.activity_client_detail
        ).apply {
            lifecycleOwner = this@ClientDetailActivity
            vm = clientViewModel
        }


    }

    override fun onResume() {
        super.onResume()

        assignElements()
        initializeViewModel()
        observeViewModel()
    }

    private fun observeViewModel() {
        clientViewModel.clientDetailLiveData.observe(this) {
            parseScrollViewElement(linearlayout_kindergarten_parsed, it.kindergartenDataParsed)
            parseScrollViewElement(linearlayout_elementary_parsed, it.elementaryDataParsed)
            parseScrollViewElement(linearlayout_junior_parsed, it.elementaryDataParsed)
            parseScrollViewElement(linearlayout_senior_parsed, it.seniorDataParsed)
            parseScrollViewElement(linearlayout_college_parsed, it.collegeDataParsed)
        }
    }

    private fun parseScrollViewElement(
        parent: LinearLayout,
        schoolData: List<EducationData>?
    ) {
        if (schoolData.isNullOrEmpty()) {
            parent.visibility = View.GONE
        } else {
            schoolData.forEach {
                val linearLayout = LinearLayout(this)
                linearLayout.setPadding(R.dimen.main_container_default_margin)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.background = this.getDrawable(R.drawable.card_item)

                val schoolNameDescription = TextView(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    schoolNameDescription.setTextColor(
                        this.resources.getColor(
                            R.color.bg_dark_blue,
                            null
                        )
                    )
                } else {
                    schoolNameDescription.setTextColor(this.resources.getColor(R.color.bg_dark_blue))
                }
                schoolNameDescription.text = this.resources.getString(R.string.school_name)
                val dataValue = TextView(this)
                dataValue.text = if (it.schoolName.isNullOrBlank()) DASH else it.schoolName

                val schoolAddressDescription = TextView(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    schoolAddressDescription.setTextColor(
                        this.resources.getColor(
                            R.color.bg_dark_blue,
                            null
                        )
                    )
                } else {
                    schoolAddressDescription.setTextColor(this.resources.getColor(R.color.bg_dark_blue))
                }
                schoolAddressDescription.text =
                    this.resources.getString(R.string.address_or_domicile)
                val addressDataValue = TextView(this)
                addressDataValue.text =
                    if (it.schoolAddress.isNullOrBlank()) DASH else it.schoolAddress

                val admissionYearDescription = TextView(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    admissionYearDescription.setTextColor(
                        this.resources.getColor(
                            R.color.bg_dark_blue,
                            null
                        )
                    )
                } else {
                    admissionYearDescription.setTextColor(this.resources.getColor(R.color.bg_dark_blue))
                }
                admissionYearDescription.text = this.resources.getString(R.string.admission_year)
                val admissionYearDataValue = TextView(this)
                admissionYearDataValue.text =
                    if (it.admissionYear!! == 0) DASH else it.admissionYear.toString()

                val graduationYearDescription = TextView(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    graduationYearDescription.setTextColor(
                        this.resources.getColor(
                            R.color.bg_dark_blue,
                            null
                        )
                    )
                } else {
                    graduationYearDescription.setTextColor(this.resources.getColor(R.color.bg_dark_blue))
                }
                graduationYearDescription.text = this.resources.getString(R.string.graduation_year)
                val graduationYearDataValue = TextView(this)
                graduationYearDataValue.text =
                    if (it.graduateYear!! == 0) DASH else it.graduateYear.toString()

                linearLayout.addView(schoolNameDescription)
                linearLayout.addView(dataValue)
                linearLayout.addView(schoolAddressDescription)
                linearLayout.addView(addressDataValue)
                linearLayout.addView(admissionYearDescription)
                linearLayout.addView(admissionYearDataValue)
                linearLayout.addView(graduationYearDescription)
                linearLayout.addView(graduationYearDataValue)
                parent.addView(linearLayout)
            }
        }
    }

    private fun initializeViewModel() {
        clientViewModel.getClientDetail(token, clientId)
        clientViewModel.getIpipSurvey(token, clientId)
        clientViewModel.getSrqSurvey(token, clientId)
    }

    private fun assignElements() {
        sharedPref = getSharedPreferences(BuildConfig.SHARED_PREF, Context.MODE_PRIVATE)
        token = sharedPref.getString(BuildConfig.TOKEN, "")!!
        clientId = intent.getIntExtra("clientId", 0)
    }
}