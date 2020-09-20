package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class ConsultationSchedule(
    @SerializedName("day")
    val day: String? = null,

    @SerializedName("time")
    val session: List<ConsultationSession>? = null
)