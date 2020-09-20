package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.chat

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.ALARM_END_CHAT_REQUEST
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ConsultationStatus
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.service.EndChatAlarmService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModelFactory

class ConsultationActivity: AppCompatActivity() {
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
        sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!
    }

    private fun startFragment() {
        consultationViewModel.getConsultation(intent.getIntExtra("scheduleId", 0)).observe(this) {
            if (it == null) {
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(this, EndChatAlarmService::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, ALARM_END_CHAT_REQUEST, alarmIntent, PendingIntent.FLAG_NO_CREATE)
                if (pendingIntent == null) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR + AlarmManager.INTERVAL_HALF_HOUR, pendingIntent)
                }
                val fragment = ChatFragment()
                Thread {
                    consultationViewModel.insertOrUpdateConsultation(
                        intent.getIntExtra(
                            "scheduleId",
                            0
                        ), ConsultationStatus.CHAT.code
                    )
                }.start()
                supportFragmentManager.beginTransaction()
                    .add(R.id.consultation_fragment_container, fragment, "chatFragment")
                    .show(fragment).commit()
            } else {
                when (it.status) {
                    ConsultationStatus.CHAT.code -> {
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val alarmIntent = Intent(this, EndChatAlarmService::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_END_CHAT_REQUEST, alarmIntent, PendingIntent.FLAG_NO_CREATE)
                        if (pendingIntent == null) {
                            alarmManager.set(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR + AlarmManager.INTERVAL_HALF_HOUR, pendingIntent)
                        }
                        val fragment = ChatFragment()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .add(R.id.consultation_fragment_container, fragment, "chatFragment")
                            .show(fragment).commit()
                    }

                    ConsultationStatus.FILL_RECORD.code -> {
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val alarmIntent = Intent(this, EndChatAlarmService::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_END_CHAT_REQUEST, alarmIntent, PendingIntent.FLAG_NO_CREATE)
                        if (pendingIntent == null) {
                            alarmManager.set(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR + AlarmManager.INTERVAL_HALF_HOUR, pendingIntent)
                        }
                        val fragment = PostConsultationFragment()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .add(
                                R.id.consultation_fragment_container,
                                fragment,
                                "postConsultationFragment"
                            )
                            .show(fragment).commit()
                    }

                    ConsultationStatus.END.code -> {
                        Toast.makeText(this, "Konsultasi sudah berakhir", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        val fragment = ChatFragment()
                        Thread {
                        consultationViewModel.insertOrUpdateConsultation(
                            intent.getIntExtra(
                                "scheduleId",
                                0
                            ), ConsultationStatus.CHAT.code
                        )}.start()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .add(R.id.consultation_fragment_container, fragment, "chatFragment")
                            .show(fragment).commit()
                    }
                }
            }

        }
    }

}