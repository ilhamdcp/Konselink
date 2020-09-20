package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class PreConsultationSurvey(
    @SerializedName("answerKey")
    val answerKey: String? = null,

    @SerializedName("answerValue")
    val answerValue: Int? = null
)
