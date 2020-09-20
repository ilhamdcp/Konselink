package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository

import android.os.Build
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.CounselorDetailActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.CounselorService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CounselorRepositoryTest {
    private lateinit var counselorService: CounselorService
    private lateinit var db: ApplicationDatabase
    private lateinit var call: Call<CounselorListResponse>
    private lateinit var counselorDetailCall: Call<CounselorDetailResponse>
    private lateinit var basicCall: Call<BasicResponse>
    private lateinit var counselorRepository: CounselorRepository
    private lateinit var activity: CounselorDetailActivity

    @Before
    fun setUp() {
        activity = buildActivity(CounselorDetailActivity::class.java).create().get()
        counselorService = mock(CounselorService::class.java)
        db = mock(ApplicationDatabase::class.java)
        counselorRepository = CounselorRepository(activity, counselorService, db)

        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<CounselorListResponse>

        @Suppress("UNCHECKED_CAST")
        counselorDetailCall = mock(Call::class.java) as Call<CounselorDetailResponse>

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>
    }

    @Test
    fun givenValidParameter_whenRetrievingCounselorList_shouldUpdateCounselorListLiveData() {
        val firstCounselor = Counselor(fullname = "counselor-1", displayPictureUrl = "url-1")
        val secondCounselor = Counselor(fullname = "counselor-2", displayPictureUrl = "url-2")
        val counselors = listOf(firstCounselor, secondCounselor)
        val counselorListResponse = CounselorListResponse(200, 1, 2, counselors)
        `when`(counselorService.retrieveCounselorList("token",1 , 2, null)).thenReturn(call)

        doAnswer {
            val callback: Callback<CounselorListResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(counselorListResponse))
        }.`when`(call).enqueue(any(CounselorRepository.CounselorListCallbackHandler::class.java))

        counselorRepository.getCounselorListFromApi("token", 1, 2, null)
        val liveData = counselorRepository.counselorListLiveData
        val currentPage = counselorRepository.currentPage
        val totalPage = counselorRepository.totalPage
        assertThat(liveData.value?.size).isEqualTo(2)
        assertThat(liveData.value?.get(0)?.fullname).isEqualTo("counselor-1")
        assertThat(currentPage.value).isEqualTo(1)
        assertThat(totalPage.value).isEqualTo(2)

    }

    @Test
    fun givenValidParameter_whenRetrievingCounselorDetail_shouldReturnCounselorDetailLiveData() {
        val counselorDetail = CounselorDetail(fullname = "Mr. Psikolog", counselorId = 1)
        val counselorDetailResponse = CounselorDetailResponse(200, counselorDetail)

        `when`(counselorService.retrieveCounselorDetail("token", 1)).thenReturn(counselorDetailCall)

       doAnswer {
           val callback: Callback<CounselorDetailResponse> = it.getArgument(0)
           callback.onResponse(counselorDetailCall, Response.success(counselorDetailResponse))
       } .`when`(counselorDetailCall).enqueue(any(CounselorRepository.CounselorDetailCallbackHandler::class.java))

        counselorRepository.getCounselorDetail("token", 1)
        assertThat(counselorRepository.counselorDetailLiveData.value?.counselorId).isEqualTo(1)
        assertThat(counselorRepository.counselorDetailLiveData.value?.fullname).isEqualTo("Mr. Psikolog")
    }

    @Test
    fun givenValidParameter_whenRequestingSchedule_shouldUpdateStatusCodeLiveData() {
        val response = BasicResponse(200, "success")
        `when`(counselorService.requestSchedule(anyString(), anyInt())).thenReturn(basicCall)

        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        } .`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))

        counselorRepository.requestSchedule("token", 1)

        verify(counselorService, times(1)).requestSchedule(anyString(), anyInt())
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        assertThat(counselorRepository.requestScheduleStatusCode.value).isEqualTo(200)
    }
}