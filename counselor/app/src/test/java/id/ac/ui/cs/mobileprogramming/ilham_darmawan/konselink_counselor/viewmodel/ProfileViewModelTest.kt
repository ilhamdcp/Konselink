package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.google.common.truth.Truth
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.TokenResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.UserData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.UserDataResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.repository.UserRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.UserService
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verifyNoMoreInteractions
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ProfileViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var userService: UserService
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var activity: MainActivity
    private lateinit var call: Call<UserDataResponse>
    private lateinit var tokenCall: Call<TokenResponse>
    private lateinit var db: ApplicationDatabase

    @Before
    fun setUp() {
        userService = Mockito.mock(UserService::class.java)
        activity = buildActivity(MainActivity::class.java).create().get()
        db = Room
            .inMemoryDatabaseBuilder(activity, ApplicationDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        db.clearAllTables()

        @Suppress("UNCHECKED_CAST")
        call = Mockito.mock(Call::class.java) as Call<UserDataResponse>

        @Suppress("UNCHECKED_CAST")
        tokenCall = Mockito.mock(Call::class.java) as Call<TokenResponse>

        profileViewModel = ProfileViewModelFactory(
            activity.applicationContext,
            userService,
            db
        ).create(ProfileViewModel::class.java)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(userService, call, tokenCall)
    }

    @Test
    fun givenValidParameter_whenRetrievingUserData_shouldUpdateLiveData() {
        val userDataResponse =
            UserDataResponse(200, UserData(username = "username", faculty = "Computer Science"))
        Mockito.`when`(userService.retrieveUserData("token")).thenReturn(call)
        Mockito.`when`(call.enqueue(Mockito.any(UserRepository.UserDataCallbackHandler::class.java)))
            .thenAnswer {
                val callback: Callback<UserDataResponse> = it.getArgument(0)
                callback.onResponse(call, Response.success(userDataResponse))
            }
        profileViewModel.getUserData("token")
        val liveData = profileViewModel.userDataLiveData

        Mockito.verify(userService).retrieveUserData("token")
        Mockito.verify(call)
            .enqueue(Mockito.any(UserRepository.UserDataCallbackHandler::class.java))
        Truth.assertThat(liveData.value?.username).isEqualTo("username")
    }


    @Test
    fun givenValidCookie_whenLoggingIn_shouldUpdateTokenLiveData() {
        val response = TokenResponse(200, "le token")
        Mockito.`when`(userService.loginViaSso("cookie")).thenReturn(tokenCall)
        Mockito.doAnswer {
            val callback: Callback<TokenResponse> = it.getArgument(0)
            callback.onResponse(tokenCall, Response.success(response))
        }.`when`(tokenCall).enqueue(Mockito.any(UserRepository.TokenCallbackHandler::class.java))
        profileViewModel.loginViaSso("cookie")

        Mockito.verify(userService, Mockito.times(1)).loginViaSso("cookie")
        Mockito.verify(tokenCall, Mockito.times(1))
            .enqueue(Mockito.any(UserRepository.TokenCallbackHandler::class.java))
        Truth.assertThat(profileViewModel.tokenLiveData.value).isEqualTo("le token")
    }
}