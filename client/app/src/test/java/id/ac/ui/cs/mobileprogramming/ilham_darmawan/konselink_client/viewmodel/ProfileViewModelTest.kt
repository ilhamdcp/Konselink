package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.os.Build
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.constant.RegistrationError
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.TokenResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.UserData
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.retrofit.UserDataResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.repository.UserRepository
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.UserService
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
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
        userService = mock(UserService::class.java)
        activity = buildActivity(MainActivity::class.java).create().get()
        db = Room
            .inMemoryDatabaseBuilder(activity, ApplicationDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        db.clearAllTables()

        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<UserDataResponse>

        @Suppress("UNCHECKED_CAST")
        tokenCall = mock(Call::class.java) as Call<TokenResponse>
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
        `when`(userService.retrieveUserData("token")).thenReturn(call)
        `when`(call.enqueue(any(UserRepository.UserDataCallbackHandler::class.java))).thenAnswer {
            val callback: Callback<UserDataResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(userDataResponse))
        }
        profileViewModel.getUserData("token")
        val liveData = profileViewModel.userDataLiveData

        verify(userService).retrieveUserData("token")
        verify(call).enqueue(any(UserRepository.UserDataCallbackHandler::class.java))
        assertThat(liveData.value?.username).isEqualTo("username")
    }

    @Test
    fun givenValidName_shouldUpdateViewModelAttribute() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val name = "Name"
        profileViewModel.handleNicknameEditText(name)
        assertTrue(profileViewModel.errNickname.get()?.isEmpty()!!)
    }

    @Test
    fun givenNameContainingSpecialCharOrNumber_shouldUpdateViewModelErrAttribute() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val name = "999,,"
        profileViewModel.handleNicknameEditText(name)
        assertEquals(
            RegistrationError.SHOULD_NOT_CONTAIN_NUMBER_AND_SPECIAL_CHAR.errName,
            profileViewModel.errNickname.get().toString()
        )
    }

    @Test
    fun givenBlankName_shouldUpdateViewModelErrAttribute() {
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        val name = "    "
        profileViewModel.handleNicknameEditText(name)
        assertEquals(
            RegistrationError.SHOULD_NOT_EMPTY.errName,
            profileViewModel.errNickname.get().toString()
        )
    }

    @Test
    fun givenValidCondition_whenValidateProfile_shouldReturnTrue() {
        profileViewModel.updatedUserLiveData.value = UserData(nickname = "nickname")
        assertThat(profileViewModel.profileIsValid()).isTrue()
    }

    @Test
    fun givenInvalidCondition_whenValidateProfile_shouldReturnTrue() {
        profileViewModel.updatedUserLiveData.value = UserData(nickname = "")
        assertThat(profileViewModel.profileIsValid()).isFalse()
    }

    @Test
    fun givenValidCookie_whenLoggingIn_shouldUpdateTokenLiveData() {
        val response = TokenResponse(200, "le token")
        `when`(userService.loginViaSso("cookie")).thenReturn(tokenCall)
        doAnswer {
            val callback: Callback<TokenResponse> = it.getArgument(0)
            callback.onResponse(tokenCall, Response.success(response))
        }.`when`(tokenCall).enqueue(any(UserRepository.TokenCallbackHandler::class.java))
        profileViewModel.loginViaSso("cookie")

        verify(userService, times(1)).loginViaSso("cookie")
        verify(tokenCall, times(1)).enqueue(any(UserRepository.TokenCallbackHandler::class.java))
        assertThat(profileViewModel.tokenLiveData.value).isEqualTo("le token")
    }
}