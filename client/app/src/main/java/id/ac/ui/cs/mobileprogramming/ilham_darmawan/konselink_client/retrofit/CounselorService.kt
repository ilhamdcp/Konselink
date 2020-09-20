package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit

import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.BASE_API_URL
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.CounselorDetailResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.CounselorListResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.RequestScheduleRequest
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface CounselorService {
    @GET("/list/psikolog")
    fun retrieveCounselorList(
        @Header("Authorization") token: String,
        @Query("pageNo") pageNo: Int,
        @Query("entrySize") entrySize: Int,
        @Query("keyword") keyword: String?
    ): Call<CounselorListResponse>

    @GET("/detail/psikolog/{counselorId}")
    fun retrieveCounselorDetail(@Header("Authorization") token: String?, @Path("counselorId") counselorId: Int): Call<CounselorDetailResponse>

    @PUT("/schedule/klien/request/{scheduleId}")
    fun requestSchedule(
        @Header("Authorization") token: String, @Path("scheduleId") scheduleId: Int): Call<BasicResponse>

    companion object {
        fun create(): CounselorService {
            val gson = GsonBuilder().setLenient().create()
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .client(okHttpClient)
                .addConverterFactory(
                    GsonConverterFactory.create(gson)
                )
                .baseUrl(BASE_API_URL)
                .build()
            return retrofit.create(CounselorService::class.java)
        }
    }
}
