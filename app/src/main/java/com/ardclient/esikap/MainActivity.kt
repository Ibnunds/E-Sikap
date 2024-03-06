package com.ardclient.esikap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ardclient.esikap.modal.BottomSheetModal
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val bottomSheetModal = BottomSheetModal()


        // -- Handle bottom navigation

        // define fragment
        val dashboardFragment = DashboardFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(dashboardFragment)

        // handle bottom nav
        bottomNav.setOnItemSelectedListener{
            when(it.itemId){
                R.id.imHome -> {
                    fab.visibility = View.VISIBLE
                    setCurrentFragment(dashboardFragment)
                    true
                }

                R.id.imProfile -> {
                    fab.visibility = View.GONE
                    setCurrentFragment(profileFragment)
                    true
                }
                else -> false
            }
        }

        fab.setOnClickListener {
            bottomSheetModal.show(supportFragmentManager, BottomSheetModal.TAG)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.flFragment, fragment).commit()
    }
}