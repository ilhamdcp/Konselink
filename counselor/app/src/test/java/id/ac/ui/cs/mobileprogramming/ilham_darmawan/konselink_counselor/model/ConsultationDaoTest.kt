package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.chat.ConsultationActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.Consultation
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ConsultationDaoTest {
    private lateinit var activity: ConsultationActivity
    private lateinit var db: ApplicationDatabase

    @get:Rule val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        activity = buildActivity(ConsultationActivity::class.java).create().get()
        db = Room.inMemoryDatabaseBuilder(activity, ApplicationDatabase::class.java)
            .allowMainThreadQueries().build()
        db.clearAllTables()
    }

    @After
    fun tearDown() {
        db.close()
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
    fun givenNonConflictingEntity_whenInsertOrUpdateConsultation_shouldIncreaseDbSize() {
        val dao = db.consultationDao()
        val consultation = Consultation(scheduleId = 123)
        dao.insertOrUpdateConsultation(consultation)

        val response = dao.getConsultation(123).blockingObserve()

        assertThat(response?.scheduleId).isEqualTo(123)
    }

    @Test
    fun givenConflictingEntity_whenInsertOrUpdateConsultation_shoulUpdateEntity() {
        val dao = db.consultationDao()
        val consultation = Consultation(scheduleId = 123)
        dao.insertOrUpdateConsultation(consultation)

        val response1 = dao.getConsultation(123).blockingObserve()
        response1?.scheduleId = 556
        dao.insertOrUpdateConsultation(response1!!)

        val response2 = dao.getConsultation(556).blockingObserve()

        assertThat(response2?.scheduleId).isEqualTo(556)
    }
}