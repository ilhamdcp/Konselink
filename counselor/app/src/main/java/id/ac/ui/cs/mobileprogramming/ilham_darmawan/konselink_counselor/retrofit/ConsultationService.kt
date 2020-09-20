package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit

import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.ChatHistoryListResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ConsultationService {
    @GET("/schedule/psikolog/list")
    fun retrieveConsultationSchedule(@Header("Authorization") token: String?,
                                     @Query("month") month: Int,
                                     @Query("year") year: Int): Call<ConsultationScheduleListResponse>

    @POST("/schedule/psikolog/create")
    fun postNewScheduleData(@Header("Authorization") token: String?,
                            @Body newSchedule: NewScheduleRequest?): Call<BasicResponse>

    @PUT("/schedule/psikolog/delete/{scheduleId}")
    fun deleteScheduleData(@Header("Authorization") token: String, @Path("scheduleId") scheduleId: Int): Call<BasicResponse>

    @GET("/list/schedule/psikolog/upcoming")
    fun retrieveUpcomingSchedule(@Header("Authorization") token: String,
                                 @Query("entrySize") entrySize: Int,
                                 @Query("pageNo") pageNo: Int): Call<UpcomingConsultationListResponse>

    @GET("/schedule/psikolog/ongoing")
    fun retrieveOngoingSchedule(@Header("Authorization") token: String): Call<OngoingConsultationResponse>

    @GET("/chat/history")
    fun retrieveChatHistory(@Header("Authorization") token: String,
                            @Query("scheduleId") scheduleId: Int,
                            @Query("timestamp") timestamp: String?
    ): Call<ChatHistoryListResponse>

    @POST("/chat/submit_record")
    fun postClientRecord(@Header("Authorization") token: String,
                         @Body clientRecord: ClientRecord): Call<BasicResponse>

    @GET("/list/icd_codes")
    fun retrieveIcdDiagnosis(@Header("Authorization") token: String,
                             @Query("entrySize") entrySize: Int,
                             @Query("pageNo") pageNo: Int,
                             @Query("keyword") keyword: String): Call<DiagnosisCodeListResponse>

    @GET("/chat/get_survey/{scheduleId}")
    fun retrievePreConsultationSurvey(@Header("Authorization") token: String, @Path("scheduleId") scheduleId: Int): Call<PreConsultationSurveyListResponse>

    @GET("/chat/get_record/{klienId}")
    fun retrieveClientRecord(@Header("Authorization") token: String, @Path("klienId") clientId: Int): Call<ClientRecordListResponse>

    companion object {
        fun create(): ConsultationService {
            val gson = GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create(gson))
                .baseUrl(BuildConfig.BASE_API_URL)
                .build()

            return retrofit.create(ConsultationService::class.java)
        }
    }
}