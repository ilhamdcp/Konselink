package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.chat.ConsultationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ItemRecyclerviewDiagnosisCodeBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.IcdDiagnosisCode
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel

class DiagnosisCodeListAdapter(private val consultationViewModel: ConsultationViewModel, private val activity: Context) : RecyclerView.Adapter<DiagnosisCodeListAdapter.DiagnosisViewHolder>() {
    private var diagnosisList: ArrayList<IcdDiagnosisCode> = ArrayList()
    var currentPage: Int = 1
    var totalPage: Int = 1
    var isLoading = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val binding = DataBindingUtil.inflate<ItemRecyclerviewDiagnosisCodeBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_recyclerview_diagnosis_code, parent, false
        )

        return DiagnosisViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return diagnosisList.size
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        holder.diagnosisBinding.icd = diagnosisList[position]
        holder.diagnosisBinding.root.setOnClickListener {
            consultationViewModel.handleDiagnosisCodeEditText(diagnosisList[position].codeId.toString())
            if (activity is ConsultationActivity) {
                val fragment =
                    activity.supportFragmentManager.findFragmentByTag("postConsultationFragment")
                Log.d("RESULT", (fragment != null).toString())
                if (fragment != null) {
                    activity.supportFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_in_up, R.anim.slide_out_up)
                        .replace(
                        R.id.consultation_fragment_container,
                        fragment,
                        "postConsultationFragment"
                    ).commit()
                }
            }

        }
    }

    fun updateDiagnosisList(diagnosisList: List<IcdDiagnosisCode>) {
        this.diagnosisList.addAll(diagnosisList)
        notifyDataSetChanged()
    }

    fun reset() {
        diagnosisList.clear()
        notifyDataSetChanged()
    }

    class DiagnosisViewHolder(val diagnosisBinding: ItemRecyclerviewDiagnosisCodeBinding) : RecyclerView.ViewHolder(diagnosisBinding.root) {}
}