package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class PreConsultationSurveyRequest(
    @SerializedName("scheduleId")
    val scheduleId: Int? = null,

    @SerializedName("survey")
    val survey: List<OptionSurvey>? = null
)