package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ItemRecyclerviewClientRequestBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ItemRecyclerviewLoadingBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.ClientRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ClientViewModel


class ClientRequestListAdapter(val clientViewModel: ClientViewModel): RecyclerView.Adapter<ViewHolder>() {
    private val CONTENT_TYPE = 0
    private val LOADING_TYPE = 1
    private val LOADER_IDENTIFIER = "loaderIdentifier"
    private lateinit var context: Context
    var token: String? = null
    var isLoading = false
    var clientRequestList = ArrayList<ClientRequest>()
    var currentPage = 1
    var totalPage = 1

    init {
        Log.d("INIT", "init called")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        if (viewType == CONTENT_TYPE) {
            val counselorListItemBinding =
                DataBindingUtil.inflate<ItemRecyclerviewClientRequestBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recyclerview_client_request, parent, false
                )

            return ClientRequestViewHolder(counselorListItemBinding)
        } else {
            val loadingItemBinding =
                DataBindingUtil.inflate<ItemRecyclerviewLoadingBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recyclerview_loading, parent, false
                )

            return LoadingViewHolder(loadingItemBinding)
        }
    }

    override fun getItemCount(): Int {
        return clientRequestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ClientRequestViewHolder) {
            val clientRequest = clientRequestList[position]
            holder.clientRequestBinding.clientRequest = clientRequest

            holder.acceptButton.setOnClickListener {
                clientViewModel.approveClientRequest(token!!, clientRequest.requestId!!)
                clientRequestList.removeAt(position)
                notifyItemRemoved(position)
            }

            holder.rejectButton.setOnClickListener {
                clientViewModel.rejectClientRequest(token!!, clientRequest.requestId!!)
                clientRequestList.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isLoading) {
            return if (position == clientRequestList.size - 1) LOADING_TYPE else CONTENT_TYPE;
        } else {
            return CONTENT_TYPE;
        }
    }

    fun addData(counselors: List<ClientRequest>) {
        if (clientRequestList.isNotEmpty()) {
            if (clientRequestList[clientRequestList.size-1].name.equals(LOADER_IDENTIFIER)) {
                clientRequestList.removeAt(clientRequestList.size-1)
            }
        }
        clientRequestList.addAll(counselors)
        notifyDataSetChanged()
        notifyItemRangeInserted(0, 2)
    }

    fun addLoading() {
        isLoading = true;
        clientRequestList.add(ClientRequest(name = LOADER_IDENTIFIER));
        notifyItemInserted(clientRequestList.size - 1);
    }
    fun removeLoading() {
        isLoading = false;
        val position = clientRequestList.size - 1;
        if (position > -1) {
            val item = clientRequestList[position]
            if (item != null && item.name == LOADER_IDENTIFIER) {
                clientRequestList.removeAt(position);
                notifyItemRemoved(position);
            }
        }
    }

    fun clear() {
        clientRequestList.clear()
        currentPage = 1
        totalPage = 1
        isLoading = false
    }

    class ClientRequestViewHolder(val clientRequestBinding: ItemRecyclerviewClientRequestBinding): ViewHolder(clientRequestBinding.root) {
        val acceptButton = clientRequestBinding.root.findViewById<ImageView>(R.id.button_accept_request)
        val rejectButton = clientRequestBinding.root.findViewById<ImageView>(R.id.button_reject_request)

        fun bind(token: String, requestId: Int) {

        }
    }

    class LoadingViewHolder(val loadingItemBinding: ItemRecyclerviewLoadingBinding): ViewHolder(loadingItemBinding.root)
}