package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.BasicResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.Registration
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.RegistrationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationRepository(
    val retrofitService: RegistrationService,
    val db: ApplicationDatabase
) {
    val registrationDao = db.registrationDao()
    val data = registrationDao.getCurrentRegistration()
    val _registrationResult = MutableLiveData<Int>(0)
    private val registrationResult: LiveData<Int> = _registrationResult

    fun getRegistrationData(): LiveData<Registration> {
        return data
    }

    fun getRegistrationResult(): LiveData<Int> {
        return registrationResult
    }

    fun insertRegistration(registration: MutableLiveData<Registration>) {
        val registrationData = registration.value
        if (registrationData != null) {
            Thread {
                val variables = registrationDao.insert(registrationData)
            }.start()
        }
    }

    fun postRegistrationData(registrationData: Registration, token: String?) {
        retrofitService.registerUser(registrationData, token)
            .enqueue(PostRegistrationCallbackHandler(_registrationResult))
    }

    internal class PostRegistrationCallbackHandler(val postResponse: MutableLiveData<Int>) :
        Callback<BasicResponse> {
        var hasRetry = false

        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            if (!hasRetry) {
                call.clone().enqueue(this)
                hasRetry = true
            }

            t.printStackTrace()
        }

        override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
            Log.d("REG RESPONSE", "response detected")
            val postRegistrationResponse = response.body()!!.code
            Log.d("RESPONSE CODE", postRegistrationResponse.toString())
            postResponse.value = postRegistrationResponse
        }
    }
}