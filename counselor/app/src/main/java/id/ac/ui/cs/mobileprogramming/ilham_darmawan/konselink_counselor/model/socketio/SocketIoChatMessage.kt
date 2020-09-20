package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.socketio

import com.stfalcon.chatkito.commons.models.IMessage
import com.stfalcon.chatkito.commons.models.IUser
import java.util.*

class SocketIoChatMessage(private val id: String, private val createdAt: Date, private val user: IUser, private val text: String) : IMessage {

    override fun getId(): String {
        return id
    }

    override fun getCreatedAt(): Date {
        return createdAt
    }

    override fun getUser(): IUser {
        return user
    }

    override fun getText(): String {
        return text
    }

}