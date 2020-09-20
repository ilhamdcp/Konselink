package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.TokenResponse
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.UpdateProfileRequest
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.retrofit.UserDataResponse
import io.reactivex.Observable
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class UserServiceTest {

    private lateinit var userService: UserService
    private lateinit var mockUserService: UserService
    private lateinit var call: Call<UserDataResponse>
    private lateinit var observableToken: Call<TokenResponse>
    private lateinit var mockResponse: MockResponse
    private lateinit var gson: Gson
    private val userProfileResponse = "{'code': 200, user: {'nickName': 'le name', 'username': 'user.name', 'faculty': 'Fakultas Ilmu Komputer', 'npm': '9999', 'study_program': 'Ilmu Komputer', 'role': 'mahasiswa', 'is_verified': true, 'is_registered': true}}"
    private val postSuccessResponse = "{'code': 200, 'message': 'Request Success'}"
    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        gson = GsonBuilder().setLenient().create()
        mockUserService = mock(UserService::class.java)

        @Suppress("UNCHECKED_CAST")
        call = mock(Call::class.java) as Call<UserDataResponse>

        @Suppress("UNCHECKED_CAST")
        observableToken = mock(Call::class.java) as Call<TokenResponse>
        userService = Retrofit.Builder()
            .addCallAdapterFactory(
                RxJava2CallAdapterFactory.create()
            )
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(UserService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }


    @Test
    fun whenCreatingUserClass_shouldReturnCorrectInstance() {
        val userService = UserService.create()
        assertTrue(userService is UserService)
    }

    @Test
    fun givenValidRequestParameter_whenRetrievingUserData_shouldReturnMockedCall() {
        `when`(mockUserService.retrieveUserData("token")).thenReturn(call)
        val response = mockUserService.retrieveUserData("token")
        verify(mockUserService, times(1)).retrieveUserData("token")
        assertThat(response).isEqualTo(call)
    }

    @Test
    fun givenValidRequestParameter_whenLoginWithCookie_shouldReturnMockedObservable() {
        `when`(mockUserService.loginViaSso("cookie")).thenReturn(observableToken)
        val response = mockUserService.loginViaSso("cookie")
        verify(mockUserService, times(1)).loginViaSso("cookie")
        assertThat(response).isEqualTo(observableToken)
    }


    @Test
    fun givenValidParameter_whenRetrievingUserData_shouldReturnCorrectResponse() {
        mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(userProfileResponse)
            .setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val response = userService.retrieveUserData("token").execute()
        mockWebServer.takeRequest()

        assertThat(response.body()?.userData?.nickname).isEqualTo("le name")
    }

    @Test
    fun givenNonNullUpdateProfileRequest_whenUpdateProfile_shouldReturnSuccessResponse() {
        mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(postSuccessResponse)
            .setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)

        val updateProfileRequest = UpdateProfileRequest(hasDisplayPicture = true, nickname = "nickname")

        val response = userService.updateProfileData("token", updateProfileRequest).execute()
        mockWebServer.takeRequest()

        assertThat(response.body()?.code).isEqualTo(200)
    }
}