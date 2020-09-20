package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.ItemRecyclerviewCounselorScheduleBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.ConsultationSession
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Consultation
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.CounselorViewModel

class CounselorScheduleTimeListAdapter(private val context: Context, private val counselorViewModel: CounselorViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var scheduleDay: String? = null
    var scheduleTime: ArrayList<ConsultationSession> = ArrayList()
    var token: String? = null
    var counselorId: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val counselorScheduleTimeItemBinding =
            DataBindingUtil.inflate<ItemRecyclerviewCounselorScheduleBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_recyclerview_counselor_schedule, parent, false
            )

        return CounselorScheduleTimeViewHolder(counselorScheduleTimeItemBinding)
    }

    override fun getItemCount(): Int {
        return scheduleTime.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CounselorScheduleTimeViewHolder) {
            holder.counselorScheduleTimeItemBinding.counselorScheduleTime = scheduleTime[position].time
            holder.requestButton.setOnClickListener {
                counselorViewModel.requestSchedule(token!!, scheduleTime[position].scheduleId!!)
                val bookedSchedule = counselorViewModel.bookedSchedules.value
                bookedSchedule?.add(scheduleTime[position].scheduleId!!)
                counselorViewModel.bookedSchedules.value = bookedSchedule

                scheduleTime.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
            }
        }
    }

    fun updateInterval(day: String?, session: List<ConsultationSession>?) {
        if (day != null && session != null && session.isNotEmpty()) {
            var freeSchedule = session.filter {
                !counselorViewModel.bookedSchedules.value?.contains(it.scheduleId)!!
            }
            scheduleTime = freeSchedule as ArrayList<ConsultationSession>
            notifyDataSetChanged()
        }
    }

    class CounselorScheduleTimeViewHolder(val counselorScheduleTimeItemBinding: ItemRecyclerviewCounselorScheduleBinding): RecyclerView.ViewHolder(counselorScheduleTimeItemBinding.root) {
        val requestButton = counselorScheduleTimeItemBinding.root.findViewById<Button>(R.id.button_request_schedule)
    }
}