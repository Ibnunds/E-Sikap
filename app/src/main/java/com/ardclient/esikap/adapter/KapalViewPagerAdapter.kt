package com.ardclient.esikap.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ardclient.esikap.fragment.kapal.KapalAgenDoneFragment
import com.ardclient.esikap.fragment.kapal.KapalAgenFragment
import com.ardclient.esikap.fragment.kapal.KapalLokalFragment

class KapalViewPagerAdapter(fa: FragmentActivity, private val listOfTitle: List<String>) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return listOfTitle.size
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return KapalLokalFragment()
            1 -> return KapalAgenFragment()
            2 -> return KapalAgenDoneFragment()
        }

        return KapalLokalFragment()
    }
}