package id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.activity.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.R
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.model.socketio.CommonNotification
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.service.ChatNotificationService
import id.ac.ui.cs.mobileprogramming.ilham_darmawan.konselink_counselor.service.CommonNotificationService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var homeFragment: HomeFragment
    private lateinit var clientRequestFragment: ClientRequestFragment
    private lateinit var scheduleFragment: ScheduleFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var active: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeFragment()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, clientRequestFragment, "clientRequestFragment")
                .hide(clientRequestFragment).commit()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, profileFragment, "profileFragment")
                .hide(profileFragment).commit()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, scheduleFragment, "scheduleFragment")
                .hide(scheduleFragment).commit()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, homeFragment, "homeFragment")
                .commit()
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

                "clientRequestFragment" -> {
                    bottom_navigation.selectedItemId = R.id.action_counselor
                    active = clientRequestFragment
                }

                "scheduleFragment" -> {
                    bottom_navigation.selectedItemId = R.id.action_schedule
                    active = clientRequestFragment
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

        clientRequestFragment =
            if (supportFragmentManager.findFragmentByTag("clientRequestFragment") != null) supportFragmentManager.findFragmentByTag(
                "clientRequestFragment"
            ) as ClientRequestFragment else ClientRequestFragment()
        scheduleFragment =
            if (supportFragmentManager.findFragmentByTag("scheduleFragment") != null) supportFragmentManager.findFragmentByTag(
                "scheduleFragment"
            ) as ScheduleFragment else ScheduleFragment()
    }

    private fun assignElements() {
        val navigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.action_home -> {
                    supportFragmentManager.beginTransaction().hide(active).show(homeFragment).commit()
                    active = homeFragment
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_profile -> {
                    supportFragmentManager.beginTransaction().hide(active).show(profileFragment).commit()
                    active = profileFragment
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_counselor -> {
                    Log.d("SELECTED", "COUNSELOR")
                    supportFragmentManager.beginTransaction().hide(active).show(clientRequestFragment).commit()
                    active = clientRequestFragment
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.action_schedule -> {
                    Log.d("SELECTED", "CONSULTATION")
                    supportFragmentManager.beginTransaction().hide(active).show(scheduleFragment).commit()
                    active = scheduleFragment
                    return@setOnNavigationItemSelectedListener true
                }
            }

            return@setOnNavigationItemSelectedListener true
        }
    }
}