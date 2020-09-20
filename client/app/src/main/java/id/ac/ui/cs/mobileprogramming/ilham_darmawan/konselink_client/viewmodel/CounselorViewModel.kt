package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.Counselor
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.CounselorDetail
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository.CounselorRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.CounselorService

class CounselorViewModel(
    context: Context,
    counselorService: CounselorService,
    db: ApplicationDatabase
) : ViewModel() {
    val counselorRepository = CounselorRepository(context, counselorService, db)
    val counselorListLiveData: LiveData<List<Counselor>> = counselorRepository.counselorListLiveData
    private val _keywordLiveData = MutableLiveData<String>()
    val keywordLiveData: LiveData<String> = _keywordLiveData
    val currentPageLiveData = counselorRepository.currentPage
    val totalPageLiveData = counselorRepository.totalPage
    val counselorDetailLiveData: LiveData<CounselorDetail> = counselorRepository.counselorDetailLiveData
    val requestScheduleStatusCode = counselorRepository.requestScheduleStatusCode
    val selectedButton = MutableLiveData<String>()
    val bookedSchedules = MutableLiveData<ArrayList<Int>>(ArrayList())

    fun getCounselorList(token: String, pageNo: Int, entrySize: Int) {
        counselorRepository.getCounselorListFromApi(token, pageNo, entrySize, keywordLiveData.value)
    }

    fun handleSearchCounselorEditText(keyword: String?) {
        if (keyword.isNullOrBlank() || keyword == "null") {
            _keywordLiveData.value = null
        } else {
            _keywordLiveData.value = keyword
        }
    }

    fun getCounselorDetail(token: String?, counselorId: Int) {
        counselorRepository.getCounselorDetail(token, counselorId)
    }

    fun requestSchedule(token: String, scheduleId: Int) {
        counselorRepository.requestSchedule(token, scheduleId)
    }
}
