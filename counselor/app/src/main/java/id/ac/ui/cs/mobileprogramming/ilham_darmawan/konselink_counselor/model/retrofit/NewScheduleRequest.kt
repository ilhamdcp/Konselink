package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class NewScheduleRequest(
    @SerializedName("startDate")
    val startDate: String? = null,

    @SerializedName("endDate")
    val endDate: String? = null,

    @SerializedName("sessionNum")
    val sessionNum: Int? = null,

    @SerializedName("workDays")
    val workDays: String? = null,

    @SerializedName("startTime")
    val startTime: String? = null,

    @SerializedName("interval")
    val interval: Int? = null
)