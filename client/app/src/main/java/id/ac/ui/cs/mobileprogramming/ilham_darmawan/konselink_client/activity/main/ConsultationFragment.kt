package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.adapter.UpcomingConsultationListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.BaseRecyclerviewListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.fragment_main_consultation_page.*

class ConsultationFragment : Fragment() {
    private val consultationViewModel by lazy {
        ViewModelProvider(
            this, ConsultationViewModelFactory(
                requireContext(),
                ConsultationService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(ConsultationViewModel::class.java)
    }

    private lateinit var adapter: UpcomingConsultationListAdapter
    private val ENTRY_SIZE = 15
    private lateinit var sharedPref: SharedPreferences
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_consultation_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignElements()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        initializeViewModel()
    }

    private fun observeViewModel() {
        consultationViewModel.upcomingScheduleListLiveData.observe(viewLifecycleOwner) {
            adapter.addData(it)
        }

        consultationViewModel.upcomingStatusCodeLiveData.observe(viewLifecycleOwner) {
            if (it > 0) {
                consultation_swipe_layout.isRefreshing = false
            }
        }
    }

    private fun assignElements() {
        adapter = UpcomingConsultationListAdapter(requireContext())
        sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!
        token = sharedPref.getString(TOKEN, "")!!
        upcoming_consultation_recyclerview.layoutManager = LinearLayoutManager(requireContext())
        upcoming_consultation_recyclerview.adapter = adapter

        consultation_swipe_layout.setOnRefreshListener {
            adapter.reset()
            consultationViewModel.getUpcomingSchedule(token, ENTRY_SIZE, 1)
        }

        upcoming_consultation_recyclerview.addOnScrollListener(object : BaseRecyclerviewListener(
            upcoming_consultation_recyclerview.layoutManager as LinearLayoutManager,
            ENTRY_SIZE) {
            override fun loadMoreItems() {
                sharedPref.getString(TOKEN, "")?.let {
                    consultationViewModel.getUpcomingSchedule(
                        it,
                        ENTRY_SIZE,
                        ++adapter.currentPage
                    )
                }
            }

            override fun isLastPage(): Boolean {
                return adapter.currentPage >= adapter.totalPage
            }

            override fun isLoading(): Boolean {
                return adapter.isLoading
            }
        })

    }

    private fun initializeViewModel() {
        adapter.reset()
        consultationViewModel.getUpcomingSchedule(token, ENTRY_SIZE, 1)
    }
}
