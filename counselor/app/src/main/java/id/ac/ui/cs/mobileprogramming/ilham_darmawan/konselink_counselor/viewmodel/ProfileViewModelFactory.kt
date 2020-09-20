package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.UserService


class ProfileViewModelFactory(
    private val mApplication: Context,
    private val retrofitService: UserService,
    private val applicationDatabase: ApplicationDatabase

) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            mApplication,
            retrofitService,
            applicationDatabase
        ) as T
    }
}
