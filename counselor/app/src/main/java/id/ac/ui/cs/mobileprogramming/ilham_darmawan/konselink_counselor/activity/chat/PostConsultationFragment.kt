package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ClientRecordFormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ConsultationStatus
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.SymptomVisibility
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.FragmentConsultationPostConsultationPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.ClientRecordSpinnerFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.ClientRecordTextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.fragment_consultation_post_consultation_page.*

class PostConsultationFragment : Fragment() {
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
    private var scheduleId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentConsultationPostConsultationPageBinding>(
            inflater,
            R.layout.fragment_consultation_post_consultation_page,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = consultationViewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        populateSpinner()
        assignElements()
    }

    private fun observeViewModel() {
        consultationViewModel.postClientRecordStatusCodeLiveData.observe(viewLifecycleOwner) {
            if (it == 200) {
                Thread {
                    consultationViewModel.insertOrUpdateConsultation(scheduleId, ConsultationStatus.END.code)
                }.start()
                activity?.finish()
            } else {
                button_save_client_record.isClickable = true
                button_save_client_record.background = resources.getDrawable(R.drawable.button_blue_gradient, null)
            }
        }
    }

    private fun populateSpinner() {
        val selfHarmAdapter = ArrayAdapter<SymptomVisibility>(
            requireActivity(),
            R.layout.item_spinner,
            R.id.text_spinner,
            SymptomVisibility.values()
        )
        spinner_self_harm_risk.adapter = selfHarmAdapter

        val suicideAdapter = ArrayAdapter<SymptomVisibility>(
            requireActivity(),
            R.layout.item_spinner,
            R.id.text_spinner,
            SymptomVisibility.values()
        )
        spinner_suicide_risk.adapter = suicideAdapter

        val othersHarmAdapter = ArrayAdapter<SymptomVisibility>(
            requireActivity(),
            R.layout.item_spinner,
            R.id.text_spinner,
            SymptomVisibility.values()
        )
        spinner_other_harm_risk.adapter = othersHarmAdapter


    }

    private fun assignElements() {
        scheduleId = activity?.intent?.getIntExtra("scheduleId", 0)!!
        sharedPref = requireContext().getSharedPreferences(BuildConfig.SHARED_PREF, Context.MODE_PRIVATE)
        val tempValue = consultationViewModel.clientRecordLiveData.value
        tempValue?.scheduleId = scheduleId
        consultationViewModel.clientRecordLiveData.value = tempValue

        form_diagnosis.addTextChangedListener(ClientRecordTextFormListener(consultationViewModel, ClientRecordFormType.DIAGNOSIS.typeName))
        form_physical_health_history.addTextChangedListener(ClientRecordTextFormListener(consultationViewModel, ClientRecordFormType.PHYSICAL_HEALTH_HISTORY.typeName))
        form_medical_consumption.addTextChangedListener(ClientRecordTextFormListener(consultationViewModel, ClientRecordFormType.MEDICAL_CONSUMPTION.typeName))
        form_assessment.addTextChangedListener(ClientRecordTextFormListener(consultationViewModel, ClientRecordFormType.ASSESSMENT.typeName))
        form_consultation_purpose.addTextChangedListener(ClientRecordTextFormListener(consultationViewModel, ClientRecordFormType.CONSULTATION_PURPOSE.typeName))
        form_treatment_plan.addTextChangedListener(ClientRecordTextFormListener(consultationViewModel, ClientRecordFormType.TREATMENT_PLAN.typeName))
        form_meetings.addTextChangedListener(ClientRecordTextFormListener(consultationViewModel, ClientRecordFormType.MEETINGS.typeName))
        form_notes.addTextChangedListener(ClientRecordTextFormListener(consultationViewModel, ClientRecordFormType.NOTES.typeName))
        form_problem_description.addTextChangedListener(ClientRecordTextFormListener(consultationViewModel,  ClientRecordFormType.PROBLEM_DESCRIPTION.typeName))

        spinner_suicide_risk.onItemSelectedListener = ClientRecordSpinnerFormListener(consultationViewModel, ClientRecordFormType.SUICIDE_RISK.typeName)
        spinner_self_harm_risk.onItemSelectedListener = ClientRecordSpinnerFormListener(consultationViewModel, ClientRecordFormType.SELF_HARM_RISK.typeName)
        spinner_other_harm_risk.onItemSelectedListener = ClientRecordSpinnerFormListener(consultationViewModel, ClientRecordFormType.OTHERS_HARM_RISK.typeName)

        form_diagnosis_code.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val fragment = DiagnosisSearchFragment(consultationViewModel)
                activity?.supportFragmentManager!!.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_in_down, R.anim.slide_out_down)
                    .addToBackStack("postConsultationFragment")
                    .replace(
                        R.id.consultation_fragment_container,
                        fragment,
                        "diagnosisSearchFragment"
                    )
                    .show(fragment).commit()
            }
        }

        button_save_client_record.setOnClickListener {
            if (consultationViewModel.postConsultationPageIsValid()) {
                button_save_client_record.isClickable = false
                button_save_client_record.background =
                    resources.getDrawable(R.drawable.button_disabled, null)
                consultationViewModel.postClientRecordData(sharedPref.getString(TOKEN, "")!!)
            } else {
                Toast.makeText(requireContext(), "Mohon lengkapi data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}