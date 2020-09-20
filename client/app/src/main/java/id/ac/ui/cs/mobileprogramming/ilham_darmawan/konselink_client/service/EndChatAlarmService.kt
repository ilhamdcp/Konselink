package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.nkzawa.socketio.client.IO
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.ConsultationStatus
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Consultation
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.socketio.EndChatRequest

class EndChatAlarmService : BroadcastReceiver() {
    private val jsonParser = JsonParser()
    private val gson = GsonBuilder()
        .setLenient()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    override fun onReceive(context: Context?, intent: Intent?) {
        val clientId = intent?.getIntExtra("clientId", 0)!!
        val scheduleId = intent.getIntExtra("scheduleId", 0)!!
        val endChatRequest = EndChatRequest(scheduleId, clientId)
        val endChatPayload = parseMessage(endChatRequest)
        val db = ApplicationDatabase.getInstance(context!!)!!
        val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        val token = sharedPref.getString(TOKEN, "")
        val socketOption = IO.Options()
        socketOption.query = "token=${token!!}"
        val socket = IO.socket(WEBSOCKET_URL, socketOption).connect()

        if (socket.connected()) {
            socket.emit("END_CHAT", endChatPayload)
            socket.disconnect()
        }

        db.consultationDao().getConsultation(scheduleId).observeForever {
            if (it.status!! <= ConsultationStatus.CHAT.code) {
                db.consultationDao().insertOrUpdateConsultation(
                    Consultation(
                        scheduleId = scheduleId,
                        status = ConsultationStatus.END.code
                    )
                )
            }
        }
    }

    private fun parseMessage(chatMessageRequest: Any): JsonObject {
        return jsonParser.parse(gson.toJson(chatMessageRequest)).asJsonObject
    }
}