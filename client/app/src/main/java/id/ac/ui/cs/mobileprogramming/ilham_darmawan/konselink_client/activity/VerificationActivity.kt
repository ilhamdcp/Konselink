package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration.RegistrationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.UserService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ProfileViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ProfileViewModelFactory

class VerificationActivity : AppCompatActivity() {
    val profileViewModel by lazy {
        ViewModelProvider(
            this, ProfileViewModelFactory(
                this,
                UserService.create(),
                ApplicationDatabase.getInstance(this)!!
            )
        ).get(ProfileViewModel::class.java)
    }

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        assignElements()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        initializeViewModel()
    }

    private fun assignElements() {
        sharedPref = getSharedPreferences(BuildConfig.SHARED_PREF, Context.MODE_PRIVATE)
    }

    private fun observeViewModel() {
        profileViewModel.userDataLiveData.observe(this) {
            with(sharedPref.edit()) {
                putInt(BuildConfig.USER_ID, it.userId!!)
                putString(BuildConfig.USER_ROLE, it.role)
                commit()
            }

            if (it.isVerified!! && it.isRegistered!!) {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                this.finish()
            }
        }
    }

    private fun initializeViewModel() {
        profileViewModel.getUserData(sharedPref.getString(BuildConfig.TOKEN, "")!!)
    }
}