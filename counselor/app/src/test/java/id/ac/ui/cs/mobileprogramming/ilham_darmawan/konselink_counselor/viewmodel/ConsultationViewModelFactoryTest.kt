package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.viewmodel

import android.os.Build
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.retrofit.ConsultationService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ConsultationViewModelFactoryTest {
    private lateinit var consultationViewModelFactory: ConsultationViewModelFactory
    private lateinit var activity: MainActivity
    private lateinit var consultationService: ConsultationService
    private lateinit var db: ApplicationDatabase

    @Before
    fun setUp() {
        activity = buildActivity(MainActivity::class.java).create().get()
        consultationService = ConsultationService.create()
        db = Room.inMemoryDatabaseBuilder(activity, ApplicationDatabase::class.java).allowMainThreadQueries().build()
        consultationViewModelFactory = ConsultationViewModelFactory(activity, consultationService, db)
    }

    @Test
    fun givenConsultationViewModelClass_whenGettingInstance_shouldReturnConsultationViewModel() {
        val consultationViewModel = consultationViewModelFactory.create(ConsultationViewModel::class.java)
        assertThat(consultationViewModel::class.java).isAssignableTo(ConsultationViewModel::class.java)
    }

    @Test
    fun givenNonConsultationViewModelClass_whenGettingInstance_shouldReturnNull() {
        val consultationViewModel = consultationViewModelFactory.create(RegistrationViewModel::class.java)
        assertThat(consultationViewModel).isNull()
    }
}