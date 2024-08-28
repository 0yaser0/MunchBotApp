@file:Suppress("DEPRECATION")

package com.munchbot.munchbot.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.munchbot.munchbot.ui.fragments.home.HealthFragment
import com.munchbot.munchbot.ui.fragments.home.Home1Fragment
import com.munchbot.munchbot.ui.fragments.home.PlannerFragment
import com.munchbot.munchbot.ui.fragments.home.VetFragment

class HomeAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Home1Fragment()
            1 -> PlannerFragment()
            2 -> VetFragment()
            3 -> HealthFragment()
            else -> throw RuntimeException("Invalid position")
        }
    }

    override fun getCount(): Int {
        return 4
    }

    fun navigateToFragment(viewPager: ViewPager, index: Int) {
        if (index in 0 until count) {
            viewPager.currentItem = index
        } else {
            throw IllegalArgumentException("Invalid fragment index: $index")
        }
    }
}