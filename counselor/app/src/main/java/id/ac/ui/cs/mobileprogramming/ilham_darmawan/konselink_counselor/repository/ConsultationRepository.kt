package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.Chat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.ChatHistoryListResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.Consultation
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
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

    private val _consultationScheduleListLiveData = MutableLiveData<List<ConsultationSchedule>>()
    val consultationScheduleListLiveData: LiveData<List<ConsultationSchedule>> =
        _consultationScheduleListLiveData

    private val _statusCode = MutableLiveData<Int>()
    val statusCode: LiveData<Int> = _statusCode

    private val _ongoingScheduleLiveData = MutableLiveData<ConsultationSession>()
    val ongoingScheduleLiveData: LiveData<ConsultationSession> = _ongoingScheduleLiveData

    private val _upcomingScheduleLiveData = MutableLiveData<ConsultationSession>()
    val upcomingScheduleLiveData: LiveData<ConsultationSession> = _upcomingScheduleLiveData

    private val _ongoingStatusCodeLiveData = MutableLiveData<Int>()
    val ongoingStatusCodeLiveData: LiveData<Int> = _ongoingStatusCodeLiveData

    private val _chatListLiveData = MutableLiveData<List<Chat>>(ArrayList())
    val chatListLiveData: LiveData<List<Chat>> = _chatListLiveData

    private val _icdDiagnosisCodeListLiveData = MutableLiveData<List<IcdDiagnosisCode>>(ArrayList())
    val icdDiagnosisListLiveData: LiveData<List<IcdDiagnosisCode>> = _icdDiagnosisCodeListLiveData

    private val _icdDiagnosisCurrentPageLiveData = MutableLiveData<Int>()
    val icdDiagnosisCurrentPageLiveData: LiveData<Int> = _icdDiagnosisCurrentPageLiveData

    private val _icdDiagnosisTotalPageLiveData = MutableLiveData<Int>()
    val icdDiagnosisTotalPageLiveData: LiveData<Int> = _icdDiagnosisTotalPageLiveData

    private val _preConsultationSurveyListLiveData = MutableLiveData<List<PreConsultationSurvey>>()
    val preConsultationSurveyListLiveData: LiveData<List<PreConsultationSurvey>> =
        _preConsultationSurveyListLiveData

    private val _clientRecordListLiveData = MutableLiveData<List<ClientRecord>>(ArrayList())
    val clientRecordListLiveData: LiveData<List<ClientRecord>> = _clientRecordListLiveData

    private val _upcomingStatusCodeLiveData = MutableLiveData<Int>()
    val upcomingStatusCodeLiveData: LiveData<Int> = _upcomingStatusCodeLiveData

    private val _chatListStatusCodeLiveData = MutableLiveData<Int>()
    val chatListStatusCodeLiveData: LiveData<Int> = _chatListStatusCodeLiveData

    private val _postClientRecordStatusCodeLiveData = MutableLiveData<Int>()
    val postClientRecordStatusCodeLiveData: LiveData<Int> = _postClientRecordStatusCodeLiveData

    private val _deleteScheduleStatusCodeLiveData = MutableLiveData<Int>()
    val deleteScheduleStatusCodeLiveData: LiveData<Int> = _deleteScheduleStatusCodeLiveData

    fun retrieveConsultationScheduleList(token: String, month: Int, year: Int) {
        consultationService.retrieveConsultationSchedule(token, month, year)
            .enqueue(ScheduleListCallbackHandler(context, _consultationScheduleListLiveData))
    }

    fun postNewScheduleData(
        token: String,
        startDate: String,
        endDate: String,
        sessionNum: Int,
        workdays: String,
        startTime: String,
        interval: Int
    ) {
        val scheduleRequest =
            NewScheduleRequest(startDate, endDate, sessionNum, workdays, startTime, interval)
        Log.d("new schedule", Gson().toJson(scheduleRequest))

        consultationService.postNewScheduleData(token, scheduleRequest).enqueue(
            UserRepository.BasicCallbackHandler(
                context,
                _statusCode,
                "Gagal menambahkan jadwal"
            )
        )
    }

    fun deleteScheduleData(token: String, scheduleId: Int) {
        consultationService.deleteScheduleData(token, scheduleId).enqueue(UserRepository.BasicCallbackHandler(context, _deleteScheduleStatusCodeLiveData, "Gagal menghapus jadwal konsultasi"))
    }

    fun getUpcomingSchedule(token: String, entrySize: Int, pageNo: Int) {
        _upcomingStatusCodeLiveData.value = 0
        consultationService.retrieveUpcomingSchedule(token, entrySize, pageNo).enqueue(
            UpcomingConsultationCallbackHandler(
                context,
                _upcomingScheduleLiveData,
                _upcomingStatusCodeLiveData
            )
        )
    }

    fun getOngoingSchedule(token: String) {
        _ongoingStatusCodeLiveData.value = 0
        consultationService.retrieveOngoingSchedule(token).enqueue(
            OngoingConsultationCallbackHandler(
                context,
                _ongoingScheduleLiveData,
                _ongoingStatusCodeLiveData
            )
        )
    }

    fun retrieveNewChat(token: String, scheduleId: Int, timestamp: String?) {
        consultationService.retrieveChatHistory(token, scheduleId, timestamp).enqueue(
            ChatHistoryCallbackHandler(
                context,
                _chatListLiveData,
                _chatListStatusCodeLiveData
            )
        )
    }

    fun getConsultation(scheduleId: Int): LiveData<Consultation> {
        return consultationDao.getConsultation(scheduleId)
    }

    fun insertOrUpdateConsultation(consultation: Consultation) {
        consultationDao.insertOrUpdateConsultation(consultation)
    }

    fun postClientRecordData(token: String, clientRecord: ClientRecord) {
        consultationService.postClientRecord(token, clientRecord).enqueue(
            UserRepository.BasicCallbackHandler(
                context,
                _postClientRecordStatusCodeLiveData,
                "Gagal menyimpan record klien"
            )
        )
    }

    fun retrieveIcdDiagnosis(token: String, entrySize: Int, pageNo: Int, keyword: String) {
        consultationService.retrieveIcdDiagnosis(token, entrySize, pageNo, keyword)
            .enqueue(
                IcdDiagnosisCallbackHandler(
                    context,
                    _icdDiagnosisCodeListLiveData,
                    _icdDiagnosisTotalPageLiveData,
                    _icdDiagnosisCurrentPageLiveData
                )
            )
    }

    fun retrievePreConsultationSurvey(token: String, scheduleId: Int) {
        consultationService.retrievePreConsultationSurvey(token, scheduleId).enqueue(
            PreConsultationSurveyCallbackHandler(
                context,
                _preConsultationSurveyListLiveData
            )
        )
    }

    fun retrieveClientRecord(token: String, clientId: Int) {
        consultationService.retrieveClientRecord(token, clientId).enqueue(ClientRecordListCallbackHandler(context, _clientRecordListLiveData))
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
                call.clone()
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
                Toast.makeText(context, "Gagal mengambil jadwal konsultasi", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    internal class UpcomingConsultationCallbackHandler(
        private val context: Context,
        private val livedata: MutableLiveData<ConsultationSession>,
        private val statusCode: MutableLiveData<Int>
    ) : Callback<UpcomingConsultationListResponse> {
        private var hasFailed = false
        override fun onFailure(call: Call<UpcomingConsultationListResponse>, t: Throwable) {
            if (!hasFailed) {
                hasFailed = true
                call.clone()
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
                    livedata.value = response.body()?.consultationSession?.get(0)
                } else {
                    livedata.value = null
                }
            } else {
                Toast.makeText(context, "Gagal mengambil jadwal konsultasi", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    internal class ScheduleListCallbackHandler(
        private val context: Context,
        private val liveData: MutableLiveData<List<ConsultationSchedule>>
    ) : Callback<ConsultationScheduleListResponse> {
        var hasFailed = false
        override fun onFailure(call: Call<ConsultationScheduleListResponse>, t: Throwable) {
            t.printStackTrace()
            if (!hasFailed) {
                call.clone()
                hasFailed = true
            }
        }

        override fun onResponse(
            call: Call<ConsultationScheduleListResponse>,
            response: Response<ConsultationScheduleListResponse>
        ) {
            if (response.isSuccessful && response.body()?.code == 200) {
                Log.d("scheduleId", response.body()?.consultationSchedule?.size.toString())
                liveData.value = response.body()?.consultationSchedule
            } else {
                Toast.makeText(context, "Gagal mengambil jadwal konsultasi", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    internal class ChatHistoryCallbackHandler(
        private val context: Context,
        private val livedata: MutableLiveData<List<Chat>>,
        private val statusCode: MutableLiveData<Int>
    ) : Callback<ChatHistoryListResponse> {
        private var hasFailed = false

        override fun onFailure(call: Call<ChatHistoryListResponse>, t: Throwable) {
            if (!hasFailed) {
                hasFailed = true
                call.clone()
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

    internal class IcdDiagnosisCallbackHandler(
        private val context: Context,
        private val icdDiagnosis: MutableLiveData<List<IcdDiagnosisCode>>,
        private val totalPage: MutableLiveData<Int>, private val currentPage: MutableLiveData<Int>
    ) : Callback<DiagnosisCodeListResponse> {
        private var hasFailed = false

        override fun onFailure(call: Call<DiagnosisCodeListResponse>, t: Throwable) {
            if (!hasFailed) {
                hasFailed = true
                call.clone()
            }
            t.printStackTrace()
        }

        override fun onResponse(
            call: Call<DiagnosisCodeListResponse>,
            response: Response<DiagnosisCodeListResponse>
        ) {
            if (response.isSuccessful && response.body()?.code == 200) {
                if (response.body()?.icdCodes?.isNotEmpty()!!) {
                    totalPage.value = response.body()?.totalPage
                    currentPage.value = response.body()?.pageNo
                    icdDiagnosis.value = response.body()?.icdCodes
                }
            } else {
                Toast.makeText(context, "Gagal mengambil kode diagnosis", Toast.LENGTH_SHORT).show()
            }
        }

    }

    internal class PreConsultationSurveyCallbackHandler(
        private val context: Context,
        private val livedata: MutableLiveData<List<PreConsultationSurvey>>
    ) : Callback<PreConsultationSurveyListResponse> {
        private var hasFailed = false

        override fun onFailure(call: Call<PreConsultationSurveyListResponse>, t: Throwable) {
            if (!hasFailed) {
                hasFailed = true
                call.clone()
            }
            t.printStackTrace()
        }

        override fun onResponse(
            call: Call<PreConsultationSurveyListResponse>,
            response: Response<PreConsultationSurveyListResponse>
        ) {
            if (response.isSuccessful && response.body()?.code == 200) {
                if (response.body()?.survey.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "Klien belum mengisi survei prakonsultasi",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                livedata.value = response.body()?.survey
            } else {
                Toast.makeText(
                    context,
                    "Gagal mengambil hasil survei prakonsultasi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    internal class ClientRecordListCallbackHandler(
        private val context: Context,
        private val livedata: MutableLiveData<List<ClientRecord>>
    ): Callback<ClientRecordListResponse> {
        private var hasFailed = false

        override fun onFailure(call: Call<ClientRecordListResponse>, t: Throwable) {
            if (!hasFailed) {
                hasFailed = true
                call.clone()
            }
            t.printStackTrace()
        }

        override fun onResponse(
            call: Call<ClientRecordListResponse>,
            response: Response<ClientRecordListResponse>
        ) {
            if (response.isSuccessful && response.body()?.code == 200) {
                livedata.value = response.body()?.record
            } else {
                Toast.makeText(context, "Gagal mengambil record klien", Toast.LENGTH_SHORT).show()
            }
        }

    }
}