package com.ardclient.esikap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ardclient.esikap.adapter.DetailKapalViewPagerAdapter
import com.ardclient.esikap.fragment.DetailKapalFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailKapalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kapal)

        val header = findViewById<MaterialToolbar>(R.id.topAppBar)
        val tabHeader = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)


        // handle on navigation back
        header.setNavigationOnClickListener {
            super.onBackPressed();
        }

        // Handle videpager
        val listOfTitles = arrayListOf<String>()
        listOfTitles.add("Informasi Kapal")
        listOfTitles.add("Dokumen Kapal")

        val DATA_TO_SEND = "HOLLAW"

        val pagerAdapter = DetailKapalViewPagerAdapter(this, listOfTitles, DATA_TO_SEND)
        viewPager.adapter = pagerAdapter

        // Integrate with tabLayout
        TabLayoutMediator(tabHeader, viewPager) {
            tab: TabLayout.Tab, position: Int -> tab.text = listOfTitles[position]
        }.attach()
    }
}