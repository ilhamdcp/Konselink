package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ClientService

class ClientViewModelFactory(
    val context: Context,
    val clientService: ClientService,
    val db: ApplicationDatabase
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(viewModelClass: Class<T>): T {
        if (viewModelClass.isAssignableFrom(ClientViewModel::class.java)) {
            return ClientViewModel(context, clientService, db) as T
        } else {
            return null as T
        }
    }

}
