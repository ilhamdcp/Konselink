package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class IpipAndSrqRequest(
    @SerializedName("SRQ")
    val srq: List<OptionSurvey>? = null,

    @SerializedName("IPIP")
    val ipip: List<OptionSurvey>? = null
)