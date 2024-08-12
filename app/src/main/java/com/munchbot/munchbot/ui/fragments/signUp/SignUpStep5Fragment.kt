package com.munchbot.munchbot.ui.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.databinding.SignUp5Binding

class SignUpStep5Fragment : Fragment() {
    private lateinit var binding: SignUp5Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUp5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)
        SetupUI.setupUI(binding.root)
    }
}