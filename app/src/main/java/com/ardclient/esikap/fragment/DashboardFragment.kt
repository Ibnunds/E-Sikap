package com.ardclient.esikap.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.DetailKapalActivity
import com.ardclient.esikap.KapalActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.adapter.DetailKapalViewPagerAdapter
import com.ardclient.esikap.adapter.KapalAdapter
import com.ardclient.esikap.adapter.KapalViewPagerAdapter
import com.ardclient.esikap.database.kapal.KapalRoomDatabase
import com.ardclient.esikap.databinding.FragmentDashboardBinding
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.utils.LocaleHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DashboardFragment : Fragment(R.layout.fragment_dashboard){
    private lateinit var binding: FragmentDashboardBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardBinding.bind(view)

        // Handle videpager
        val listOfTitles = arrayListOf<String>()
        listOfTitles.add(getString(R.string.from_local_title))
        listOfTitles.add(getString(R.string.from_agen_title))

        val pagerAdapter = KapalViewPagerAdapter(requireActivity(), listOfTitles)


        with(binding){
            viewPager.adapter = pagerAdapter

            // Integrate with tabLayout
            TabLayoutMediator(tabLayout, viewPager) {
                    tab: TabLayout.Tab, position: Int -> tab.text = listOfTitles[position]
            }.attach()
        }
    }
}