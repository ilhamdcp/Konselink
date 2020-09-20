package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class UpdateProfileRequest(
    @SerializedName("hasDisplayPicture")
    var hasDisplayPicture: Boolean? = null,

    @SerializedName("name")
    var nickname: String
)