package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ItemClientRecordBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.ClientRecord

class ClientRecordListAdapter :
    RecyclerView.Adapter<ClientRecordListAdapter.ClientRecordViewHolder>() {
    private val clientRecords = ArrayList<ClientRecord>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientRecordViewHolder {
        val binding = DataBindingUtil.inflate<ItemClientRecordBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_client_record,
            parent,
            false
        )
        return ClientRecordViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return clientRecords.size
    }

    override fun onBindViewHolder(holder: ClientRecordViewHolder, position: Int) {
        holder.binding.clientRecord = clientRecords[position]
    }

    fun updateClientRecordList(clientRecordList: List<ClientRecord>) {
        clientRecords.clear()
        clientRecords.addAll(clientRecordList)
        notifyDataSetChanged()
    }

    class ClientRecordViewHolder(val binding: ItemClientRecordBinding) :
        RecyclerView.ViewHolder(binding.root)
}