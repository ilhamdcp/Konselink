package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName
import java.util.*

class ClientRequest(
    @SerializedName("requestId")
    val requestId: Int? = null,

    @SerializedName("clientId")
    val clientId: Int?  = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("displayPictureUrl")
    val displayPictureUrl: String? = null,

    @SerializedName("startTime")
    val startTime: String? = null,

    @SerializedName("endTime")
    val endTime: String? = null
)
