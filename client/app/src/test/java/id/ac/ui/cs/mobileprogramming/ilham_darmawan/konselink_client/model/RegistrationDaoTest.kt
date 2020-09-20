package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model

import android.os.Build
import android.os.Looper
import android.os.Looper.getMainLooper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration.RegistrationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.Registration
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.model.room.RegistrationDao
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RegistrationDaoTest {
    private lateinit var applicationDatabase: ApplicationDatabase
    private lateinit var dao: RegistrationDao

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val activity = Robolectric.buildActivity(RegistrationActivity::class.java).create().get()
        applicationDatabase = Room.inMemoryDatabaseBuilder(
            activity.applicationContext,
            ApplicationDatabase::class.java
        )
            .allowMainThreadQueries().build()
        applicationDatabase.clearAllTables()
        dao = applicationDatabase.registrationDao()
    }

    private fun <T> LiveData<T>.blockingObserve(): T? {
        var value: T? = null
        val latch = CountDownLatch(1)

        val observer = Observer<T> { t ->
            value = t
            latch.countDown()
        }

        observeForever(observer)

        latch.await(2, TimeUnit.SECONDS)
        return value
    }

    @Test
    fun whenSuccessfullyInsert_dbSizeShouldIncrease() {
        shadowOf(getMainLooper()).idle()
        val registration = Registration(name="name")

        dao.insert(registration)

        assertEquals(1, dao.getAllRegistrations().blockingObserve()?.size)
    }
}

