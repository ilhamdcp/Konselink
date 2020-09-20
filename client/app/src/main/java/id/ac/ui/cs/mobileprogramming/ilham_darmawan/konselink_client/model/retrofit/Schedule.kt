package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class Schedule (
    @SerializedName("day")
    val day: String? = null,

    @SerializedName("time")
    val session: List<ConsultationSession>? = null

)