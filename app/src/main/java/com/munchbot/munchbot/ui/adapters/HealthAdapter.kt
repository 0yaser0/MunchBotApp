package com.munchbot.munchbot.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.munchbot.munchbot.ui.fragments.home.HealthFragment
import com.munchbot.munchbot.ui.fragments.home.health.Health1Fragment
import com.munchbot.munchbot.ui.fragments.home.health.Health2Fragment
import com.munchbot.munchbot.ui.fragments.home.health.Health3Fragment
import com.munchbot.munchbot.ui.fragments.home.health.Health4Fragment

class HealthAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HealthFragment()
            1 -> Health1Fragment()
            2 -> Health2Fragment()
            3 -> Health3Fragment()
            4 -> Health4Fragment()
            else -> throw RuntimeException("Invalid position")
        }
    }

    fun navigateToFragment(viewPager: ViewPager2, index: Int) {
        if (index in 0 until itemCount) {
            viewPager.currentItem = index
        } else {
            throw IllegalArgumentException("Invalid fragment index: $index")
        }
    }
}