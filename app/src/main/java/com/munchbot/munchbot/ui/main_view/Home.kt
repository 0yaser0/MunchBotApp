package com.munchbot.munchbot.ui.main_view

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.munchbot.munchbot.MunchBotActivity
import com.munchbot.munchbot.databinding.HomeBinding
import com.munchbot.munchbot.ui.adapters.HomeAdapter

class Home : MunchBotActivity() {

    private lateinit var binding: HomeBinding
    lateinit var viewPager: ViewPager
    lateinit var adapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        adapter = HomeAdapter(supportFragmentManager)
        viewPager.adapter = adapter


    }

}