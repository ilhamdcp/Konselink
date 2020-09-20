package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration.RegistrationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.retrofit.RegistrationService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
class RegistrationViewModelFactoryTest {
    private lateinit var activity: RegistrationActivity
    private lateinit var db: ApplicationDatabase
    private lateinit var registrationService: RegistrationService
    private lateinit var registrationViewModelFactory: RegistrationViewModelFactory

    @Before
    fun setUp() {
        activity = buildActivity(RegistrationActivity::class.java).create().get()
        db = Room.inMemoryDatabaseBuilder(activity.applicationContext, ApplicationDatabase::class.java).build()
        registrationService = RegistrationService.create()
        registrationViewModelFactory = RegistrationViewModelFactory(activity.applicationContext, registrationService, db)
    }

    @Test
    fun givenRegistrationViewModelClass_whenGettingInstance_shouldReturnRegistrationViewModel() {
        val registrationViewModel = registrationViewModelFactory.create(RegistrationViewModel::class.java)
        assertNotNull(registrationViewModel)
    }

    @Test
    fun givenNonRegistrationViewModelClass_whenGettingInstance_shouldReturnNull() {
        val registrationViewModel = registrationViewModelFactory.create(ProfileViewModel::class.java)
        assertNull(registrationViewModel)
    }

    @Test
    fun whenCheckingViewModelFactoryIsViewModelNewInstance_shouldReturnTrue() {
        assertTrue(registrationViewModelFactory is ViewModelProvider.NewInstanceFactory)
    }

}