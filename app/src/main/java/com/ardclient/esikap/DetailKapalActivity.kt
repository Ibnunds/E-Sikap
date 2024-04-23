package com.ardclient.esikap

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ardclient.esikap.adapter.DetailKapalViewPagerAdapter
import com.ardclient.esikap.fragment.DetailKapalFragment
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.utils.LocaleHelper
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

        // get existing data
        val existingData = intent.getParcelableExtra<KapalModel>("KAPAL")

        // handle on navigation back
        header.setNavigationOnClickListener {
            finish()
        }

        // Handle videpager
        val listOfTitles = arrayListOf<String>()
        listOfTitles.add(getString(R.string.informasi_kapal_title))
        listOfTitles.add(getString(R.string.document_kapal_title))


        val pagerAdapter = DetailKapalViewPagerAdapter(this, listOfTitles, existingData!!)
        viewPager.adapter = pagerAdapter

        // Integrate with tabLayout
        TabLayoutMediator(tabHeader, viewPager) {
            tab: TabLayout.Tab, position: Int -> tab.text = listOfTitles[position]
        }.attach()
    }

    override fun attachBaseContext(base: Context?) {
        LocaleHelper().setLocale(base!!, LocaleHelper().getLanguage(base))
        super.attachBaseContext(LocaleHelper().onAttach(base))
    }
}