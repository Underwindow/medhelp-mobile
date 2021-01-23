package com.podoynikov.medhelpmobile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.podoynikov.medhelpmobile.fragments.LogoutFragment
import com.podoynikov.medhelpmobile.R
import com.podoynikov.medhelpmobile.fragments.ScheduleFragment
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val scheduleFragment : ScheduleFragment = ScheduleFragment()
    private val logoutFragment : LogoutFragment = LogoutFragment()
    private var selectedFragment : Fragment = scheduleFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener{
        when (it.itemId) {
            R.id.nav_schedule,
            R.id.nav_logout -> {
                // handle click
                selectedFragment = getFragmentByMenuItem(it)
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
                true
            }
            else -> false
        }
    }

    private fun getFragmentByMenuItem(menuItem: MenuItem) : Fragment {
        when (menuItem.itemId){
            R.id.nav_schedule -> return scheduleFragment
            R.id.nav_logout -> return  logoutFragment
            else -> throw Exception("No such fragment")
        }
    }
}