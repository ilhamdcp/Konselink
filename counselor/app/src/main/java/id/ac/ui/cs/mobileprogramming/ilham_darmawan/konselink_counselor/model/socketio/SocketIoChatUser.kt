package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.socketio

import com.stfalcon.chatkito.commons.models.IUser

class SocketIoChatUser(private val name: String, private val id: String) :IUser {
    override fun getAvatar(): String {
        return ""
    }

    override fun getName(): String {
        return name
    }

    override fun getId(): String {
        return id
    }

}