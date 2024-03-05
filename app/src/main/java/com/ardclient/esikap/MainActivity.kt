package com.ardclient.esikap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // define fragment
        val dashboardFragment = DashboardFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(dashboardFragment)

        bottomNav.setOnItemSelectedListener{
            when(it.itemId){
                R.id.imHome -> {
                    setCurrentFragment(dashboardFragment)
                    true
                }

                R.id.imProfile -> {
                    setCurrentFragment(profileFragment)
                    true
                }
                else -> false
            }
        }

    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit()
    }
}