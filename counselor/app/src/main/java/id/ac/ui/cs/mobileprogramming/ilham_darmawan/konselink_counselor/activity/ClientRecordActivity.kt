package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter.ClientRecordListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ActivityClientRecordBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.activity_client_record.*

class ClientRecordActivity : AppCompatActivity() {
    private val consultationViewModel by lazy {
        ViewModelProvider(
            this, ConsultationViewModelFactory(
                this,
                ConsultationService.create(),
                ApplicationDatabase.getInstance(this)!!
            )
        ).get(ConsultationViewModel::class.java)
    }

    private val adapter = ClientRecordListAdapter()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var token: String
    private var clientId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityClientRecordBinding>(
            this,
            R.layout.activity_client_record
        )

        assignElements()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        initializeViewModel()
    }

    private fun initializeViewModel() {
        consultationViewModel.getClientRecord(token, clientId)
    }

    private fun observeViewModel() {
        consultationViewModel.clientRecordListLiveData.observe(this) {
            adapter.updateClientRecordList(it)
        }
    }

    private fun assignElements() {
        sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        token = sharedPref.getString(TOKEN, "")!!
        clientId = intent.getIntExtra("clientId", 0)
        recyclerview_client_record.layoutManager = LinearLayoutManager(this)
        recyclerview_client_record.adapter = adapter
    }

}