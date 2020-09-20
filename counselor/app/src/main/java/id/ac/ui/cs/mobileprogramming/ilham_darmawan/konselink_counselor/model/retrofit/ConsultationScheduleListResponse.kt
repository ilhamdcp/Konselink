package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class ConsultationScheduleListResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("schedule")
    val consultationSchedule: List<ConsultationSchedule>? = null
)