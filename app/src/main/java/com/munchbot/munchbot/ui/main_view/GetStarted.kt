package com.munchbot.munchbot.ui.main_view

import android.os.Bundle
import com.munchbot.munchbot.MunchBotActivity
import com.munchbot.munchbot.databinding.GetStartedBinding
import com.munchbot.munchbot.ui.adapters.GetstartedAdapter

class GetStarted : MunchBotActivity() {

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
