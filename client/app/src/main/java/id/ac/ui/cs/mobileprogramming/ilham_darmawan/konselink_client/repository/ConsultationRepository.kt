package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Consultation
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.ConsultationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class ConsultationRepository(
    private val context: Context,
    private val consultationService: ConsultationService,
    private val db: ApplicationDatabase
) {
    private val consultationDao = db.consultationDao()

    private val _ongoingScheduleLiveData = MutableLiveData<ConsultationSession>()
    val ongoingScheduleLiveData: LiveData<ConsultationSession> = _ongoingScheduleLiveData

    private val _upcomingScheduleLiveData = MutableLiveData<ConsultationSession>()
    val upcomingScheduleLiveData: LiveData<ConsultationSession> = _upcomingScheduleLiveData

    private val _chatListLiveData = MutableLiveData<List<Chat>>(ArrayList())
    val chatListLiveData: LiveData<List<Chat>> = _chatListLiveData

    private val _upcomingScheduleListLiveData = MutableLiveData<List<ConsultationSession>>()
    val upcomingScheduleListLiveData: LiveData<List<ConsultationSession>> = _upcomingScheduleListLiveData

    private val _ongoingStatusCodeLiveData = MutableLiveData<Int>()
    val ongoingStatusCodeLiveData: LiveData<Int> = _ongoingStatusCodeLiveData
    private val _upcomingStatusCodeLiveData = MutableLiveData<Int>()
    val upcomingStatusCodeLiveData: LiveData<Int> = _upcomingStatusCodeLiveData
    private val _chatListStatusCodeLiveData = MutableLiveData<Int>()
    val chatListStatusCodeLiveData: LiveData<Int> = _chatListStatusCodeLiveData
    private val _postPreConsultationCodeLiveData = MutableLiveData<Int>()
    val postPreConsultationCodeLiveData: LiveData<Int> = _postPreConsultationCodeLiveData

    fun getUpcomingSchedule(token: String, entrySize: Int, pageNo: Int) {
        _upcomingStatusCodeLiveData.value = 0
        consultationService.retrieveUpcomingSchedule(token, entrySize, pageNo).enqueue(UpcomingConsultationCallbackHandler(context, _upcomingScheduleLiveData, _upcomingScheduleListLiveData,  _upcomingStatusCodeLiveData))
    }

    fun getOngoingSchedule(token: String) {
        _ongoingStatusCodeLiveData.value = 0
        consultationService.retrieveOngoingSchedule(token).enqueue(OngoingConsultationCallbackHandler(context, _ongoingScheduleLiveData, _ongoingStatusCodeLiveData))
    }

    fun retrieveNewChat(token: String, scheduleId: Int, timestamp: String?) {
        consultationService.retrieveChatHistory(token, scheduleId, timestamp).enqueue(ChatHistoryCallbackHandler(context, _chatListLiveData, _chatListStatusCodeLiveData))
    }

    fun postPreConsultationSurvey(token: String, preConsultationSurveyRequest: PreConsultationSurveyRequest) {
        consultationService.postPreConsultationSurvey(token, preConsultationSurveyRequest).enqueue(UserRepository.BasicCallbackHandler(context, _postPreConsultationCodeLiveData, "Gagal menyimpan hasil survei"))
    }

    fun getConsultation(scheduleId: Int): LiveData<Consultation> {
        return consultationDao.getConsultation(scheduleId)
    }

    fun insertOrUpdateConsultation(consultation: Consultation) {
        consultationDao.insertOrUpdateConsultation(consultation)
    }



    internal class OngoingConsultationCallbackHandler(
        private val context: Context,
        private val livedata: MutableLiveData<ConsultationSession>,
        private val statusCode: MutableLiveData<Int>
    ) : Callback<OngoingConsultationResponse> {
        private var hasFailed = false
        override fun onFailure(call: Call<OngoingConsultationResponse>, t: Throwable) {
            if (!hasFailed) {
                hasFailed = true
                call.clone().enqueue(this)
            }
            t.printStackTrace()
        }

        override fun onResponse(
            call: Call<OngoingConsultationResponse>,
            response: Response<OngoingConsultationResponse>
        ) {
            if (response.body()?.code != null) {
                statusCode.value = response.body()?.code
            }
            if (response.isSuccessful && response.body()?.code == 200) {
                livedata.value = response.body()?.consultationSession
            } else {
                Toast.makeText(context, "Gagal mengambil jadwal konsultasi", Toast.LENGTH_SHORT).show()
            }
        }
    }


    internal class UpcomingConsultationCallbackHandler(
        private val context: Context,
        private val livedata: MutableLiveData<ConsultationSession>,
        private val listLivedata: MutableLiveData<List<ConsultationSession>>,
        private val statusCode: MutableLiveData<Int>
    ) : Callback<UpcomingConsultationListResponse> {
        private var hasFailed = false
        override fun onFailure(call: Call<UpcomingConsultationListResponse>, t: Throwable) {
            if (!hasFailed) {
                hasFailed = true
                call.clone().enqueue(this)
            }
            t.printStackTrace()
        }

        override fun onResponse(
            call: Call<UpcomingConsultationListResponse>,
            response: Response<UpcomingConsultationListResponse>
        ) {
            if (response.body()?.code != null) {
                statusCode.value = response.body()?.code
            }
            if (response.isSuccessful && response.body()?.code == 200) {
                if (response.body()?.consultationSession?.isNotEmpty()!!) {
                    listLivedata.value = response.body()?.consultationSession
                    livedata.value = response.body()?.consultationSession?.get(0)
                } else {
                    livedata.value = null
                }
            } else {
                Toast.makeText(context, "Gagal mengambil jadwal konsultasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal class ChatHistoryCallbackHandler(
    private val context: Context,
    private val livedata: MutableLiveData<List<Chat>>,
    private val statusCode: MutableLiveData<Int>
    ): Callback<ChatHistoryListResponse> {
        private var hasFailed = false

        override fun onFailure(call: Call<ChatHistoryListResponse>, t: Throwable) {
            if (!hasFailed) {
                hasFailed = true
                call.clone().enqueue(this)
            }
            t.printStackTrace()
        }

        override fun onResponse(
            call: Call<ChatHistoryListResponse>,
            response: Response<ChatHistoryListResponse>
        ) {
            if (response.body()?.code != null) {
                statusCode.value = response.body()?.code
            }

            if (response.isSuccessful && response.body()?.code == 200) {
                if (response.body()?.chat?.isNotEmpty()!!) {
                    livedata.value = response.body()?.chat
                }
            } else {
                Toast.makeText(context, "Gagal mengambil pesan baru", Toast.LENGTH_SHORT).show()
            }
        }

    }
}