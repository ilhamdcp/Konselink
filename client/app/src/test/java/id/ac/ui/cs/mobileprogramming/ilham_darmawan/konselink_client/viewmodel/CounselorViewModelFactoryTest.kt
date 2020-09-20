package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.CounselorService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
class CounselorViewModelFactoryTest {
    private lateinit var activity: MainActivity
    private lateinit var db: ApplicationDatabase
    private lateinit var counselorService: CounselorService
    private lateinit var counselorViewModelFactory: CounselorViewModelFactory

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        db = Room.inMemoryDatabaseBuilder(activity.applicationContext, ApplicationDatabase::class.java).build()
        counselorService = CounselorService.create()
        counselorViewModelFactory = CounselorViewModelFactory(activity.applicationContext, counselorService, db)
    }

    @Test
    fun givenCounselorViewModelClass_whenGettingInstance_shouldReturnRegistrationViewModel() {
        val counselorViewModel = counselorViewModelFactory.create(CounselorViewModel::class.java)
        Assert.assertNotNull(counselorViewModel)
    }

    @Test
    fun givenNonCounselorViewModelClass_whenGettingInstance_shouldReturnNull() {
        val counselorViewModel = counselorViewModelFactory.create(RegistrationViewModel::class.java)
        Assert.assertNull(counselorViewModel)
    }

    @Test
    fun whenCheckingViewModelFactoryIsViewModelNewInstance_shouldReturnTrue() {
        Assert.assertTrue(counselorViewModelFactory is ViewModelProvider.NewInstanceFactory)
    }

}