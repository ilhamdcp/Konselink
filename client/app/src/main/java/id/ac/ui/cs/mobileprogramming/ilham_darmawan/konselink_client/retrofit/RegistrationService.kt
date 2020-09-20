package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit

import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.BASE_API_URL
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.IpipAndSrqRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Registration
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface RegistrationService {
    @POST("/register/klien")
    fun registerUser(@Body data: Registration, @Header("Authorization") token: String?): Call<BasicResponse>

    @POST("/register/submit_survey")
    fun postIpipAndSrqSurvey(@Header("Authorization") token: String, @Body data: IpipAndSrqRequest): Call<BasicResponse>

    companion object {
        fun create(): RegistrationService {
            val gson = GsonBuilder().setLenient().create()
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
                .baseUrl(BASE_API_URL)
                .build()
            return retrofit.create(RegistrationService::class.java)
        }
    }
}
