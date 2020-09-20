package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class CounselorDetail(
    @SerializedName("fullName")
    val fullname: String? = null,

    @SerializedName("specialization")
    val specialization: String? = null,

    @SerializedName("counselorId")
    val counselorId: Int? = null,

    @SerializedName("strNumber")
    val strNumber: String? = null,

    @SerializedName("sipNumber")
    val sipNumber: String? = null,

    @SerializedName("sspNumber")
    val sspNumber: String? = null,

    @SerializedName("displayPicturUrl")
    val displayPictureUrl: String? = null,

    @SerializedName("schedule")
    val schedule: List<Schedule>? = null
)
