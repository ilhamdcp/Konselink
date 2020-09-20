package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration

import android.os.Build
import android.os.Looper.getMainLooper
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.buildActivity
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
class RegistrationActivityTest {
    private lateinit var activity: RegistrationActivity

    @Before
    fun setUp() {
        activity = buildActivity(RegistrationActivity::class.java).create().get()
    }

    @Test
    fun whenLoaded_shouldDisplayContainViewPager() {
        shadowOf(getMainLooper()).idle()
        val fragment = activity.supportFragmentManager.findFragmentByTag("firstPage")
        assertEquals(FirstPageFragment::class.java.name, fragment?.javaClass?.name)
    }

}