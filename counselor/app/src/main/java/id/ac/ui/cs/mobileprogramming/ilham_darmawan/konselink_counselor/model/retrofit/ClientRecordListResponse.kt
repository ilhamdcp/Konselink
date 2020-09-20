package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class ClientRecordListResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("record")
    val record: List<ClientRecord>? = null
)