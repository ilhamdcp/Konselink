package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R

class RegistrationActivity : AppCompatActivity() {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    val FIRST_PAGE = "firstPage"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = FirstPageFragment()
        fragmentTransaction.add(R.id.fragment_container, fragment, FIRST_PAGE)
        fragmentTransaction.commit()
    }
}