package com.munchbot.munchbot.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.databinding.SignUp5Binding

class SignUpStep5 : AppCompatActivity() {
    private lateinit var binding: SignUp5Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUp5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBarColor(window, R.color.status_bar_color)

        SetupUI.setupUI(binding.root)

    }
}
