package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository

import android.os.Build
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.*
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.UserDao
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.UserService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private lateinit var call: Call<UserDataResponse>
    private lateinit var basicCall: Call<BasicResponse>
    private lateinit var tokenCall: Call<TokenResponse>
    private lateinit var userService: UserService
    private lateinit var db: ApplicationDatabase
    private lateinit var mockUserDao: UserDao
    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<UserDataResponse>

        @Suppress("UNCHECKED_CAST")
        basicCall = mock(Call::class.java) as Call<BasicResponse>

        @Suppress("UNCHECKED_CAST")
        tokenCall = mock(Call::class.java) as Call<TokenResponse>
        activity = buildActivity(MainActivity::class.java).create().get()
        userService = mock(UserService::class.java)
        db = Room.inMemoryDatabaseBuilder(buildActivity(MainActivity::class.java).create().get(),
            ApplicationDatabase::class.java).allowMainThreadQueries().build()
        db.clearAllTables()
        userRepository = UserRepository(activity.applicationContext, userService, db)
        mockUserDao = mock(UserDao::class.java)
    }

    @After
    fun tearDown() {
        db.close()
        verifyNoMoreInteractions(call, basicCall, tokenCall, userService)
    }

    @Test
    fun givenValidToken_whenRetrievingUserData_shouldUpdateUserLiveData() {
        val response = UserDataResponse(200, UserData(fullname = "le name", isRegistered = true, isVerified = true))
        `when`(userService.retrieveUserData("token")).thenReturn(call)
        doAnswer {
            val callback: Callback<UserDataResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(response))
        }.`when`(call).enqueue(any(UserRepository.UserDataCallbackHandler::class.java))

        userRepository.retrieveUserData("token")

        verify(call, times(1)).enqueue(any(UserRepository.UserDataCallbackHandler::class.java))
        verify(userService, times(1)).retrieveUserData("token")
        assertThat(userRepository.userLiveData.value?.fullname).isEqualTo("le name")
    }

    @Test
    fun givenValidParameter_whenUpdatingProfileData_shouldUpdateStatusCodeLiveData() {
        val response = BasicResponse(200, "success")
        val updateProfileRequest = UpdateProfileRequest(hasDisplayPicture = true, nickname = "name")
        `when`(userService.updateProfileData("token", updateProfileRequest)).thenReturn(basicCall)
        doAnswer {
            val callback: Callback<BasicResponse> = it.getArgument(0)
            callback.onResponse(basicCall, Response.success(response))
        }.`when`(basicCall).enqueue(any(UserRepository.BasicCallbackHandler::class.java))

        userRepository.updateProfileData("token", updateProfileRequest)

        verify(userService, times(1)).updateProfileData("token", updateProfileRequest)
        verify(basicCall, times(1)).enqueue(any(UserRepository.BasicCallbackHandler::class.java))
        assertThat(userRepository.editProfileStatusCode.value).isEqualTo(200)
    }

    @Test
    fun givenValidCookie_whenLoggingIn_shouldUpdateTokenLiveData() {
        val response = TokenResponse(200, "le token")
        `when`(userService.loginViaSso("cookie")).thenReturn(tokenCall)
        doAnswer {
            val callback: Callback<TokenResponse> = it.getArgument(0)
            callback.onResponse(tokenCall, Response.success(response))
        }.`when`(tokenCall).enqueue(any(UserRepository.TokenCallbackHandler::class.java))
        userRepository.loginViaSso("cookie")

        verify(userService, times(1)).loginViaSso("cookie")
        verify(tokenCall, times(1)).enqueue(any(UserRepository.TokenCallbackHandler::class.java))
        assertThat(userRepository.tokenLiveData.value).isEqualTo("le token")
    }
}