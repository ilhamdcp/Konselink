package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity

import android.os.Build
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main.MainActivity
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class MainActivityTest {
    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        activity = buildActivity(MainActivity::class.java).create().start().get()
    }

    @Test
    fun whenDisplayed_shouldDisplayBottomNav() {
        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        assertNotNull(bottomNav)
    }
}
