package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

data class IcdDiagnosisCode(
    @SerializedName("codeId")
    val codeId: Int? = null,

    @SerializedName("icd9Code")
    val icd9Code: String? = null,

    @SerializedName("icd10Code")
    val icd10Code: String? = null,

    @SerializedName("disorder")
    val disorder: String? = null
)
