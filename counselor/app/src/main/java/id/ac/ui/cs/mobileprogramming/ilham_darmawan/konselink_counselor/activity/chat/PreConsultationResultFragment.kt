package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter.PreConsultationSurveyListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.FragmentConsultationPreConsultationPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.fragment_consultation_pre_consultation_page.*

class PreConsultationResultFragment : Fragment() {
    private val consultationViewModel by lazy {
        ViewModelProvider(
            this, ConsultationViewModelFactory(
                requireContext(),
                ConsultationService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(ConsultationViewModel::class.java)
    }
    private val adapter = PreConsultationSurveyListAdapter()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var token: String
    private var scheduleId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentConsultationPreConsultationPageBinding>(
            inflater,
            R.layout.fragment_consultation_pre_consultation_page,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
        }.root
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

    private fun assignElements() {
        sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!
        token = sharedPref.getString(TOKEN, "")!!
        scheduleId = activity?.intent?.getIntExtra("scheduleId", 0)!!

        recyclerview_pre_consultation_survey_response.layoutManager =
            LinearLayoutManager(requireContext())
        recyclerview_pre_consultation_survey_response.setHasFixedSize(true)
        recyclerview_pre_consultation_survey_response.adapter = adapter
    }

    private fun observeViewModel() {
        consultationViewModel.preConsultationSurveyListLiveData.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                adapter.updateItems(it)
            }
        }
    }

    private fun initializeViewModel() {
        consultationViewModel.getPreConsultationSurvey(token, scheduleId)
    }
}