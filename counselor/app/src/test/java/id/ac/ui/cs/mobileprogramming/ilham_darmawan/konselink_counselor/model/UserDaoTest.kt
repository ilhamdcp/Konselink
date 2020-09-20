package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model

import android.os.Build
import androidx.room.Room
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.ApplicationDatabase
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main.MainActivity
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.User
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.room.UserDao
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class UserDaoTest {
    private lateinit var applicationDatabase: ApplicationDatabase
    private lateinit var activity: MainActivity
    private lateinit var dao: UserDao
    @Before
    fun setUp() {
        activity = buildActivity(MainActivity::class.java).create().get()
        applicationDatabase = Room.inMemoryDatabaseBuilder(
            activity.applicationContext,
            ApplicationDatabase::class.java
        )
            .allowMainThreadQueries().build()
        applicationDatabase.clearAllTables()
        dao = applicationDatabase.userDao()
    }

    @After
    fun tearDown() {
        applicationDatabase.close()
    }

    @Test
    fun whenSuccessfullyInsert_dbSizeShouldIncrease() {
        val user = User(0, 1,"academicId", "name", "username", "role")
        dao.insert(user)
        assertEquals(dao.getAllUsers().size, 1)
    }

    @Test
    fun whenRetrievingAllUsers_shouldReturnAllUsers() {
        val user1 = User(academicId = "academicId", fullname = "name", username = "username", role = "role")
        val user2 = User(academicId = "academicId", fullname = "name", username = "username", role = "role")

        dao.insert(user1)
        dao.insert(user2)
        val retrievedUser = dao.getAllUsers()
        assertEquals(2, dao.getAllUsers().size)
    }

    @Test
    fun whenSuccessfullyDelete_dbSizeShouldDecrease() {
        val user1 = User(academicId = "academicId", fullname = "name", username = "username", role = "role")
        val user2 = User(academicId = "academicId", fullname = "name", username = "username", role = "role")

        dao.insert(user1)
        dao.insert(user2)
        val retrievedUser = dao.getAllUsers()[0]
        dao.delete(retrievedUser)
        assertEquals(1, dao.getAllUsers().size)
    }

    @Test
    fun givenNonEmptyRow_whenRetrievingLatestUser_shouldReturnUserLiveData() {
        val user = User(academicId = "academicId", fullname = "name", username = "username", role = "role")
        dao.insert(user)

        dao.getLatestUser().observeForever {
            Assert.assertEquals("name", it.fullname)
            Assert.assertEquals(1, it.id)
        }
    }
}