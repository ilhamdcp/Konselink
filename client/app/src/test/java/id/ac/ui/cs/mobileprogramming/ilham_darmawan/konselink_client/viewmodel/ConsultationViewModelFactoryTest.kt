package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.ConsultationService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ConsultationViewModelFactoryTest {
    private lateinit var activity: MainActivity
    private lateinit var db: ApplicationDatabase
    private lateinit var consultationService: ConsultationService
    private lateinit var consultationViewModelFactory: ConsultationViewModelFactory

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        db = Room.inMemoryDatabaseBuilder(activity.applicationContext, ApplicationDatabase::class.java).build()
        consultationService = ConsultationService.create()
        consultationViewModelFactory = ConsultationViewModelFactory(activity.applicationContext, consultationService, db)
    }

    @Test
    fun givenCounselorViewModelClass_whenGettingInstance_shouldReturnRegistrationViewModel() {
        val consultationViewModel = consultationViewModelFactory.create(ConsultationViewModel::class.java)
        Assert.assertNotNull(consultationViewModel)
    }

    @Test
    fun givenNonCounselorViewModelClass_whenGettingInstance_shouldReturnNull() {
        val consultationViewModel = consultationViewModelFactory.create(RegistrationViewModel::class.java)
        Assert.assertNull(consultationViewModel)
    }

    @Test
    fun whenCheckingViewModelFactoryIsViewModelNewInstance_shouldReturnTrue() {
        Assert.assertTrue(consultationViewModelFactory is ViewModelProvider.NewInstanceFactory)
    }
}