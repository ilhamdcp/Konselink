package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class RequestScheduleRequest (
    @SerializedName("counselorId")
    val userId: Int? = null,

    @SerializedName("day")
    val day: String? = null,

    @SerializedName("time")
    val time: String? = null

)