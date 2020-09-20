package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class Counselor(
    @SerializedName("counselorId")
    var counselorId: Int? = null,

    @SerializedName("fullName")
    val fullname: String? = null,

    @SerializedName("specialization")
    val specialization: String? = null,

    @SerializedName("displayPictureUrl")
    val displayPictureUrl: String? = null
)
