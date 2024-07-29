package com.munchbot.munchbot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.munchbot.munchbot.Utils.GetstartedAdapter
import com.munchbot.munchbot.databinding.GetStartedBinding

class GetStarted : AppCompatActivity() {

    lateinit var binding: GetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = GetstartedAdapter(this)
        binding.viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.viewPager)

    }
}
