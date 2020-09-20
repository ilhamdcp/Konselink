package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName
import java.util.*

class Chat(
    @SerializedName("scheduleId")
    val scheduleId: Int? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("senderId")
    val senderId: Int? = null,
    @SerializedName("timestamp")
    val timestamp: Date? = null
)
