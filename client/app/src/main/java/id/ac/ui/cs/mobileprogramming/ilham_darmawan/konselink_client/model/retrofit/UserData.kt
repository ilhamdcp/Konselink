package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class UserData (
    @SerializedName("username")
    var username: String? = null,

    @SerializedName("faculty")
    var faculty: String? = null,

    @SerializedName("fullName")
    var fullname: String? = null,

    @SerializedName("nickName")
    var nickname: String? = null,

    @SerializedName("academicId")
    var academicId: String? = null,

    @SerializedName("programStudi")
    var studyProgram: String? = null,

    @SerializedName("programEdukasi")
    var educationalProgram: String? = null,

    @SerializedName("userRole")
    var role: String? = null,

    @SerializedName("isVerified")
    var isVerified: Boolean? = false,

    @SerializedName("isRegistered")
    var isRegistered: Boolean? = false,

    @SerializedName("id")
    var userId: Int? = null,

    @SerializedName("displayPictureUrl")
    var displayPictureUrl: String? = null
)