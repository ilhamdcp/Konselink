package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ClientService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ClientViewModelFactoryTest {
    private lateinit var activity: MainActivity
    private lateinit var db: ApplicationDatabase
    private lateinit var clientService: ClientService
    private lateinit var counselorViewModelFactory: ClientViewModelFactory

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        db = Room.inMemoryDatabaseBuilder(activity.applicationContext, ApplicationDatabase::class.java).build()
        clientService = ClientService.create()
        counselorViewModelFactory = ClientViewModelFactory(activity.applicationContext, clientService, db)
    }

    @Test
    fun givenCounselorViewModelClass_whenGettingInstance_shouldReturnRegistrationViewModel() {
        val counselorViewModel = counselorViewModelFactory.create(ClientViewModel::class.java)
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