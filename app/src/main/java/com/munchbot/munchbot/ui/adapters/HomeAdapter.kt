@file:Suppress("DEPRECATION")

package com.munchbot.munchbot.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.munchbot.munchbot.ui.fragments.home.Home1Fragment
import com.munchbot.munchbot.ui.fragments.home.Home2Fragment

class HomeAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> Home1Fragment()
            1 -> Home2Fragment()
            else -> throw RuntimeException("Invalid position")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}