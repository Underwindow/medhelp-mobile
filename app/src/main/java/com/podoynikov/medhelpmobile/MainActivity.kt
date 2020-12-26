package com.podoynikov.medhelpmobile

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception

class MainActivity : AppCompatActivity() {
//    val jwtToken = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE).getString("JWT_TOKEN", "")
    private val scheduleFragment : ScheduleFragment = ScheduleFragment()
    private val checklistFragment : ChecklistFragment = ChecklistFragment()
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
            R.id.nav_schedule, R.id.nav_checklist, R.id.nav_logout -> {
                // handle click
                selectedFragment = getFragmentByMenuItem(it)
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
                true
            }
            else -> false
        }
    }

    fun getFragmentByMenuItem(menuItem: MenuItem) : Fragment {
        when (menuItem.itemId){
            R.id.nav_schedule -> return scheduleFragment
            R.id.nav_checklist -> return checklistFragment
            R.id.nav_logout -> return  logoutFragment
            else -> throw Exception("No such fragment")
        }
    }
}