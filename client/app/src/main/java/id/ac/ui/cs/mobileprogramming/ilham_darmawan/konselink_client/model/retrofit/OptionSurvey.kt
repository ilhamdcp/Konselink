package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class OptionSurvey(
    @SerializedName("answerKey")
    val answerKey: String? = null,

    @SerializedName("answerValue")
    val answerValue: Int? = null
)
