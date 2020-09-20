package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.chat

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.transition.Fade
import androidx.transition.TransitionManager
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.ALARM_END_CHAT_REQUEST
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.ConsultationStatus
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentConsultationPreconsultationPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.service.EndChatAlarmService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.fragment_consultation_preconsultation_page.*

class PreConsultationFragment : Fragment() {
    private val consultationViewModel by lazy {
        ViewModelProvider(
            this, ConsultationViewModelFactory(
                requireContext(),
                ConsultationService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(ConsultationViewModel::class.java)
    }
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentConsultationPreconsultationPageBinding>(
            inflater,
            R.layout.fragment_consultation_preconsultation_page,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = consultationViewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assignElements()
    }

    private fun assignElements() {
        sharedPref = activity?.getSharedPreferences(BuildConfig.SHARED_PREF, Context.MODE_PRIVATE)!!
        layout_loading_preconsultation.visibility = View.GONE

        val transition = Fade()
        transition.duration = 300
        transition.addTarget(layout_loading_preconsultation)
        TransitionManager.beginDelayedTransition(
            layout_loading_preconsultation.parent as ViewGroup,
            transition
        )

        button_save_panas_answer.setOnClickListener {
            if (consultationViewModel.preConsultationPageIsValid()) {
                consultationViewModel.postPreConsultationData(
                    sharedPref.getString(TOKEN, "")!!,
                    activity?.intent?.getIntExtra("scheduleId", 0)!!
                )
                layout_loading_preconsultation.visibility = View.VISIBLE
            } else {
                Toast.makeText(
                    requireContext(),
                    "Harap mengisi seluruh data kuesioner",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        consultationViewModel.postPreConsultationCodeLiveData.observe(viewLifecycleOwner) {
            if (it == 200) {
                Thread {
                    consultationViewModel.insertOrUpdateConsultation(
                        activity?.intent?.getIntExtra(
                            "scheduleId",
                            0
                        )!!, ConsultationStatus.CHAT.code
                    )
                }.start()

                val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(requireContext(), EndChatAlarmService::class.java)
                val pendingIntent = PendingIntent.getBroadcast(requireContext(), ALARM_END_CHAT_REQUEST, alarmIntent, PendingIntent.FLAG_NO_CREATE)
                if (pendingIntent == null) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HOUR + AlarmManager.INTERVAL_HALF_HOUR, pendingIntent)
                }

                activity?.supportFragmentManager!!
                    .beginTransaction()
                    .replace(R.id.consultation_fragment_container, ChatFragment(), "chatFragment")
                    .commit()
            } else {
                layout_loading_preconsultation.visibility = View.GONE
            }
        }
    }
}