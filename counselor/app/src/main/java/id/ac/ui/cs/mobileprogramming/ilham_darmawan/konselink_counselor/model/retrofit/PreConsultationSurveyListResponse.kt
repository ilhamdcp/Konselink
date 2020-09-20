package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.PreConsultationSurvey

class PreConsultationSurveyListResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("result")
    val survey: List<PreConsultationSurvey>? = null
)
