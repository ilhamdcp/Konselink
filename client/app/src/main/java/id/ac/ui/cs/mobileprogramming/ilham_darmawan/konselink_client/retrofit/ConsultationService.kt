package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit

import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.BASE_API_URL
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ConsultationService {
    @GET("/list/schedule/klien/upcoming")
    fun retrieveUpcomingSchedule(@Header("Authorization") token: String, @Query("entrySize") entrySize: Int, @Query("pageNo") pageNo: Int): Call<UpcomingConsultationListResponse>

    @GET("/schedule/klien/ongoing")
    fun retrieveOngoingSchedule(@Header("Authorization") token: String): Call<OngoingConsultationResponse>

    @GET("/chat/history")
    fun retrieveChatHistory(@Header("Authorization") token: String, @Query("scheduleId") scheduleId: Int, @Query("timestamp") timestamp: String?): Call<ChatHistoryListResponse>

    @POST("/chat/submit_survey")
    fun postPreConsultationSurvey(@Header("Authorization") token: String, @Body preConsultationSurveyRequest: PreConsultationSurveyRequest): Call<BasicResponse>

    companion object {
        fun create(): ConsultationService {
            val gson = GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(
                    GsonConverterFactory.create(gson)
                )
                .baseUrl(BASE_API_URL)
                .build()
                .create(ConsultationService::class.java)
        }
    }
}