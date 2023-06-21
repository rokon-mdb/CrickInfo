package com.kamrulhasan.crickinfo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kamrulhasan.crickinfo.ui.fragment.LiveMatchFragment
import com.kamrulhasan.crickinfo.ui.fragment.RecentMatchFragment
import com.kamrulhasan.crickinfo.ui.fragment.UpcomingMatchFragment
import com.kamrulhasan.crickinfo.utils.VIEW_PAGER_COUNTER

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return VIEW_PAGER_COUNTER
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> {
                //Upcoming Fragment
                UpcomingMatchFragment()
            }
            2 -> {
                //Recent Fragment
                RecentMatchFragment()
            }
            else -> {
                // Live Fragment
                LiveMatchFragment()
            }

        }
    }
}