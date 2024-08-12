package com.munchbot.munchbot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.munchbot.munchbot.Utils.BtnContinueClickListener
import com.munchbot.munchbot.Utils.SignUpAdapter
import com.munchbot.munchbot.databinding.SignUpBinding

class SignUp : AppCompatActivity() {
    private lateinit var binding: SignUpBinding
    lateinit var viewPager: ViewPager
    lateinit var adapter: SignUpAdapter
    private lateinit var btnContinueClickListener: BtnContinueClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        adapter = SignUpAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        btnContinueClickListener = BtnContinueClickListener(viewPager)

        binding.signUpButton.setOnClickListener {
            val currentFragmentIndex = viewPager.currentItem
            btnContinueClickListener.handleBtnContinueClick(currentFragmentIndex)
        }
    }
}
