package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.chat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter.DiagnosisCodeListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.FragmentConsultationDiagnosisPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.BaseRecyclerviewListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.DiagnosisSearchTextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import kotlinx.android.synthetic.main.fragment_consultation_diagnosis_page.*


class DiagnosisSearchFragment(private val consultationViewModel: ConsultationViewModel) :
    Fragment() {
    private lateinit var token: String
    private val ENTRY_SIZE = 15
    private lateinit var sharedPref: SharedPreferences
    private lateinit var adapter:   DiagnosisCodeListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DataBindingUtil.inflate<FragmentConsultationDiagnosisPageBinding>(
            inflater,
            R.layout.fragment_consultation_diagnosis_page,
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

    override fun onResume() {
        super.onResume()
        observeViewModel()
    }

    private fun assignElements() {
        sharedPref = requireContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        token = sharedPref.getString(TOKEN, "")!!
        adapter = DiagnosisCodeListAdapter(consultationViewModel, requireContext())
        recyclerview_diagnosis.adapter = adapter
        recyclerview_diagnosis.layoutManager = LinearLayoutManager(requireContext())
        recyclerview_diagnosis.setHasFixedSize(true)
        form_search_diagnosis.addTextChangedListener(DiagnosisSearchTextFormListener(consultationViewModel, adapter, sharedPref.getString(TOKEN, "")!!, ENTRY_SIZE))

        recyclerview_diagnosis.addOnScrollListener(object : BaseRecyclerviewListener(recyclerview_diagnosis.layoutManager as LinearLayoutManager, ENTRY_SIZE) {

            override fun loadMoreItems() {
                consultationViewModel.getIcdDiagnosis(token, ENTRY_SIZE, ++adapter.currentPage, form_search_diagnosis.text.toString())
            }

            override fun isLastPage(): Boolean {
                return adapter.currentPage >= adapter.totalPage
            }

            override fun isLoading(): Boolean {
                return adapter.isLoading
            }
        })
    }

    private fun observeViewModel() {
        consultationViewModel.icdDiagnosisListLiveData.observe(viewLifecycleOwner) {
            adapter.updateDiagnosisList(it)
        }

        consultationViewModel.icdCurrentPageLiveData.observe(viewLifecycleOwner) {
            adapter.currentPage = it
        }

        consultationViewModel.icdTotalPageLiveData.observe(viewLifecycleOwner) {
            adapter.totalPage = it
        }
    }

}