package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit

import com.google.gson.annotations.SerializedName

class ChatHistoryListResponse (
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("chat")
    val chat: List<Chat>? = null
)