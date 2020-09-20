package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.adapter

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.databinding.ItemRecyclerviewConsultationBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.dateFormat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.ConsultationSession
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpcomingConsultationListAdapter(private val context: Context) :
    RecyclerView.Adapter<UpcomingConsultationListAdapter.UpcomingConsultationViewHolder>() {
    val isLoading = true
    private var upcomingConsultations: ArrayList<ConsultationSession> = ArrayList()
    var currentPage = 1
    var totalPage = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpcomingConsultationViewHolder {
        val binding = DataBindingUtil.inflate<ItemRecyclerviewConsultationBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_recyclerview_consultation,
            parent,
            false
        )
        return UpcomingConsultationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return upcomingConsultations.size
    }

    override fun onBindViewHolder(holder: UpcomingConsultationViewHolder, position: Int) {
        holder.binding.consultation = upcomingConsultations[position]
        val item = upcomingConsultations[position]
        if (item.counselorId!! > 0 && item.counselorName != null && item.day != null && item.time != null) {
            holder.consultationReminder.visibility = View.VISIBLE
            holder.consultationReminder.isClickable = true
            holder.consultationReminder.setOnClickListener {
                enableReminderButton(item)
            }
        } else {
            holder.consultationReminder.visibility = View.GONE
            holder.consultationReminder.isClickable = false
        }
    }

    private fun enableReminderButton(item: ConsultationSession) {
        val format = SimpleDateFormat(dateFormat)
        val timeStringArray = item.time!!.split("-")
        val consultationDate = item.day!!.split("/")

        val startTime = Calendar.getInstance()
        val endTime = Calendar.getInstance()
        val startTimeString = "${consultationDate[2]}-${consultationDate[1]}-${consultationDate[0]}T${timeStringArray[0]}:00.000Z"
        val endTimeString = "${consultationDate[2]}-${consultationDate[1]}-${consultationDate[0]}T${timeStringArray[1]}:00.000Z"
        startTime.time = format.parse(startTimeString)!!
        endTime.time = format.parse(endTimeString)!!


        val startMillis: Long = Calendar.getInstance().run {
            set(startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(
                Calendar.DATE), startTime.get(Calendar.HOUR_OF_DAY), startTime.get(
                Calendar.MINUTE))
            timeInMillis
        }
        val endMillis: Long = Calendar.getInstance().run {
            set(endTime.get(Calendar.YEAR), endTime.get(Calendar.MONTH), endTime.get(
                Calendar.DATE), endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE))
            timeInMillis
        }
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            .putExtra(CalendarContract.Events.TITLE, "Konsultasi Konselink")
            .putExtra(
                CalendarContract.Events.DESCRIPTION,
                "Konsultasi melalui aplikasi Konselink dengan psikolog: ${item.counselorName}"
            )
            .putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_BUSY
            )
        context.startActivity(intent)
    }

    fun addData(upcomingConsultationList: List<ConsultationSession>) {
        val currentSize = upcomingConsultations.size
        upcomingConsultations.addAll(upcomingConsultationList)
        notifyItemRangeInserted(currentSize, upcomingConsultationList.size)
    }

    fun reset() {
        upcomingConsultations.clear()
        notifyDataSetChanged()
    }

    class UpcomingConsultationViewHolder(val binding: ItemRecyclerviewConsultationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val consultationReminder = binding.root.findViewById<ImageView>(R.id.consultation_add_reminder)
    }
}