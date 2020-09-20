package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.NotificationType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.socketio.CommonNotification
import org.json.JSONObject
import java.lang.ClassCastException
import java.net.URISyntaxException


class CommonNotificationService : Service() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var notificationManager: NotificationManagerCompat
    private var commonNotificationChannel: NotificationChannel? = null
    private var socket: Socket? = null
    private var userId = 0
    private var notificationId = 0
    private val jsonParser = JsonParser()


    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            commonNotificationChannel = NotificationChannel(
                NotificationType.COMMON.channel,
                NotificationType.COMMON.type,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(commonNotificationChannel!!)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        userId = sharedPref.getInt(USER_ID, 0)
        socket?.disconnect()
        socket?.off()
        if (socket == null || !socket!!.connected()) {
            openChatSocket()
        }
        return START_STICKY
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
                    socket!!.on("APPROVAL-${userId}") {
                        val data = it[0] as JSONObject
                        val gsonObject =
                            jsonParser.parse(data.toString()) as JsonObject
                        val serializedData = GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .setLenient()
                            .create()
                            .fromJson<CommonNotification>(
                                gsonObject,
                                CommonNotification::class.java
                            )
                        createCommonNotification(serializedData, "APPROVAL")
                    }
                        .on("REJECTION-${userId}") {
                            val data = it[0] as JSONObject
                            val gsonObject =
                                jsonParser.parse(data.toString()) as JsonObject
                            val serializedData = GsonBuilder()
                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                .setLenient()
                                .create()
                                .fromJson<CommonNotification>(
                                    gsonObject,
                                    CommonNotification::class.java
                                )
                            createCommonNotification(serializedData, "REJECTION")
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

    private fun createCommonNotification(
        serializedData: CommonNotification?,
        notificationType: String
    ) {
        if (serializedData != null) {
            when (notificationType) {
                "APPROVAL" -> if (serializedData.counselorName != null)
                    sendNormalLayoutNotification(
                        "${serializedData.counselorName} menyetujui permintaan jadwal konsultasi kamu",
                        NotificationType.COMMON.type,
                        "Pengajuan Konsultasi Disetujui"
                    )
                "REJECTION" -> if (serializedData.counselorName != null)
                    sendNormalLayoutNotification(
                        "${serializedData.counselorName} menolak permintaan jadwal konsultasi kamu",
                        NotificationType.COMMON.type,
                        "Pengajuan Konsultasi Ditolak"
                    )
            }
        }
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
                        this,
                        NotificationType.CHAT.channel
                    )
                    NotificationType.COMMON.type -> NotificationCompat.Builder(
                        this,
                        NotificationType.COMMON.channel
                    )
                    else -> NotificationCompat.Builder(this, NotificationType.COMMON.channel)
                }
            } else {
                NotificationCompat.Builder(this)
            }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("activeFragment", "consultationFragment")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder.setContentTitle("Konselink")
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        builder = builder
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        notificationManager.notify(notificationId++, builder.build())
    }
}