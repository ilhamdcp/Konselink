package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class UserDataResponse(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("user")
    val userData: UserData? = null
)