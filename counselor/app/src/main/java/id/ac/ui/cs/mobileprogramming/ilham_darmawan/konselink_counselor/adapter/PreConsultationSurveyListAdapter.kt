package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ItemPreconsultationResultBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.PreConsultationSurvey

class PreConsultationSurveyListAdapter :
    RecyclerView.Adapter<PreConsultationSurveyListAdapter.PreConsultationResult>() {
    private var preConsultationResults = ArrayList<PreConsultationSurvey>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreConsultationResult {
        val binding = DataBindingUtil.inflate<ItemPreconsultationResultBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_preconsultation_result,
            parent,
            false
        )

        return PreConsultationResult(binding)
    }

    override fun getItemCount(): Int {
        return preConsultationResults.size
    }

    override fun onBindViewHolder(holder: PreConsultationResult, position: Int) {
        holder.binding.result = preConsultationResults[position]
    }

    fun updateItems(preConsultationResultList: List<PreConsultationSurvey>) {
        preConsultationResults.clear()
        preConsultationResults.addAll(preConsultationResultList)
        notifyDataSetChanged()
    }

    class PreConsultationResult(val binding: ItemPreconsultationResultBinding) :
        RecyclerView.ViewHolder(binding.root)
}