package com.munchbot.munchbot.ui.fragments.home.health

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.FragmentHealth1Binding // Ensure the correct binding class is imported

class Health1Fragment : MunchBotFragments() {
    private var _binding: FragmentHealth1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealth1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.black)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
