package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.ConsultationService

class ConsultationViewModelFactory(
    private val context: Context,
    private val consultationService: ConsultationService,
    private val db: ApplicationDatabase
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(viewModelClass: Class<T>): T {
        if (viewModelClass.isAssignableFrom(ConsultationViewModel::class.java)) {
            return ConsultationViewModel(context, consultationService, db) as T
        } else {
            return null as T
        }
    }

}
