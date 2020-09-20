package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.socketio

import java.util.*

class ChatMessageRequest(
    val message: String? = null,
    val scheduleId: Int? = null,
    val receiverId: Int? = null,
    val timestamp: Date? = null
)