package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.IpipAndSrqRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Registration
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService

class RegistrationRepository(
    val context: Context,
    val registrationService: RegistrationService,
    val db: ApplicationDatabase
) {
    private val _postRegistrationCode = MutableLiveData<Int>(0)
    val postRegistrationCode: LiveData<Int> = _postRegistrationCode

    private val _postIpipAndSrqStatusCode = MutableLiveData(0)
    val postIpipAndSrqStatusCode: LiveData<Int> = _postIpipAndSrqStatusCode
    val registrationDao = db.registrationDao()
    val data = registrationDao.getCurrentRegistration()

    fun getRegistrationData(): LiveData<Registration> {
        return data
    }

    fun insertRegistration(registration: MutableLiveData<Registration>) {
        val registrationData = registration.value
        if (registrationData != null) {
            Thread {
                val variables = registrationDao.insert(registrationData)
            }.start()
        }
    }

    fun postRegistrationData(registrationData: Registration, token: String) {
        registrationService.registerUser(registrationData, token)
            .enqueue(UserRepository.BasicCallbackHandler(context, _postRegistrationCode, "Gagal mengupload data registrasi"))
    }

    fun postIpipAndSrqData(token: String, requestBody: IpipAndSrqRequest) {
        registrationService.postIpipAndSrqSurvey(token, requestBody)
            .enqueue(UserRepository.BasicCallbackHandler(context, _postIpipAndSrqStatusCode, "Gagal mengupload data survey IPIP dan SRQ"))
    }
}