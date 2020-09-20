package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.chat.ConsultationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.NotificationType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.socketio.ChatMessageNotification
import okhttp3.internal.wait
import org.json.JSONObject
import java.lang.ClassCastException
import java.lang.Exception
import java.net.URISyntaxException
import java.util.*

class ChatNotificationService : Service() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var notificationManager: NotificationManagerCompat
    private var chatNotificationChannel: NotificationChannel? = null
    private var socket: Socket? = null
    private var userId = 0
    private var notificationId = 0
    private val jsonParser = JsonParser()
    private var recipientId = 0
    private var recipientName: String? = null
    private var userName: String? = null
    private var scheduleId = 0


    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        notificationManager = NotificationManagerCompat.from(this)

        userId = sharedPref.getInt(USER_ID, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chatNotificationChannel = NotificationChannel(
                NotificationType.CHAT.channel,
                NotificationType.CHAT.type,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(chatNotificationChannel!!)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            recipientId = intent.getIntExtra("recipientId", 0)
            recipientName = intent.getStringExtra("recipientName")
            scheduleId = intent.getIntExtra("scheduleId", 0)
            userName = intent.getStringExtra("userName")
        }

        if (socket == null || !socket!!.connected()) {
            openChatSocket()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.off()
        socket = socket?.disconnect()
    }

    private fun openChatSocket() {
        try {
            val socketOption = IO.Options()
            socketOption.query = "token=" + sharedPref.getString(TOKEN, "")
            socket = IO.socket(WEBSOCKET_URL, socketOption)
            Thread {
                var counter = 0
                while (!socket!!.connected() && counter < 5) {
                    socket = socket!!.connect()
                    counter++
                    Thread.sleep(3000)
                }
                if (socket!!.connected()) {
                    Log.d("CHAT SERVICE", "CONNECTED")
                    socket!!.on("CHAT-${userId}") {
                        val data = it[0] as JSONObject
                        val gsonObject =
                            jsonParser.parse(data.toString()) as JsonObject
                        val serializedData = GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .setLenient()
                            .create()
                            .fromJson<ChatMessageNotification>(
                                gsonObject,
                                ChatMessageNotification::class.java
                            )
                        createChatNotification(serializedData)
                    }
                }
            }.start()

        } catch (e: URISyntaxException) {
            e.printStackTrace()
            Log.d("NotificationService", "invalid URI syntax at NotificationService")
        } catch (e: ClassCastException) {
            e.printStackTrace()
            Log.d("NotificationService", "Invalid object format received at NotificationService")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("NotificationService", "Caught exception")
        }
    }

    private fun createChatNotification(serializedData: ChatMessageNotification?) {
        sendNormalLayoutNotification(
            "Terdapat pesan baru",
            NotificationType.CHAT.type,
            "Pesan Baru"
        )
    }

    private fun sendNormalLayoutNotification(
        content: String,
        notificationType: String,
        title: String
    ) {
        var builder: NotificationCompat.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (notificationType) {
                    NotificationType.CHAT.type -> NotificationCompat.Builder(
                        applicationContext,
                        NotificationType.CHAT.channel
                    )
                    NotificationType.COMMON.type -> NotificationCompat.Builder(
                        applicationContext,
                        NotificationType.COMMON.channel
                    )
                    else -> NotificationCompat.Builder(
                        applicationContext,
                        NotificationType.COMMON.channel
                    )
                }
            } else {
                NotificationCompat.Builder(applicationContext)
            }

        val intent = if (recipientId > 0 && recipientName != null && recipientName!!.isNotBlank() &&
            scheduleId > 0 && userId > 0 && userName != null && userName!!.isNotBlank()
        ) {
            Intent(this, ConsultationActivity::class.java).apply {
                putExtra("scheduleId", scheduleId)
                putExtra("clientId", recipientId)
                putExtra("userId", userId)
                putExtra("userName", userName)
                putExtra("clientName", recipientName)
            }
        } else {
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder = builder
            .setSmallIcon(R.drawable.logo_konselink_counselor)
            .setSmallIcon(R.drawable.logo_konselink_counselor)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        notificationManager.notify(notificationId++, builder.build())

    }

}