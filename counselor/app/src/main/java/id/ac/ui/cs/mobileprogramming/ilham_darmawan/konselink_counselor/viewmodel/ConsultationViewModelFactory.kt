package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService

class ConsultationViewModelFactory(
    private val context: Context,
    private val consultationService: ConsultationService,
    private val db: ApplicationDatabase
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(viewModelClass: Class<T>): T {
        return if (viewModelClass.isAssignableFrom(ConsultationViewModel::class.java)) {
            ConsultationViewModel(context, consultationService, db) as T
        } else {
            null as T
        }
    }
}
