package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.adapter

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.ClientDetailActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.databinding.ItemCalendarEventViewBinding
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.dateFormat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.ConsultationSession
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ConsultationScheduleListAdapter(private val context: Context, private val consultationViewModel: ConsultationViewModel) :
    RecyclerView.Adapter<ConsultationScheduleListAdapter.ConsultationScheduleViewHolder>() {
    private var consultationScheduleList = ArrayList<ConsultationSession>()
    var token: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConsultationScheduleViewHolder {
        val binding = DataBindingUtil.inflate<ItemCalendarEventViewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_calendar_event_view,
            parent,
            false
        )

        return ConsultationScheduleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return consultationScheduleList.size
    }

    override fun onBindViewHolder(
        holder: ConsultationScheduleViewHolder,
        position: Int
    ) {

        val item = consultationScheduleList[holder.adapterPosition]
        holder.consultationClient.text = item.clientName
        holder.consultationTime.text = item.time
        if (item.clientName != null && item.clientName.isNotBlank() && item.clientId!! > 0) {
            holder.consultationClient.setOnClickListener {
                val intent = Intent(context, ClientDetailActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    .putExtra("clientId", item.clientId)
                context.startActivity(intent)
            }
            holder.consultationReminder.setOnClickListener {
                enableReminderButton(item)
            }

            holder.consultationReminder.visibility = View.VISIBLE
        } else {
            holder.consultationClient.isClickable = false
            holder.consultationReminder.visibility = View.GONE
            holder.consultationReminder.isClickable = false
        }
    }

    private fun enableReminderButton(item: ConsultationSession) {
        val format = SimpleDateFormat(dateFormat)
        val timeStringArray = item.time!!.split("-")

        val startTime = Calendar.getInstance()
        val endTime = Calendar.getInstance()
        val startTimeString = "${consultationViewModel.selectedDate.value}T${timeStringArray[0]}:00.000Z"
        val endTimeString = "${consultationViewModel.selectedDate.value}T${timeStringArray[1]}:00.000Z"
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
                "Konsultasi melalui aplikasi Konselink dengan klien: ${item.clientName}"
            )
            .putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_BUSY
            )
        context.startActivity(intent)
    }

    fun updateScheduleList(scheduleList: List<ConsultationSession>) {
        consultationScheduleList.clear()
        val filteredScheduleList = scheduleList.filter {
            !consultationViewModel.deletedSchedule.value?.contains(it.scheduleId)!!
        }
        consultationScheduleList.addAll(filteredScheduleList)
        notifyDataSetChanged()
    }

    fun clearScheduleList() {
        consultationScheduleList.clear()
        notifyDataSetChanged()
    }

    fun deleteItem(adapterPosition: Int) {
        if (consultationScheduleList[adapterPosition].clientId == null ||
            consultationScheduleList[adapterPosition].clientId == 0) {
            consultationViewModel.deleteScheduleData(
                token!!,
                consultationScheduleList[adapterPosition].scheduleId!!
            )
            val deletedSchedule = consultationViewModel.deletedSchedule.value
            deletedSchedule?.add(consultationScheduleList[adapterPosition].scheduleId!!)
            consultationScheduleList.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
        } else {
            Toast.makeText(context, "Tidak dapat menghapus jadwal yang sudah dipesan oleh klien", Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }
    }

    class ConsultationScheduleViewHolder(binding: ItemCalendarEventViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val consultationTime = binding.root.findViewById<TextView>(R.id.consultation_session)
        val consultationClient = binding.root.findViewById<TextView>(R.id.consultation_client)
        val consultationReminder = binding.root.findViewById<ImageView>(R.id.consultation_add_reminder)
    }
}