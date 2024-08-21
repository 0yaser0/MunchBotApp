@file:Suppress("DEPRECATION")

package com.munchbot.munchbot.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep1Fragment
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep2Fragment
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep3Fragment
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep4Fragment
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep5Fragment

class SignUpAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SignUpStep1Fragment()
            1 -> SignUpStep2Fragment()
            2 -> SignUpStep3Fragment()
            3 -> SignUpStep4Fragment()
            4 -> SignUpStep5Fragment()
            else -> throw RuntimeException("Invalid position")
        }
    }

    override fun getCount(): Int {
        return 5
    }

    fun navigateToFragment(viewPager: ViewPager, index: Int) {
        if (index in 0 until count) {
            viewPager.currentItem = index
        } else {
            throw IllegalArgumentException("Invalid fragment index: $index")
        }
    }
}

class BtnContinueClickListener(private val viewPager: ViewPager) {

    fun handleBtnContinueClick(fragmentNumber: Int) {
        when (fragmentNumber) {
            0 -> performActionForFragment1()
            1 -> performActionForFragment2()
            2 -> performActionForFragment3()
            3 -> performActionForFragment4()
            4 -> performActionForFragment5()
            else -> throw IllegalArgumentException("Unknown fragment number: $fragmentNumber")
        }
    }

    private fun performActionForFragment1() {
        val fragment = (viewPager.adapter as? SignUpAdapter)?.instantiateItem(
            viewPager,
            0
        ) as? SignUpStep1Fragment
        fragment?.signUpValidate()
    }

    private fun performActionForFragment2() {
        val fragment = (viewPager.adapter as? SignUpAdapter)?.instantiateItem(
            viewPager,
            1
        ) as? SignUpStep2Fragment
        fragment?.validateInputAndProceed { isValid ->
            if (isValid) {
                (viewPager.adapter as? SignUpAdapter)?.navigateToFragment(viewPager, 2)
            }
        }
    }

    private fun performActionForFragment3() {
        val fragment = (viewPager.adapter as? SignUpAdapter)?.instantiateItem(
            viewPager,
            2
        ) as? SignUpStep3Fragment
        fragment?.saveSelectedPetType()

        if (!fragment?.selectedImageValue.isNullOrEmpty() || !fragment?.selectedSpinnerValue.isNullOrEmpty()) {
            (viewPager.adapter as? SignUpAdapter)?.navigateToFragment(viewPager, 3)
        }
    }

    private fun performActionForFragment4() {
        val fragment = (viewPager.adapter as? SignUpAdapter)?.instantiateItem(
            viewPager,
            3
        ) as? SignUpStep4Fragment
        fragment?.validatePetProfileInputAndProceed { isValid ->
            if (isValid) {
                (viewPager.adapter as? SignUpAdapter)?.navigateToFragment(viewPager, 4)
            }
        }
    }

    private fun performActionForFragment5() {
        // Actions specific to Fragment 5
    }
}
