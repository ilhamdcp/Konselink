package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit

import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.BASE_API_URL
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.TokenResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.UpdateProfileRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.UserDataResponse
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import java.util.concurrent.TimeUnit


interface UserService {
    @GET("/profile/basic_info/psikolog")
    fun retrieveUserData(@Header("Authorization") token: String?): Call<UserDataResponse>

    @GET("/login")
    fun loginViaSso(@Header("cookie") cookie: String?): Call<TokenResponse>

    @PUT("/profile/update/psikolog")
    fun updateProfileData(@Header("Authorization") token: String, @Body updateProfileRequest: UpdateProfileRequest): Call<BasicResponse>

    companion object {
        fun create(): UserService {
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

            return retrofit.create(UserService::class.java)
        }
    }
}