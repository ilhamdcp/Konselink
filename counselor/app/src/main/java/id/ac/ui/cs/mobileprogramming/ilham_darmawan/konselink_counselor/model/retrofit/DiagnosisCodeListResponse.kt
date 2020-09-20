package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

data class DiagnosisCodeListResponse (
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("icdCodes")
    val icdCodes: List<IcdDiagnosisCode>? = null,

    @SerializedName("pageNo")
    val pageNo: Int? = null,

    @SerializedName("totalPage")
    val totalPage: Int? = null
)