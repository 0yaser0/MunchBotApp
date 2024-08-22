package com.munchbot.munchbot.ui.fragments.getStarted

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.AnimationUtils
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.GetStarted2Binding
import com.munchbot.munchbot.ui.main_view.GetStarted

class GetStarted2Fragment : MunchBotFragments() {
    private var _binding: GetStarted2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GetStarted2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)

        AnimationUtils.startJumpingAnimation(binding.playPetJump, -60f, 1000, 20)

        binding.next.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 2
        }

        binding.skip.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 2
        }

        binding.arrowBack.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 0
        }
    }
}
