package com.munchbot.munchbot.ui.fragments.getStarted

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.GetStarted3Binding
import com.munchbot.munchbot.ui.main_view.GetStarted
import com.munchbot.munchbot.ui.main_view.auth.Login
import com.munchbot.munchbot.ui.main_view.auth.SignUp

@Suppress("DEPRECATION")
class GetStarted3Fragment : Fragment() {
    private var _binding: GetStarted3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = GetStarted3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)

        binding.arrowBack.setOnClickListener {
            (activity as? GetStarted)?.binding?.viewPager?.currentItem = 1
        }

        binding.btnGetstarted.setOnClickListener {
            startActivity(Intent(requireActivity(), Login::class.java))
            requireActivity().overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
        }

        binding.doesnTHav.setOnClickListener {
            startActivity(Intent(requireActivity(), SignUp::class.java))
            requireActivity().overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
        }
    }
}
