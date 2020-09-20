package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class CounselorListResponse (
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("pageNo")
    val currentPage: Int? = null,

    @SerializedName("totalPage")
    val totalPage: Int? = null,

    @SerializedName("data")
    val counselorList: List<Counselor>? = null
)
