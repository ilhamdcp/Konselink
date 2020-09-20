package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit

import com.google.gson.annotations.SerializedName

class UpdateProfileRequest(
    @SerializedName("hasDisplayPicture")
    var hasDisplayPicture: Boolean? = false,

    @SerializedName("name")
    var nickname: String? = null
)