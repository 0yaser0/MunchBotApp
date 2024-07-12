package com.munchbot.munchbot

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)

        val jumpingImage = findViewById<ImageView>(R.id.jumpingImage)
        startJumpingAnimation(jumpingImage)
    }

    private fun startJumpingAnimation(imageView: ImageView) {
        val jumpUp = ObjectAnimator.ofFloat(imageView, "translationY", 0f, -50f)
        jumpUp.duration = 1000

        val jumpDown = ObjectAnimator.ofFloat(imageView, "translationY", -50f, 0f)
        jumpDown.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(jumpUp, jumpDown)
        animatorSet.startDelay = 10
        animatorSet.start()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Restart the animation when it ends
                animatorSet.start()
            }
        })
    }
}
