package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class CounselorDetailResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("data")
    val counselorDetail: CounselorDetail
)