package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ClientService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientRepository(
    private val clientService: ClientService,
    private val db: ApplicationDatabase,
    private val context: Context
) {
    private val _clientRequestListLiveData = MutableLiveData<List<ClientRequest>>()
    val clientRequestListLiveData: MutableLiveData<List<ClientRequest>> = _clientRequestListLiveData

    private val _totalPage = MutableLiveData<Int>()
    val totalPage: LiveData<Int> = _totalPage

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage

    private val _statusCode = MutableLiveData<Int>()
    val statusCode: LiveData<Int> = _statusCode

    private val _clientDetailLiveData = MutableLiveData<ClientDetail>()
    val clientDetailLiveData: LiveData<ClientDetail> = _clientDetailLiveData

    private val _ipipLiveData = MutableLiveData<Ipip>()
    val ipipLiveData: LiveData<Ipip> = _ipipLiveData

    private val _srqLiveData = MutableLiveData<Srq>()
    val srqLiveData: LiveData<Srq> = _srqLiveData

    fun getClientRequestList(token: String, pageNo: Int, entrySize: Int, keyword: String?) {
        clientService.retrieveClientRequestList(token, pageNo, entrySize, keyword)
            .enqueue(
                ClientRequestListCallbackHandler(
                    _clientRequestListLiveData,
                    _currentPage,
                    _totalPage
                )
            )
    }

    fun approveClientRequest(token: String, requestId: Int) {
        clientService.approveClientRequest(token, requestId)
            .enqueue(ClientRequestUpdateCallbackHandler(_statusCode))
    }

    fun rejectClientRequest(token: String, requestId: Int) {
        clientService.rejectClientRequest(token, requestId)
            .enqueue(ClientRequestUpdateCallbackHandler(_statusCode))
    }

    fun resetStatusCode() {
        _statusCode.value = 0
    }

    fun retrieveClientDetail(token: String, clientId: Int) {
        clientService.retrieveClientDetail(token, clientId).enqueue(ClientDetailCallbackHandler(context, _clientDetailLiveData))
    }

    fun retrieveIpipSurvey(token: String, clientId: Int) {
        clientService.retrieveIpipSurvey(token, clientId).enqueue(IpipSurveyCallbackHandler(context, _ipipLiveData))
    }

    fun retrieveSrqSurvey(token: String, clientId: Int) {
        clientService.retrieveSrqSurvey(token, clientId).enqueue(SrqSurveyCallbackHandler(context, _srqLiveData))
    }

    internal class ClientRequestListCallbackHandler(
        private val clientRequestListLiveData: MutableLiveData<List<ClientRequest>>,
        private val currentPage: MutableLiveData<Int>,
        private val totalPage: MutableLiveData<Int>
    ) : Callback<ClientRequestListResponse> {
        private var hasFailed = false

        override fun onFailure(call: Call<ClientRequestListResponse>, t: Throwable) {
            t.printStackTrace()
            if (!hasFailed) {
                call.clone()
                hasFailed = true
            }
        }

        override fun onResponse(
            call: Call<ClientRequestListResponse>,
            response: Response<ClientRequestListResponse>
        ) {
            if (response.isSuccessful && response.body()!!.code == 200) {
                clientRequestListLiveData.value = response.body()!!.clientRequestList
                currentPage.value = response.body()!!.currentPage
                totalPage.value = response.body()!!.totalPage
            }
        }
    }

    internal class ClientRequestUpdateCallbackHandler(
        private val statusCode: MutableLiveData<Int>
    ) : Callback<BasicResponse> {
        private var hasFailed = false

        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            t.printStackTrace()
            if (!hasFailed) {
                call.clone()
                hasFailed = true
            }
        }

        override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
            if (response.isSuccessful) {
                statusCode.value = response.body()!!.code
            }
        }
    }

    internal class ClientDetailCallbackHandler(
        private val context: Context,
        private val livedata: MutableLiveData<ClientDetail>
    ) : Callback<ClientDetailResponse> {
        private var hasFailed = false

        override fun onFailure(call: Call<ClientDetailResponse>, t: Throwable) {
            if (!hasFailed) {
                call.clone()
                hasFailed = true
            }
            t.printStackTrace()
        }

        override fun onResponse(
            call: Call<ClientDetailResponse>,
            response: Response<ClientDetailResponse>
        ) {
            if (response.isSuccessful && response.body()?.code == 200) {
                livedata.value = response.body()?.detail
            } else {
                Toast.makeText(context, "Gagal mengambil detail klien", Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal class IpipSurveyCallbackHandler(
        private val context: Context,
        private val livedata: MutableLiveData<Ipip>
    ) : Callback<IpipResponse> {
        private var hasFailed = false
        override fun onFailure(call: Call<IpipResponse>, t: Throwable) {
            if (!hasFailed) {
                call.clone()
                hasFailed = true
            }
            t.printStackTrace()
        }

        override fun onResponse(call: Call<IpipResponse>, response: Response<IpipResponse>) {
            if (response.isSuccessful && response.body()?.code == 200) {
                livedata.value = response.body()?.result
            } else {
                Toast.makeText(context, "Gagal mengambil data survei IPIP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal class SrqSurveyCallbackHandler(
        private val context: Context,
        private val livedata: MutableLiveData<Srq>
    ) : Callback<SrqResponse> {
        private var hasFailed = false
        override fun onFailure(call: Call<SrqResponse>, t: Throwable) {
            if (!hasFailed) {
                call.clone()
                hasFailed = true
            }
            t.printStackTrace()
        }

        override fun onResponse(call: Call<SrqResponse>, response: Response<SrqResponse>) {
            if (response.isSuccessful && response.body()?.code == 200) {
                livedata.value = response.body()?.result
            } else {
                Toast.makeText(context, "Gagal mengambil data survei IPIP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
