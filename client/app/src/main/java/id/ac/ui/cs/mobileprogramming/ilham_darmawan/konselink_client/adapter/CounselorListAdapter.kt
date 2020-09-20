package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.COUNSELOR_ID
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.CounselorDetailActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.ItemRecyclerviewCounselorBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.ItemRecyclerviewLoadingBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.Counselor


class CounselorListAdapter: RecyclerView.Adapter<ViewHolder>() {
    private val CONTENT_TYPE = 0
    private val LOADING_TYPE = 1
    private val LOADER_IDENTIFIER = "loaderIdentifier"
    private lateinit var context: Context
    var isLoading = false
    var counselorList = ArrayList<Counselor>()
    var currentPage = 1
    var totalPage = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        context = parent.context
        if (viewType == CONTENT_TYPE) {
            val counselorListItemBinding =
                DataBindingUtil.inflate<ItemRecyclerviewCounselorBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recyclerview_counselor, parent, false
                )

            return CounselorViewHolder(counselorListItemBinding)
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
        return counselorList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is CounselorViewHolder) {
            val counselor = counselorList[position]
            holder.counselorItemBinding.counselor = counselor

            holder.counselorItemBinding.root.setOnClickListener {
                val intent = Intent(holder.counselorItemBinding.root.context, CounselorDetailActivity::class.java)
                intent.putExtra(COUNSELOR_ID, counselor.counselorId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isLoading) {
            return if (position == counselorList.size - 1) LOADING_TYPE else CONTENT_TYPE;
        } else {
            return CONTENT_TYPE;
        }
    }

    fun addData(counselors: List<Counselor>) {
        if (counselorList.isNotEmpty()) {
            if (counselorList[counselorList.size-1].fullname.equals(LOADER_IDENTIFIER)) {
                counselorList.removeAt(counselorList.size-1)
            }
        }
        counselorList.addAll(counselors)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoading = true;
        counselorList.add(Counselor(fullname = LOADER_IDENTIFIER));
        notifyItemInserted(counselorList.size - 1);
    }
    fun removeLoading() {
        isLoading = false;
        val position = counselorList.size - 1;
        val item = counselorList[position]
        if (item != null && item.fullname == LOADER_IDENTIFIER) {
            counselorList.removeAt(position);
            notifyItemRemoved(position);
        }
    }

    fun clear() {
        counselorList.clear()
        currentPage = 1
        totalPage = 1
        isLoading = false
    }


    class CounselorViewHolder(val counselorItemBinding: ItemRecyclerviewCounselorBinding): ViewHolder(counselorItemBinding.root)

    class LoadingViewHolder(val loadingItemBinding: ItemRecyclerviewLoadingBinding): ViewHolder(loadingItemBinding.root)
}