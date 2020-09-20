package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.connectedToInternet
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.User
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.UserDao
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository (private val context: Context, private val userService: UserService, db: ApplicationDatabase) {
    private val userDao = db.userDao()

    private val _userLiveData = MutableLiveData<UserData>()
    val userLiveData: LiveData<UserData> = _userLiveData

    private val _editProfileStatusCode = MutableLiveData<Int>()
    val editProfileStatusCode: LiveData<Int> = _editProfileStatusCode

    private val _tokenLiveData = MutableLiveData<String>()
    val tokenLiveData: LiveData<String> = _tokenLiveData

    fun retrieveUserData(token: String?) {
        if (connectedToInternet(context)) {
            userService.retrieveUserData(token)
                .enqueue(UserDataCallbackHandler(context, userDao, _userLiveData))
        } else {
            Transformations.map(userDao.getLatestUser()) {
                val mappedUser = UserData(userId = it.userId, fullname = it.fullname, nickname = it.nickname, academicId = it.academicId,
                    username = it.username, role = it.role, isVerified = it.isVerified, isRegistered = it.isRegistered)
                _userLiveData.value = mappedUser
            }
        }
    }

    fun updateProfileData(token: String, updateProfileRequest: UpdateProfileRequest) {
        userService.updateProfileData(token, updateProfileRequest).enqueue(BasicCallbackHandler(context, _editProfileStatusCode, "Gagal mengedit profil"))
    }

    fun loginViaSso(cookie: String) {
        userService.loginViaSso(cookie).enqueue(TokenCallbackHandler(context, _tokenLiveData, "Gagal melakukan login"))
    }

    internal class TokenCallbackHandler(
        private val context: Context,
        private val liveData: MutableLiveData<String>,
        private val errorMessage: String
    ) : Callback<TokenResponse> {
        private var hasFailed = false
        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
            if (!hasFailed) {
                call.clone().enqueue(this)
                hasFailed = true
            }

            t.printStackTrace()        }

        override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
            if (response.isSuccessful && response.body()?.code == 200) {
                liveData.value = response.body()?.token
            } else {
                liveData.value = null
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal class UserDataCallbackHandler(private val context: Context, private val userDao: UserDao, private val userData: MutableLiveData<UserData>): Callback<UserDataResponse> {
        private var hasRetry = false

        override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
            if (!hasRetry) {
                call.clone().enqueue(this)
                hasRetry = true
            }

            t.printStackTrace()
        }

        override fun onResponse(call: Call<UserDataResponse>, response: Response<UserDataResponse>) {
            if (response.isSuccessful && response.body()!!.code == 200) {
                val userResponse = response.body()!!.userData!!
                val user = User()
                user.userId = userResponse.userId
                user.fullname = userResponse.fullname
                user.nickname = userResponse.nickname
                user.academicId = userResponse.academicId
                user.username = userResponse.username
                user.role = userResponse.role
                user.isVerified = userResponse.isVerified
                user.isRegistered = userResponse.isRegistered

                Thread {
                    userDao.insert(user)
                }.start()

                userData.value = userResponse
            } else {
                Toast.makeText(context, "Error pada server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    internal class BasicCallbackHandler(private val context: Context, private val statusCode: MutableLiveData<Int>, private val errorText: String ): Callback<BasicResponse> {
        private var hasFailed = false

        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
            if (!hasFailed) {
                hasFailed = true
                call.clone()
            }
            statusCode.value = 500
            t.printStackTrace()
        }

        override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
            Log.d("CounselorRepo", "statusCode: ${response.code()} and ${response.body()?.code}")
            if (response.isSuccessful) {
                if (response.body()?.code != 200) {
                    Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
                }

                statusCode.value = response.body()?.code
            } else {
                Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
            }
        }
    }
}