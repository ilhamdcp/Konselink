package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository.CounselorRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository.UserRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.CounselorService
import org.junit.After
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
class CounselorViewModelTest {
    private lateinit var activity: MainActivity
    private lateinit var counselorViewModel: CounselorViewModel
    private lateinit var counselorService: CounselorService
    private lateinit var call: Call<CounselorListResponse>
    private lateinit var basicCall: Call<BasicResponse>
    private lateinit var counselorDetailCall: Call<CounselorDetailResponse>

    @Before
    fun setUp() {
        activity = buildActivity(MainActivity::class.java).create().get()
        counselorService = mock(CounselorService::class.java)

        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<CounselorListResponse>
        counselorViewModel = ViewModelProvider(
            activity,
            CounselorViewModelFactory(
                activity,
                counselorService,
                Room.inMemoryDatabaseBuilder(
                    activity,
                    ApplicationDatabase::class.java
                ).allowMainThreadQueries().build()
            )
        ).get(CounselorViewModel::class.java)

        @Suppress("UNCHECKED_CAST")
        counselorDetailCall = mock(Call::class.java) as Call<CounselorDetailResponse>

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(call, counselorDetailCall, basicCall)
    }

    @Test
    fun givenValidParameter_whenRetrievingCounselorList_shouldUpdateLiveData() {
        val counselor = Counselor(fullname = "counselor-1", specialization = "Psikologi Pendidikan")
        val response = CounselorListResponse(200, 1, 2, listOf(counselor))
        `when`(counselorService.retrieveCounselorList("token", 1, 2, null)).thenReturn(call)

        doAnswer {
            val callback: Callback<CounselorListResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(response))
        }.`when`(call).enqueue(any(CounselorRepository.CounselorListCallbackHandler::class.java))

        counselorViewModel.getCounselorList("token", 1, 2)

        val updatedLiveData = counselorViewModel.counselorListLiveData.value?.get(0)
        val currentPage = counselorViewModel.currentPageLiveData
        val totalPage = counselorViewModel.totalPageLiveData

        verify(counselorService, times(1)).retrieveCounselorList("token", 1, 2, null)
        verify(call, times(1)).enqueue(any(CounselorRepository.CounselorListCallbackHandler::class.java))
        assertThat(updatedLiveData?.fullname).isEqualTo(counselor.fullname)
        assertThat(currentPage.value).isEqualTo(1)
        assertThat(totalPage.value).isEqualTo(2)
    }

    @Test
    fun givenNonEmptyKeyword_whileHandlingTextChange_shouldUpdateLiveData() {
        counselorViewModel.handleSearchCounselorEditText("keyword")

        assertThat(counselorViewModel.keywordLiveData.value).isEqualTo("keyword")
    }

    @Test
    fun givenEmptyKeyword_whileHandlingTextChange_shouldUpdateLiveDataToNull() {
        counselorViewModel.handleSearchCounselorEditText("")

        assertThat(counselorViewModel.keywordLiveData.value).isNull()
    }

    @Test
    fun givenValidParameter_whenRetrievingCounselorDetail_shouldUpdateLiveData() {
        val counselorDetail = CounselorDetail(fullname = "Mr. Psikolog", counselorId = 1)
        val counselorDetailResponse = CounselorDetailResponse(200, counselorDetail)

        `when`(counselorService.retrieveCounselorDetail("token", 1)).thenReturn(counselorDetailCall)

        doAnswer {
            val callback: Callback<CounselorDetailResponse> = it.getArgument(0)
            callback.onResponse(counselorDetailCall, Response.success(counselorDetailResponse))
        } .`when`(counselorDetailCall).enqueue(any(CounselorRepository.CounselorDetailCallbackHandler::class.java))

        counselorViewModel.getCounselorDetail("token", 1)

        verify(counselorService, times(1)).retrieveCounselorDetail("token", 1)
        verify(counselorDetailCall, times(1)).enqueue(any(CounselorRepository.CounselorDetailCallbackHandler::class.java))
        assertThat(counselorViewModel.counselorDetailLiveData.value?.counselorId).isEqualTo(1)
        assertThat(counselorViewModel.counselorDetailLiveData.value?.fullname).isEqualTo("Mr. Psikolog")
    }

    @Test
    fun givenValidParameter_whenRequestingSchedule_shouldUpdateLiveData() {
        val response = BasicResponse(200, "success")
        `when`(counselorService.requestSchedule(anyString(), anyInt())).thenReturn(basicCall)

        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        } .`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))

        counselorViewModel.requestSchedule("token", 1)

        verify(counselorService, times(1)).requestSchedule(anyString(), anyInt())
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        assertThat(counselorViewModel.requestScheduleStatusCode.value).isEqualTo(200)
    }
}