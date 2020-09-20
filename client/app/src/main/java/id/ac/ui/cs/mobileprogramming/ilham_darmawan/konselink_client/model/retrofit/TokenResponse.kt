package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class TokenResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("token")
    val token: String? = null
)