package com.podoynikov.medhelpmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ScheduleFragment()).commit()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener{
        when (it.itemId) {
            R.id.nav_schedule, R.id.nav_checklist, R.id.nav_logout -> {
                // handle click
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, getFragmentByMenuItem(it)).commit()
                true
            }
            else -> false
        }
    }

    fun getFragmentByMenuItem(menuItem: MenuItem) : Fragment {
        when (menuItem.itemId){
            R.id.nav_schedule -> return ScheduleFragment()
            R.id.nav_checklist -> return ChecklistFragment()
            R.id.nav_logout -> return  LogoutFragment()
            else -> throw Exception("No such fragment")
        }
    }
}