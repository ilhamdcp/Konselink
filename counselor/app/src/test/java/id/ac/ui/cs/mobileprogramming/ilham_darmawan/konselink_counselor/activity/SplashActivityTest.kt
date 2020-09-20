package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity

import android.content.Intent
import android.os.Build
import com.google.gson.GsonBuilder
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.UserService
import junit.framework.Assert.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
class SplashActivityTest {
    private val jsonBody = "{\"code\": \"200\", " +
            "\"data\": [" +
            "{\"username\": \"username\", " +
            "\"name\": \"name\", " +
            "\"faculty\": \"faculty\"," +
            "\"academicId\": \"academicId\"," +
            "\"studyProgram\": \"studyProgram\"," +
            "\"educationalProgram\": \"educationalProgram\"," +
            "\"role\": \"role \"}" +
            "]}"

    private val mockWebServer = MockWebServer()
    private lateinit var activity: SplashActivity
    private lateinit var userService: UserService

    @Before
    fun setup() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(jsonBody)
            .setHeader("content-type", "application/json")
        mockWebServer.enqueue(mockResponse)
        mockWebServer.start()

        val gson = GsonBuilder().setLenient().create()
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
        activity = buildActivity(SplashActivity::class.java).create().start().get()
    }

    @Test
    fun whenTokenIsNull_shouldDisplayLoginActivity() {
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        val expectedActivity = Intent(activity, LoginActivity::class.java)
        assertEquals(expectedActivity.component, shadowOf(activity).nextStartedActivity.component)
    }

}