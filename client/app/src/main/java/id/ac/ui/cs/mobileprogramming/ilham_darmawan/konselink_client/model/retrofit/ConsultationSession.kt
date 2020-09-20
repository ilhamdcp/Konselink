package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class ConsultationSession(
    @SerializedName("scheduleId")
    val scheduleId: Int? = null,

    @SerializedName("day")
    val day: String? = null,

    @SerializedName("time")
    val time: String? = null,

    @SerializedName("counselorId")
    val counselorId: Int? = null,

    @SerializedName("counselorName")
    val counselorName: String? = null
)
