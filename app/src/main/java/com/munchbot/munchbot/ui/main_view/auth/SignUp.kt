package com.munchbot.munchbot.ui.main_view.auth

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.viewpager.widget.ViewPager
import com.munchbot.munchbot.MunchBotActivity
import com.munchbot.munchbot.R
import com.munchbot.munchbot.databinding.SignUpBinding
import com.munchbot.munchbot.ui.adapters.BtnContinueClickListener
import com.munchbot.munchbot.ui.adapters.SignUpAdapter
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep1Fragment
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep2Fragment
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep3Fragment
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep4Fragment
import com.munchbot.munchbot.ui.fragments.signUp.SignUpStep5Fragment


class SignUp : MunchBotActivity() {
    private lateinit var binding: SignUpBinding
    lateinit var viewPager: CustomViewPager
    lateinit var adapter: SignUpAdapter
    private lateinit var btnContinueClickListener: BtnContinueClickListener
    companion object {
        private const val ANIMATION_DURATION = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        adapter = SignUpAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        viewPager.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.performClick()
                    false
                }
                else -> {
                    true
                }
            }
        }


        btnContinueClickListener = BtnContinueClickListener(viewPager, signUp = this)

        binding.signUpButton.setOnClickListener {
            val currentFragmentIndex = viewPager.currentItem
            btnContinueClickListener.handleBtnContinueClick(currentFragmentIndex)
        }

        binding.alreadyHav.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (currentFragment is SignUpStep1Fragment ||
                    currentFragment is SignUpStep2Fragment ||
                    currentFragment is SignUpStep3Fragment ||
                    currentFragment is SignUpStep4Fragment ||
                    currentFragment is SignUpStep5Fragment) {
                    Toast.makeText(this@SignUp, "You cannot go back from this step.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun updateProgress(progress: Int) {
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val progressBarWidth = binding.progressBar.width
            val indicatorWidth = binding.progressIndicator.width
            val indicatorPosition = progressBarWidth * (progress / 100f) - indicatorWidth / 2f

            ObjectAnimator.ofInt(binding.progressBar, "progress", progress).apply {
                duration = ANIMATION_DURATION
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }

            binding.progressIndicator.animate()
                .translationX(indicatorPosition)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            when (progress) {
                50 -> {
                    updateStepImage(R.id.stepsignupblur1, R.drawable.ic_stepsignupclean)
                    showckeckedImage(R.id.checkImage1, R.id.txt1, R.id.txt2, R.style.btn_36px_white, R.id.profile, R.style.txt_24px_blue)
                }
                80 -> {
                    updateStepImage(R.id.stepsignupblur2, R.drawable.ic_stepsignupclean)
                    showckeckedImage(R.id.checkImage2, R.id.txt2, R.id.txt3, R.style.btn_36px_white, R.id.questionare, R.style.txt_24px_blue)
                }
                100 -> {
                    showckeckedImage(R.id.checkImage3, R.id.txt3, R.id.txt3, R.style.btn_36px_white, R.id.questionare, R.style.txt_24px_blue)
                }
            }
        }, 2000)
    }

    private fun showckeckedImage(checkId: Int, textViewGoneId: Int, textViewstyledId: Int, styleId: Int, textViewstyled2Id: Int, style2Id: Int ) {
        val imageView = binding.root.findViewById<ImageView>(checkId)
        val textViewGone = binding.root.findViewById<TextView>(textViewGoneId)
        val textViewstyled = binding.root.findViewById<TextView>(textViewstyledId)
        val textViewstyled2 = binding.root.findViewById<TextView>(textViewstyled2Id)
        imageView.visibility = View.VISIBLE
        textViewGone.visibility = View.GONE
        textViewstyled.setTextAppearance(styleId)
        textViewstyled2.setTextAppearance(style2Id)
    }

    private fun updateStepImage(cercleblurId: Int, cerclecleanId: Int) {
        val cercleblur = binding.root.findViewById<ImageView>(cercleblurId)
        cercleblur.setImageResource(cerclecleanId)
    }

    fun updateButtonText(newText: String) {
         binding.signUpButton.text = newText
    }

    fun showLoader(show: Boolean) {
        binding.loaderLayout.visibility = if (show) View.VISIBLE else View.GONE
    }
}

class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            performClick()
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}


