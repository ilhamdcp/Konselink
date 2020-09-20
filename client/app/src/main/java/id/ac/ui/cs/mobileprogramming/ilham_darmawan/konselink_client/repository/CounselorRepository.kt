package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.CounselorService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CounselorRepository(
    private val context: Context,
    private val counselorService: CounselorService,
    val db: ApplicationDatabase
) {
    private var _counselorListLiveData = MutableLiveData<List<Counselor>>(ArrayList())
    val counselorListLiveData: LiveData<List<Counselor>> = _counselorListLiveData

    private var _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> = _currentPage

    private var _totalPage = MutableLiveData(0)
    val totalPage: LiveData<Int> = _totalPage

    private var _counselorDetailLiveData = MutableLiveData<CounselorDetail>()
    val counselorDetailLiveData: LiveData<CounselorDetail> = _counselorDetailLiveData

    private var _requestScheduleStatusCode = MutableLiveData(0)
    val requestScheduleStatusCode: LiveData<Int> = _requestScheduleStatusCode



    fun getCounselorListFromApi(token: String, pageNo: Int, entrySize: Int, keyword: String?) {
        counselorService.retrieveCounselorList(token, pageNo, entrySize, keyword)
            .enqueue(CounselorListCallbackHandler(_counselorListLiveData, _currentPage, _totalPage))
    }

    fun getCounselorDetail(token: String?, counselorId: Int) {
        counselorService.retrieveCounselorDetail(token, counselorId)
            .enqueue(CounselorDetailCallbackHandler(_counselorDetailLiveData))
    }

    fun requestSchedule(token: String, scheduleId: Int) {
        _requestScheduleStatusCode.value = 0
        counselorService.requestSchedule(token, scheduleId).enqueue(UserRepository.BasicCallbackHandler(context, _requestScheduleStatusCode, "Gagal memesan jadwal"))
    }


    internal class CounselorListCallbackHandler(
        private val counselorListLiveData: MutableLiveData<List<Counselor>>,
        private val currentPage: MutableLiveData<Int>,
        private val totalPage: MutableLiveData<Int>
    ) : Callback<CounselorListResponse> {
        var hasFailed = false
        override fun onFailure(call: Call<CounselorListResponse>, t: Throwable) {
            t.printStackTrace()
            if (!hasFailed) {
                call.clone().enqueue(this)
                hasFailed = true
            }
        }

        override fun onResponse(
            call: Call<CounselorListResponse>,
            response: Response<CounselorListResponse>
        ) {
            if (response.isSuccessful && response.body()!!.code == 200) {
                val counselorListResponse = response.body()!!
                val counselorList = counselorListResponse.counselorList
                if (counselorList != null && counselorList.isNotEmpty()) {
                    counselorListLiveData.value = counselorList
                    currentPage.value = counselorListResponse.currentPage
                    totalPage.value = counselorListResponse.totalPage
                }
            }
        }
    }

    internal class CounselorDetailCallbackHandler (private val counselorDetailLiveData: MutableLiveData<CounselorDetail>): Callback<CounselorDetailResponse> {
        var hasFailed = false
        override fun onFailure(call: Call<CounselorDetailResponse>, t: Throwable) {
            t.printStackTrace()
            if (!hasFailed) {
                call.clone().enqueue(this)
                hasFailed = true
            }
        }

        override fun onResponse(
            call: Call<CounselorDetailResponse>,
            response: Response<CounselorDetailResponse>
        ) {
            if (response.isSuccessful && response.body()!!.code == 200) {
                counselorDetailLiveData.value = response.body()!!.counselorDetail
            }
        }

    }

}