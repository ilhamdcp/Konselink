package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.CounselorService

class CounselorViewModelFactory(
    val context: Context,
    val counselorService: CounselorService,
    val db: ApplicationDatabase
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(viewModelClass: Class<T>): T {
        if (viewModelClass.isAssignableFrom(CounselorViewModel::class.java)) {
            return CounselorViewModel(context, counselorService, db) as T
        } else {
            return null as T
        }
    }

}
