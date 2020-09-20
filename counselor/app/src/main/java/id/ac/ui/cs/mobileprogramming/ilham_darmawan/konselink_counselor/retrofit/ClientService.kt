package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit

import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.BASE_API_URL
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ClientService {
    @GET("/list/klien")
    fun retrieveClientRequestList(
        @Header("Authorization") token: String,
        @Query("pageNo") pageNo: Int,
        @Query("entrySize") entrySize: Int,
        @Query("keyword") keyword: String?
    ): Call<ClientRequestListResponse>

    @PUT("/schedule/psikolog/approve/{clientId}")
    fun approveClientRequest(@Header("Authorization") token: String, @Path("clientId") clientId: Int): Call<BasicResponse>

    @PUT("/schedule/psikolog/reject/{requestId}")
    fun rejectClientRequest(@Header("Authorization") token: String, @Path("requestId") requestId: Int): Call<BasicResponse>

    @GET("/register/get_klien_data/{klienId}")
    fun retrieveClientDetail(@Header("Authorization") token: String, @Path("klienId") clientId: Int): Call<ClientDetailResponse>

    @GET("/register/get_survey")
    fun retrieveIpipSurvey(
        @Header("Authorization") token: String,
        @Query("clientId") clientId: Int,
        @Query("questionType") questionType: String = "IPIP"
    ): Call<IpipResponse>

    @GET("/register/get_survey")
    fun retrieveSrqSurvey(
        @Header("Authorization") token: String,
        @Query("clientId") clientId: Int,
        @Query("questionType") questionType: String = "SRQ"
    ): Call<SrqResponse>


    companion object {
        fun create(): ClientService {
            val gson = GsonBuilder().setLenient().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                ).client(okHttpClient)
                .addConverterFactory(
                    GsonConverterFactory.create(gson)
                )
                .baseUrl(BASE_API_URL)
                .build()
            return retrofit.create(ClientService::class.java)
        }
    }

}