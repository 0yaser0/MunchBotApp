package com.munchbot.munchbot.Utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.munchbot.munchbot.ui.fragments.getStarted.GetStarted1Fragment
import com.munchbot.munchbot.ui.fragments.getStarted.GetStarted2Fragment
import com.munchbot.munchbot.ui.fragments.getStarted.GetStarted3Fragment

class GetstartedAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GetStarted1Fragment()
            1 -> GetStarted2Fragment()
            2 -> GetStarted3Fragment()
            else -> GetStarted1Fragment()
        }
    }
}
