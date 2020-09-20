package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService

class RegistrationViewModelFactory (
    private val context: Context,
    private val registrationService: RegistrationService,
    private val db: ApplicationDatabase
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(viewModelClass: Class<T>): T{
        return if (viewModelClass .isAssignableFrom(RegistrationViewModel::class.java)) {
            RegistrationViewModel(context, registrationService, db) as T
        } else {
            null as T
        }

    }
}
