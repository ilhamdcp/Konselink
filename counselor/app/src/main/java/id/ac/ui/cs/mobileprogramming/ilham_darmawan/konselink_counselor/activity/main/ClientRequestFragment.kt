package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.SHARED_PREF
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.TOKEN
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter.ClientRequestListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.FragmentMainClientRequestPageBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.listener.BaseRecyclerviewListener
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ClientService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ClientViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ClientViewModelFactory

class ClientRequestFragment : Fragment() {
    private val clientViewModel by lazy {
        ViewModelProvider(
            this, ClientViewModelFactory(
                requireContext(),
                ClientService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(ClientViewModel::class.java)
    }

    private val clientRequestListAdapter by lazy {
        ClientRequestListAdapter(clientViewModel)
    }

    private lateinit var sharedPref: SharedPreferences
    private lateinit var clientRequestListRecyclerView: RecyclerView
    private var token: String? = null
    private val ENTRY_SIZE = 15

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentMainClientRequestPageBinding>(
            inflater,
            R.layout.fragment_main_client_request_page,
            container,
            false
        )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }


        clientRequestListRecyclerView = binding.listClientRequestRecyclerview
        clientRequestListRecyclerView.adapter = clientRequestListAdapter
        clientRequestListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        clientRequestListRecyclerView.setHasFixedSize(true)
        sharedPref = activity?.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)!!
        token = sharedPref.getString(TOKEN, "")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assignElements()
        populateRecyclerView()
    }

    private fun assignElements() {
        clientRequestListAdapter.token = token
        clientRequestListRecyclerView.addOnScrollListener(object : BaseRecyclerviewListener(
            clientRequestListRecyclerView.layoutManager as LinearLayoutManager,
            15) {
            override fun loadMoreItems() {
                clientViewModel.getClientRequestList(token!!, ++clientRequestListAdapter.currentPage, ENTRY_SIZE)
            }

            override fun isLastPage(): Boolean {
                return clientRequestListAdapter.currentPage >= clientRequestListAdapter.totalPage
            }

            override fun isLoading(): Boolean {
                return clientRequestListAdapter.isLoading
            }

        })

        val swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.swipe_layout)
        swipeRefreshLayout?.setOnRefreshListener {
            clientRequestListAdapter.clear()
            clientViewModel.getClientRequestList(token!!, 1, ENTRY_SIZE)
        }

        clientViewModel.clientRequestListLiveData.observe(viewLifecycleOwner) {
            Log.d("CounselorFragment", "size: ${it.size}")
            swipeRefreshLayout?.isRefreshing = false
            clientRequestListAdapter.addData(it)
            clientRequestListAdapter.removeLoading()
        }

        clientViewModel.currentPageLiveData.observe(viewLifecycleOwner) {
            clientRequestListAdapter.currentPage = it
            if (clientRequestListAdapter.totalPage > clientRequestListAdapter.currentPage && !clientRequestListAdapter.isLoading) {
                clientRequestListAdapter.addLoading()
            } else if (clientRequestListAdapter.itemCount > 0) {
                clientRequestListAdapter.removeLoading()
            }
        }

        clientViewModel.totalPageLiveData.observe(viewLifecycleOwner) {
            clientRequestListAdapter.totalPage = it
            if (clientRequestListAdapter.totalPage > clientRequestListAdapter.currentPage && !clientRequestListAdapter.isLoading) {
                clientRequestListAdapter.addLoading()
            } else if (clientRequestListAdapter.itemCount > 0) {
                clientRequestListAdapter.removeLoading()
            }
        }

        clientViewModel.statusCode.observe(viewLifecycleOwner) {
            if (it == 200) {
                clientRequestListAdapter.notifyDataSetChanged()
                clientViewModel.resetStatusCode()
            }
        }
    }

    private fun populateRecyclerView() {
        clientViewModel.getClientRequestList(token!!, 1, 15)
    }
}
