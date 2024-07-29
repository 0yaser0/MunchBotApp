package com.munchbot.munchbot.ui.fragments.getStarted

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.munchbot.munchbot.GetStarted
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.AnimationUtils.Companion.startJumpingAnimation
import com.munchbot.munchbot.databinding.GetStarted1Binding
import com.munchbot.munchbot.Utils.StatusBarUtils

class GetStarted1Fragment : Fragment() {
    private var _binding: GetStarted1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GetStarted1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)

        jumpingAnimation(binding.jumpingImage)

        binding.next.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 1
        }

        binding.skip.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 2
        }
    }

    private fun jumpingAnimation(imageView: ImageView) {
        startJumpingAnimation(imageView, -50f, 1000, 20)
    }
}
