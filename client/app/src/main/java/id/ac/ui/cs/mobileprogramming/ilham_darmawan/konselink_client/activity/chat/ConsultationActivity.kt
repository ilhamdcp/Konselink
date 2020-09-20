package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.ConsultationStatus
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ConsultationViewModelFactory

class ConsultationActivity : AppCompatActivity() {
    private val consultationViewModel by lazy {
        ViewModelProvider(
            this, ConsultationViewModelFactory(
                this,
                ConsultationService.create(),
                ApplicationDatabase.getInstance(this)!!
            )
        ).get(ConsultationViewModel::class.java)
    }

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultation)

        assignElements()
        startFragment()
    }

    private fun assignElements() {
        sharedPref = getSharedPreferences(BuildConfig.SHARED_PREF, Context.MODE_PRIVATE)!!
    }

    private fun startFragment() {
        consultationViewModel.getConsultation(intent.getIntExtra("scheduleId", 0)).observe(this) {
            if (it == null) {
                val fragment = PreConsultationFragment()
                Thread {
                    consultationViewModel.insertOrUpdateConsultation(
                        intent.getIntExtra(
                            "scheduleId",
                            0
                        ), ConsultationStatus.FILL_SURVEY.code
                    )
                }.start()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.consultation_fragment_container, fragment, "preConsultationFragment")
                    .show(fragment).commit()
            } else {
                when (it.status) {
                    ConsultationStatus.FILL_SURVEY.code -> {
                        val fragment =
                            if (supportFragmentManager.findFragmentByTag("preConsultationFragment") == null) PreConsultationFragment()
                            else supportFragmentManager.findFragmentByTag("preConsultationFragment")

                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.consultation_fragment_container,
                                fragment!!,
                                "preConsultationFragment"
                            )
                            .show(fragment).commit()
                    }

                    ConsultationStatus.CHAT.code -> {
                        val fragment =
                            if (supportFragmentManager.findFragmentByTag("chatFragment") == null) ChatFragment()
                            else supportFragmentManager.findFragmentByTag("chatFragment")
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.consultation_fragment_container, fragment!!, "chatFragment")
                            .show(fragment).commit()
                    }

                    ConsultationStatus.END.code -> {
                        Toast.makeText(this, "Konsultasi sudah berakhir", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        val fragment =
                            if (supportFragmentManager.findFragmentByTag("chatFragment") == null) ChatFragment()
                            else supportFragmentManager.findFragmentByTag("chatFragment")
                        Thread {
                            consultationViewModel.insertOrUpdateConsultation(
                                intent.getIntExtra(
                                    "scheduleId",
                                    0
                                ), ConsultationStatus.CHAT.code
                            )
                        }.start()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.consultation_fragment_container, fragment!!, "chatFragment")
                            .show(fragment).commit()
                    }
                }
            }
        }
    }
}