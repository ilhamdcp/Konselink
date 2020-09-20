package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.adapter.CounselorListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.FormType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.FragmentMainCounselorPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.BaseRecyclerviewListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.listener.TextFormListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.CounselorService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.CounselorViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.CounselorViewModelFactory
import java.util.*

class CounselorFragment : Fragment() {
    private val counselorViewModel by lazy {
        ViewModelProvider(
            this, CounselorViewModelFactory(
                requireContext(),
                CounselorService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(CounselorViewModel::class.java)
    }

    private val counselorListAdapter by lazy {
        CounselorListAdapter()
    }

    private lateinit var sharedPref: SharedPreferences

    private lateinit var counselorListRecyclerView: RecyclerView

    private var timerHasCancelled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentMainCounselorPageBinding>(
            inflater,
            R.layout.fragment_main_counselor_page,
            container,
            false
        )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        counselorListRecyclerView = binding.listCounselorRecyclerview

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        counselorListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!

        assignElements()
        populateRecyclerView()
    }

    private fun assignElements() {
        counselorListRecyclerView.adapter = counselorListAdapter
        counselorListRecyclerView.addOnScrollListener(object : BaseRecyclerviewListener(
            counselorListRecyclerView.layoutManager as LinearLayoutManager,
            15) {
            override fun loadMoreItems() {
                sharedPref.getString(TOKEN, "")?.let { counselorViewModel.getCounselorList(it, ++counselorListAdapter.currentPage, 15) }
            }

            override fun isLastPage(): Boolean {
                return counselorListAdapter.currentPage >= counselorListAdapter.totalPage
            }

            override fun isLoading(): Boolean {
                return counselorListAdapter.isLoading
            }

        })

        val searchEditText = view?.findViewById<EditText>(R.id.search_counselor)
        searchEditText?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                counselorListAdapter.clear()
                sharedPref.getString(TOKEN, "")?.let { counselorViewModel.getCounselorList(it, 1, 15) }
                true
            } else {
                false
            }
        }

        searchEditText?.addTextChangedListener(TextFormListener(counselorViewModel, FormType.SEARCH_COUNSELOR.typeName))

        val swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.swipe_layout)
        swipeRefreshLayout?.setOnRefreshListener {
            counselorListAdapter.clear()
            sharedPref.getString(TOKEN, "")?.let { counselorViewModel.getCounselorList(it, 1, 15) }
        }

        counselorViewModel.counselorListLiveData.observe(viewLifecycleOwner) {
            swipeRefreshLayout?.isRefreshing = false
            counselorListAdapter.addData(it)
        }

        counselorViewModel.currentPageLiveData.observe(viewLifecycleOwner) {
            counselorListAdapter.currentPage = it
            if (counselorListAdapter.totalPage > counselorListAdapter.currentPage && !counselorListAdapter.isLoading) {
                counselorListAdapter.addLoading()
            } else if (counselorListAdapter.itemCount > 0) {
                counselorListAdapter.removeLoading()
            }
        }

        counselorViewModel.totalPageLiveData.observe(viewLifecycleOwner) {
            counselorListAdapter.totalPage = it
            if (counselorListAdapter.totalPage > counselorListAdapter.currentPage && !counselorListAdapter.isLoading) {
                counselorListAdapter.addLoading()
            } else if (counselorListAdapter.itemCount > 0) {
                counselorListAdapter.removeLoading()
            }
        }

        var timer = Timer()
        counselorViewModel.keywordLiveData.observe(viewLifecycleOwner) {
            if (timerHasCancelled) {
                timer.cancel()
                timer = Timer()
            }
            timer.schedule(object : TimerTask() {
                override fun run() {
                    counselorListAdapter.clear()
                    sharedPref.getString(TOKEN, "")?.let { counselorViewModel.getCounselorList(it, 1, 15) }
                }

            }, 1000)
            timerHasCancelled = true
        }
    }

    private fun populateRecyclerView() {
        sharedPref.getString(TOKEN, "")?.let { counselorViewModel.getCounselorList(it, 1, 15) }
    }
}
