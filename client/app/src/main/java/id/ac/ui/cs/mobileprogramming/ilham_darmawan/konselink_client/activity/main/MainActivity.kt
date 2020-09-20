package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.activity.main

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.service.ChatNotificationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_client.service.CommonNotificationService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var homeFragment: HomeFragment
    private lateinit var counselorFragment: CounselorFragment
    private lateinit var consultationFragment: ConsultationFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var active: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.main_fragment_container, counselorFragment, "counselorFragment")
            .hide(counselorFragment).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.main_fragment_container, profileFragment, "profileFragment")
            .hide(profileFragment).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.main_fragment_container, consultationFragment, "consultationFragment")
            .hide(consultationFragment).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.main_fragment_container, homeFragment, "homeFragment")
            .commit()

        if (savedInstanceState == null) {
            active = homeFragment
            supportFragmentManager.beginTransaction().show(active).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        startCommonNotificationService()
        startChatNotificationService()
        initializeFragment()
        assignElements()

        if (intent.getStringExtra("activeFragment") != null) {
            when (intent.getStringExtra("activeFragment")) {
                "homeFragment" -> {
                    bottom_navigation.selectedItemId = R.id.action_home
                    active = homeFragment
                }

                "profileFragment" -> {
                    bottom_navigation.selectedItemId = R.id.action_profile
                    active = profileFragment
                }

                "counselorFragment" -> {
                    bottom_navigation.selectedItemId = R.id.action_counselor
                    active = counselorFragment
                }

                "consultationFragment" -> {
                    bottom_navigation.selectedItemId = R.id.action_consultation
                    active = consultationFragment
                }

                else -> {
                    bottom_navigation.selectedItemId = R.id.action_home
                    active = homeFragment
                }
            }

            supportFragmentManager.beginTransaction().show(active).commit()
            intent.removeExtra("activeFragment")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        assignElements()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("activeFragment", active.tag)
    }

    private fun startCommonNotificationService() {
        val commonNotification = Intent(this, CommonNotificationService::class.java)
        startService(commonNotification)
    }

    private fun startChatNotificationService() {
        val chatNotification = Intent(this, ChatNotificationService::class.java)
        startService(chatNotification)
    }

    private fun initializeFragment() {
        homeFragment =
            if (supportFragmentManager.findFragmentByTag("homeFragment") != null) supportFragmentManager.findFragmentByTag(
                "homeFragment"
            ) as HomeFragment else HomeFragment()
        profileFragment =
            if (supportFragmentManager.findFragmentByTag("profileFragment") != null) supportFragmentManager.findFragmentByTag(
                "profileFragment"
            ) as ProfileFragment else ProfileFragment()
        counselorFragment =
            if (supportFragmentManager.findFragmentByTag("counselorFragment") != null) supportFragmentManager.findFragmentByTag(
                "counselorFragment"
            ) as CounselorFragment else CounselorFragment()
        consultationFragment =
            if (supportFragmentManager.findFragmentByTag("consultationFragment") != null) supportFragmentManager.findFragmentByTag(
                "consultationFragment"
            ) as ConsultationFragment else ConsultationFragment()
    }

    private fun assignElements() {
        val navigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    Log.d("Fragment", "${homeFragment != null}")
                    supportFragmentManager.beginTransaction().hide(active).show(homeFragment)
                        .commit()
                    active = homeFragment
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_profile -> {
                    Log.d("Fragment", "${profileFragment != null}")
                    supportFragmentManager.beginTransaction().hide(active).show(profileFragment)
                        .commit()
                    active = profileFragment
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_counselor -> {
                    Log.d("Fragment", "${counselorFragment != null}")
                    Log.d("SELECTED", "COUNSELOR")
                    supportFragmentManager.beginTransaction().hide(active).show(counselorFragment)
                        .commit()
                    active = counselorFragment
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_consultation -> {
                    Log.d("Fragment", "${consultationFragment != null}")
                    Log.d("SELECTED", "CONSULTATION")
                    supportFragmentManager.beginTransaction().hide(active)
                        .show(consultationFragment).commit()
                    active = consultationFragment
                    return@setOnNavigationItemSelectedListener true
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }
}