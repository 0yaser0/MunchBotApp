package com.munchbot.munchbot.ui.fragments.home.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.Home1Binding

class Home4Fragment : MunchBotFragments() {
    private var _binding: Home1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Home1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.black)



    }

}