package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class SrqResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("result")
    val result: Srq? = null
)