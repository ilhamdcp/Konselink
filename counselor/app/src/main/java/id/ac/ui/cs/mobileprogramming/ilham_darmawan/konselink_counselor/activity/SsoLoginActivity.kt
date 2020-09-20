package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.BuildConfig.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.UserService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.constant.ResponseType
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ProfileViewModel
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel.ProfileViewModelFactory

class SsoLoginActivity : AppCompatActivity() {
    private val profileViewModel by lazy {
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
        setContentView(R.layout.activity_webview_login)
        assignElements()
        observeViewModel()
    }

    fun assignElements() {
        CookieManager.getInstance().removeAllCookies {
            Log.d("COOKIE", "All cookie removed")
        }
        sharedPref = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        val webview = findViewById<WebView>(R.id.webview)
        webview.settings.javaScriptEnabled = true
        webview.webViewClient =
            CustomWebView(
                this,
                sharedPref,
                profileViewModel
            )
        webview.loadUrl(intent.getStringExtra(LOGIN_URL))
    }

    private fun observeViewModel() {
        profileViewModel.tokenLiveData.observe(this) {
            if (it.isNullOrBlank()) {
                setResult(ResponseType.INTERNAL_SERVER_ERROR.code)
                finish()
            } else {
                setResult(ResponseType.SUCCESS.code)
                with(sharedPref.edit()) {
                    putString(TOKEN, it)
                    commit()
                }
                finish()
            }
        }
    }

    private class CustomWebView(
        private val activity: Activity,
        private val sharedPref: SharedPreferences,
        private val profileViewModel: ProfileViewModel
    ) :
        WebViewClient() {
        var authenticated = false


        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val loader = activity.findViewById<ConstraintLayout>(R.id.layout_loading)
            loader.visibility = View.VISIBLE
            if (!sharedPref.getString(
                    "cookie",
                    null
                ).isNullOrEmpty() && authenticated && url.equals(AUTH_API_URL)
            ) {
                retrieveSsoData()
            } else {
                view?.loadUrl(url)
            }
            return false
        }

        private fun retrieveSsoData() {
            profileViewModel.loginViaSso(CookieManager.getInstance().getCookie(AUTH_API_URL))
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            val cookie = CookieManager.getInstance().getCookie(url)
            if (!cookie.isNullOrEmpty() && cookie.contains("SESSIONID") && url?.contains(";jsessionid")!!) {
                with(sharedPref.edit()) {
                    putString("cookie", cookie)
                    commit()
                }
                authenticated = true
            }

            if (sharedPref.getString(
                    "cookie",
                    null
                ).isNullOrEmpty() || !authenticated || !url.equals(AUTH_API_URL)
            ) {
                val loader = activity.findViewById<ConstraintLayout>(R.id.layout_loading)
                loader.visibility = View.GONE
            }
        }
    }

}