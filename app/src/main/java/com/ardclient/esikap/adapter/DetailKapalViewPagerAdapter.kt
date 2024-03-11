package com.ardclient.esikap.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ardclient.esikap.fragment.DetailKapalFragment

class DetailKapalViewPagerAdapter(fa: FragmentActivity, private val listOfTitle: List<String>, private val data: String) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return listOfTitle.size
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return DetailKapalFragment.newInstance(data)
            1 -> return DetailKapalFragment.newInstance(data)
        }

        return DetailKapalFragment()
    }

}