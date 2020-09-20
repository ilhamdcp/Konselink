package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.registration.RegistrationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.UserService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ProfileViewModelFactory
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ProfileViewModel


class SplashActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPref = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        profileViewModel = ProfileViewModelFactory(
            application, UserService.create(), ApplicationDatabase.getInstance(
                this
            )!!
        ).create(ProfileViewModel::class.java)

        if (!sharedPref.getString(TOKEN, null).isNullOrEmpty()) {
            profileViewModel.getUserData(sharedPref.getString(TOKEN, "")!!)
            observeViewModel()
        } else {
            Handler().postDelayed({
                val i = Intent(this, LoginActivity::class.java)

                startActivity(i)
                this.finish()
            }, 2000)
        }

    }

    private fun observeViewModel() {
        profileViewModel.userDataLiveData.observe(this)
        {
            Handler().postDelayed({
                Intent(this, LoginActivity::class.java)

                val i = if (it.isVerified!! && it.isRegistered!!) {
                    Intent(this, MainActivity::class.java)
                } else if (!it.isVerified!! && it.isRegistered!!) {
                    Intent(this, VerificationActivity::class.java)
                }  else {
                    Intent(this, RegistrationActivity::class.java)
                }

                with(sharedPref.edit()) {
                    putInt(USER_ID, it.userId!!)
                    putString(USER_ROLE, it.role)
                    commit()
                }
                startActivity(i)
                this.finish()
            }, 2000)
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    external fun stringFromJNI(): String

//    companion object {
//
//        // Used to load the 'native-lib' library on application startup.
//        init {
//            System.loadLibrary("native-lib")
//        }
//    }
}
