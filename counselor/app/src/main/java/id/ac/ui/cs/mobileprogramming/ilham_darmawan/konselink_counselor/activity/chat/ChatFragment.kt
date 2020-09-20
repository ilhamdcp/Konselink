package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.chat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.ClientDetailActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.ClientRecordActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.socketio.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.service.ChatNotificationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ConsultationViewModelFactory
import kotlinx.android.synthetic.main.fragment_consultation_chat_page.*
import kotlinx.android.synthetic.main.toolbar_chat.*
import org.json.JSONObject
import java.lang.ClassCastException
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

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
    private lateinit var sender: SocketIoChatUser
    private lateinit var recipient: SocketIoChatUser
    private lateinit var clientName: String
    private lateinit var userName: String
    private lateinit var socket: Socket
    private lateinit var messagesListAdapter: MessagesListAdapter<SocketIoChatMessage>
    private var timestamp: Date? = null
    private var scheduleId: Int = 0
    private var userId: Int = 0
    private var chatId = 0
    private var clientId = 0
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
        toolbar.inflateMenu(R.menu.chat_menu)
        assignElements()
        observeViewModel()
    }

    private fun initializeViewModel() {
        consultationViewModel.getChatHistory(
            sharedPref.getString(TOKEN, "")!!,
            scheduleId,
            null
        )
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
        intent.putExtra("recipientId", clientId)
        intent.putExtra("recipientName", clientName)
        intent.putExtra("scheduleId", scheduleId)
        intent.putExtra("userName", userName)
        requireContext().startService(intent)
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
                    temp.add(socketIoChatMessage)
                } else if (chat.senderId == sender.id.toInt()) {
                    val socketIoChatMessage = SocketIoChatMessage(
                        chatId++.toString(),
                        chat.timestamp!!,
                        sender,
                        chat.message!!
                    )
                    temp.add(socketIoChatMessage)
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
        clientName = activity?.intent?.getStringExtra("clientName")!!
        clientId = activity?.intent?.getIntExtra("clientId", 0)!!
        scheduleId = activity?.intent?.getIntExtra("scheduleId", 0)!!
        userId = sharedPref.getInt(USER_ID, 0)
        sender = SocketIoChatUser(userName, userId.toString())
        recipient = SocketIoChatUser(clientName, clientId.toString())

        val shapeImageView = view?.findViewById<ImageView>(R.id.diplay_picture_chat_user)
        insertPicture(shapeImageView!!, "")

        text_chat_user.text = clientName
        text_chat_user.setOnClickListener {
            val intent = Intent(requireContext(), ClientDetailActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra("clientId", clientId)
            startActivity(intent)
        }
        text_chat_user.isSingleLine = true
        text_chat_user.ellipsize = TextUtils.TruncateAt.MARQUEE
        text_chat_user.isSelected = true

        //We can pass any data to ViewHolder with payload
        messagesListAdapter = MessagesListAdapter(sender.id, null)
        messagesList.setAdapter(messagesListAdapter, true)

        input.setInputListener {
            val currDate = Calendar.getInstance().time
            val messageBody = ChatMessageRequest(it.toString(), scheduleId, clientId, currDate)
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

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.chat_action_end -> {
                    val fragment = PostConsultationFragment()
                    activity?.supportFragmentManager!!.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(
                            R.id.consultation_fragment_container,
                            fragment,
                            "postConsultationFragment"
                        )
                        .addToBackStack(null)
                        .show(fragment).commit()
                }

                R.id.chat_action_see_preconsultation -> {
                    val fragment = PreConsultationResultFragment()
                    activity?.supportFragmentManager!!.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                        .replace(
                            R.id.consultation_fragment_container,
                            fragment,
                            "preConsultationResultFragment"
                        )
                        .addToBackStack("postConsultationFragment")
                        .show(fragment).commit()
                }

                R.id.chat_action_client_record -> {
                    val intent = Intent(requireContext(), ClientRecordActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra("clientId", clientId)
                    startActivity(intent)
                }
            }
            true
        }
    }

    private fun parseMessage(chatMessageRequest: Any): JsonObject {
        return jsonParser.parse(gson.toJson(chatMessageRequest)).asJsonObject
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
            Toast.makeText(requireContext(), "Format URI tidak valid", Toast.LENGTH_SHORT).show()
        } catch (e: ClassCastException) {
            Toast.makeText(requireContext(), "Format data tidak valid", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}