package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName
import java.util.*

class ConsultationSession(
    @SerializedName("scheduleId")
    val scheduleId: Int? = null,

    @SerializedName("day")
    val day: String? = null,

    @SerializedName("time")
    val time: String? = null,

    @SerializedName("clientId")
    val clientId: Int? = null,

    @SerializedName("clientName")
    val clientName: String? = null
)
