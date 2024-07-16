package com.munchbot.munchbot

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import com.munchbot.munchbot.databinding.GetStarted2Binding
import kotlin.math.abs

@Suppress("DEPRECATION")
class GetStarted2 : ComponentActivity() {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var endX: Float = 0f
    private var endY: Float = 0f
    private lateinit var binding: GetStarted2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GetStarted2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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


        AnimationUtils.startJumpingAnimation(binding.playPetJump, -60f, 1000, 20)

        binding.next.setOnClickListener {
            val intent = Intent(this, GetStarted3::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.skip.setOnClickListener {
            val intent = Intent(this, GetStarted3::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.arrowBack.setOnClickListener {
            val intent = Intent(this, GetStarted1::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        findViewById<View>(android.R.id.content).setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) return false

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.x
                        startY = event.y
                    }

                    MotionEvent.ACTION_UP -> {
                        endX = event.x
                        endY = event.y

                        if (isSwipeRight(startX, endX)) {
                            animateCircles()
                            val intent = Intent(this@GetStarted2, GetStarted1::class.java)
                            val options = ActivityOptions.makeCustomAnimation(
                                this@GetStarted2,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right
                            )
                            startActivity(intent, options.toBundle())
                        } else if (isSwipeLeft(startX, endX)) {
                            animateCircles()
                            val intent = Intent(this@GetStarted2, GetStarted3::class.java)
                            val options = ActivityOptions.makeCustomAnimation(
                                this@GetStarted2,
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            startActivity(intent, options.toBundle())
                        } else if (isClick(startX, endX, startY, endY)) {
                            v?.performClick()
                        }
                    }
                }
                return true
            }
        })
    }

    private fun isSwipeLeft(startX: Float, endX: Float): Boolean {
        return endX < startX - 100
    }

    private fun isSwipeRight(startX: Float, endX: Float): Boolean {
        return endX > startX + 100
    }

    private fun isClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val deltaX = abs(endX - startX)
        val deltaY = abs(endY - startY)
        return deltaX < 10 && deltaY < 10
    }
    private fun animateCircles() {

        ViewCompat.animate(binding.circleClean).translationX(-100f).setDuration(50)
            .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View) {
                    binding.circleClean.translationX = 0f
                }
            }).start()

        ViewCompat.animate(binding.circleB1).translationX(-100f).setDuration(50)
            .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View) {
                    binding.circleB1.translationX = 0f
                }
            }).start()

        ViewCompat.animate(binding.circleB2).translationX(-100f).setDuration(50)
            .setListener(object : ViewPropertyAnimatorListenerAdapter() {
                override fun onAnimationEnd(view: View) {
                    binding.circleB2.translationX = 0f
                }
            }).start()
    }
}
