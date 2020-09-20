package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.TransitionInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration.RegistrationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.RequestType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.ResponseType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.UserService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ProfileViewModelFactory
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel.ProfileViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private val profileViewModel by lazy {
        return@lazy ProfileViewModelFactory(
            application, UserService.create(), ApplicationDatabase.getInstance(
                this
            )!!
        ).create(ProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.fade_in)
        window.enterTransition = enterTransition
        sharedPref = this.getSharedPreferences(BuildConfig.SHARED_PREF, Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)
        assignElements()
    }

    fun assignElements() {
        val loginButton = findViewById<ImageView>(R.id.button_sso_login)
        loginButton.setOnClickListener {
            val i = Intent(this, SsoLoginActivity::class.java)
            i.putExtra(LOGIN_URL, AUTH_API_URL)
            startActivityForResult(i, RequestType.AUTH_SSO_LOGIN.requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestType.AUTH_SSO_LOGIN.requestCode) {
            if (resultCode == ResponseType.SUCCESS.code) {
                profileViewModel.userDataLiveData.observe(this) {
                    if (it != null) {
                        val i = if (it.isVerified!! && it.isRegistered!!) {
                            Intent(this, MainActivity::class.java)
                        } else if (!it.isVerified!! && it.isRegistered!!) {
                            Intent(this, VerificationActivity::class.java)
                        } else {
                            Intent(this, RegistrationActivity::class.java)
                        }

                        with(sharedPref.edit()) {
                            putInt(USER_ID, it.userId!!)
                            putString(USER_ROLE, it.role)
                            commit()
                        }
                        startActivity(i)
                        this.finish()
                    }
                }

                profileViewModel.getUserData(sharedPref.getString(TOKEN, "")!!)
            }
        }
    }
}