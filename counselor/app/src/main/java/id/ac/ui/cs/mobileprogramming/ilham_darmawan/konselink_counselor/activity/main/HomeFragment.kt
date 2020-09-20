package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.chat.ConsultationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.FragmentMainHomePageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.UserService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModelFactory
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ProfileViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ProfileViewModelFactory
import kotlinx.android.synthetic.main.fragment_main_home_page.*

class HomeFragment: Fragment() {
    private val consultationViewModel by lazy {
        ViewModelProvider(
            this, ConsultationViewModelFactory(
                requireContext(),
                ConsultationService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(ConsultationViewModel::class.java)
    }

    private val profileViewModel by lazy {
        ViewModelProvider(
            this, ProfileViewModelFactory(
                requireContext(),
                UserService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(ProfileViewModel::class.java)
    }
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentMainHomePageBinding>(
            inflater,
            R.layout.fragment_main_home_page,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = consultationViewModel
            uservm = profileViewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!
        assignElements()
    }

    override fun onResume() {
        super.onResume()
        initializeViewModel()
    }

    private fun assignElements() {
        home_swipe_layout.setOnRefreshListener {
            consultationViewModel.getUpcomingSchedule(sharedPref.getString(TOKEN, "")!!, 1, 1)
            consultationViewModel.getOngoingSchedule(sharedPref.getString(TOKEN, "")!!)
            if (profileViewModel.userDataLiveData.value == null) {
                    profileViewModel.getUserData(sharedPref.getString(TOKEN, "")!!)
            }
        }

        consultationViewModel.ongoingStatusCodeLiveData.observe(viewLifecycleOwner) {
            if (it > 0) {
                home_swipe_layout.isRefreshing = false
            }
        }

        consultationViewModel.upcomingStatusCodeLiveData.observe(viewLifecycleOwner) {
            if (it > 0) {
                home_swipe_layout.isRefreshing = false
            }
        }

        button_start_chat.setOnClickListener {
            if (consultationViewModel.ongoingScheduleLiveData.value?.clientId != null) {
                val intent = Intent(requireContext(), ConsultationActivity::class.java)
                intent.putExtra("userName", profileViewModel.userDataLiveData.value?.fullname)
                intent.putExtra(
                    "clientName",
                    consultationViewModel.ongoingScheduleLiveData.value?.clientName
                )
                intent.putExtra(
                    "clientId",
                    consultationViewModel.ongoingScheduleLiveData.value?.clientId!!
                )
                intent.putExtra(
                    "scheduleId",
                    consultationViewModel.ongoingScheduleLiveData.value?.scheduleId
                )

                startActivity(intent)
            }
        }
    }

    private fun initializeViewModel() {
        profileViewModel.getUserData(sharedPref.getString(TOKEN, "")!!)
        consultationViewModel.getUpcomingSchedule(sharedPref.getString(TOKEN, "")!!, 1, 1)
        consultationViewModel.getOngoingSchedule(sharedPref.getString(TOKEN, "")!!)
    }
}
