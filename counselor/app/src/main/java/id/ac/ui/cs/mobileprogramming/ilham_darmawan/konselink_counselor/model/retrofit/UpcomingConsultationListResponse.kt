package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class UpcomingConsultationListResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("schedule")
    val consultationSession: List<ConsultationSession>? = null
)