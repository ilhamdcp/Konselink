package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.ClientRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository.ClientRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ClientService

class ClientViewModel(
    context: Context,
    clientService: ClientService,
    db: ApplicationDatabase
) : ViewModel() {
    val clientRepository = ClientRepository(clientService, db, context)
    private val _clientRequestListLiveData = clientRepository.clientRequestListLiveData
    val clientRequestListLiveData: LiveData<List<ClientRequest>> = _clientRequestListLiveData

    val totalPageLiveData: LiveData<Int> = clientRepository.totalPage
    val currentPageLiveData: LiveData<Int> = clientRepository.currentPage
    val statusCode: LiveData<Int> = clientRepository.statusCode
    val clientDetailLiveData = clientRepository.clientDetailLiveData
    val ipipLiveData = clientRepository.ipipLiveData
    val srqLiveData = clientRepository.srqLiveData

    fun getClientRequestList(token: String, pageNo: Int, entrySize: Int) {
        clientRepository.getClientRequestList(token, pageNo, entrySize, null)
    }

    fun approveClientRequest(token: String, requestId: Int) {
        clientRepository.approveClientRequest(token, requestId)
    }

    fun rejectClientRequest(token: String, requestId: Int) {
        clientRepository.rejectClientRequest(token, requestId)
    }

    fun resetStatusCode() {
        clientRepository.resetStatusCode()
    }

    fun getClientDetail(token: String, clientId: Int) {
        clientRepository.retrieveClientDetail(token, clientId)
    }

    fun getIpipSurvey(token: String, clientId: Int) {
        clientRepository.retrieveIpipSurvey(token, clientId)
    }

    fun getSrqSurvey(token: String, clientId: Int) {
        clientRepository.retrieveSrqSurvey(token, clientId)
    }
}