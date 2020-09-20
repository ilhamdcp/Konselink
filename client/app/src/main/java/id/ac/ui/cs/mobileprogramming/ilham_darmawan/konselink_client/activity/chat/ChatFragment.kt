package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.chat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.stfalcon.chatkito.messages.MessagesListAdapter
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.socketio.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.service.ChatNotificationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.fragment_consultation_chat_page.*
import kotlinx.android.synthetic.main.toolbar_chat.*
import org.json.JSONObject
import java.lang.ClassCastException
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {
    private val consultationViewModel by lazy {
        ViewModelProvider(
            this, ConsultationViewModelFactory(
                requireContext(),
                ConsultationService.create(),
                ApplicationDatabase.getInstance(requireContext())!!
            )
        ).get(ConsultationViewModel::class.java)
    }

    private lateinit var sharedPref: SharedPreferences
    private lateinit var userSocketIo: SocketIoChatUser
    private lateinit var counselorName: String
    private lateinit var userName: String
    private lateinit var socket: Socket
    private lateinit var messagesListAdapter: MessagesListAdapter<SocketIoChatMessage>
    private var scheduleId: Int = 0
    private var userId: Int = 0
    private var chatId = 0
    private var counselorId = 0
    private var timestamp: Date? = null
    private lateinit var sender: SocketIoChatUser
    private lateinit var recipient: SocketIoChatUser
    private val jsonParser = JsonParser()
    private val gson =
        GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consultation_chat_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignElements()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        messagesListAdapter.clear(true)
        initializeViewModel()
        openChatSocket()
    }

    override fun onPause() {
        super.onPause()
        with(sharedPref.edit()) {
            putInt(CHAT_ID, chatId)
            commit()
        }
        messagesListAdapter.clear(true)
        socket = socket.disconnect()
        socket.off()

        val intent = Intent(requireContext(), ChatNotificationService::class.java)
        intent.putExtra("recipientId", counselorId)
        intent.putExtra("recipientName", counselorName)
        intent.putExtra("scheduleId", scheduleId)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        requireContext().startService(intent)
    }

    private fun initializeViewModel() {
        consultationViewModel.getChatHistory(
            sharedPref.getString(TOKEN, "")!!,
            scheduleId,
            null
        )
    }

    private fun observeViewModel() {
        consultationViewModel.chatListLiveData.observe(viewLifecycleOwner) {
            val temp = ArrayList<SocketIoChatMessage>()
            it.forEach { chat ->
                if (chat.senderId == recipient.id.toInt()) {
                    val socketIoChatMessage = SocketIoChatMessage(
                        chatId++.toString(),
                        chat.timestamp!!,
                        recipient,
                        chat.message!!
                    )
                    messagesListAdapter.addToStart(socketIoChatMessage, true)
                } else if (chat.senderId == sender.id.toInt()) {
                    val socketIoChatMessage = SocketIoChatMessage(
                        chatId++.toString(),
                        chat.timestamp!!,
                        sender,
                        chat.message!!
                    )
                    messagesListAdapter.addToStart(socketIoChatMessage, true)
                }
                timestamp = chat.timestamp
            }
            messagesListAdapter.addToEnd(temp, true)
        }
    }


    private fun assignElements() {
        sharedPref = requireContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        chatId = sharedPref.getInt(CHAT_ID, 0)
        userName = activity?.intent?.getStringExtra("userName")!!
        counselorName = activity?.intent?.getStringExtra("counselorName")!!
        counselorId = activity?.intent?.getIntExtra("counselorId", 0)!!
        scheduleId = activity?.intent?.getIntExtra("scheduleId", 0)!!
        userId = sharedPref.getInt(USER_ID, 0)
        userSocketIo = SocketIoChatUser(userName, userId.toString())
        sender = SocketIoChatUser(userName, userId.toString())
        recipient = SocketIoChatUser(counselorName, counselorId.toString())


        val shapeImageView = view?.findViewById<ImageView>(R.id.diplay_picture_chat_user)
        insertPicture(shapeImageView!!, "")

        text_chat_user.text = counselorName
        text_chat_user.isSingleLine = true
        text_chat_user.ellipsize = TextUtils.TruncateAt.MARQUEE
        text_chat_user.isSelected = true

        //We can pass any data to ViewHolder with payload
        messagesListAdapter = MessagesListAdapter(userSocketIo.id, null)
        messagesList.setAdapter(messagesListAdapter, true)

        input.setInputListener {
            val currDate = Calendar.getInstance().time
            val messageBody = ChatMessageRequest(it.toString(), scheduleId, counselorId, currDate)

            socket.emit("CHAT", parseMessage(messageBody))
            messagesListAdapter.addToStart(
                SocketIoChatMessage(
                    chatId++.toString(),
                    currDate,
                    sender,
                    it.toString()
                ), true
            )
            timestamp = currDate
            true
        }
    }

    private fun insertPicture(imageView: ImageView, imageURL: String) {
        Glide.with(imageView.context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .circleCrop()
            )
            .load(imageURL)
            .placeholder(R.drawable.user_placeholder)
            .into(imageView)
    }

    private fun parseMessage(messageObject: Any): JsonObject {
        return jsonParser.parse(gson.toJson(messageObject)).asJsonObject
    }

    private fun openChatSocket() {
        try {
            val options = IO.Options()
            options.query = "token=${sharedPref.getString(TOKEN, "")!!}"
            socket = IO.socket(WEBSOCKET_URL, options)
            Thread {
                var counter = 0
                while (!socket.connected() && counter < 5) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Mencoba menghubungi socket",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    socket = socket.connect()
                    counter++
                    Thread.sleep(3000)
                }

                if (socket.connected()) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Socket terhubung", Toast.LENGTH_SHORT).show()
                    }
                    socket.on("CHAT-${userId}") {
                        val data = it[0] as JSONObject
                        val gsonObject =
                            jsonParser.parse(data.toString()) as JsonObject
                        val serializedData = gson
                            .fromJson<ChatMessageNotification>(
                                gsonObject,
                                ChatMessageNotification::class.java
                            )
                        timestamp = serializedData.timestamp
                        requireActivity().runOnUiThread {
                            messagesListAdapter.addToStart(
                                SocketIoChatMessage(
                                    chatId++.toString(),
                                    serializedData.timestamp!!,
                                    recipient,
                                    serializedData.message!!
                                ), true
                            )
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Gagal menghubungi socket",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.start()
        } catch (e: URISyntaxException) {
            Toast.makeText(requireContext(), "Format URI tidak valid", Toast.LENGTH_SHORT)
                .show()
        } catch (e: ClassCastException) {
            Toast.makeText(requireContext(), "Format data tidak valid", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Gagal menghubungi server", Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }
}