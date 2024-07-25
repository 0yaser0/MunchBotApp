package com.munchbot.munchbot

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListenerAdapter
import com.example.utils.StatusBarUtils
import com.munchbot.munchbot.Ui.Auth.Login
import com.munchbot.munchbot.databinding.GetStarted3Binding
import kotlin.math.abs

@Suppress("DEPRECATION")
class GetStarted3 : AppCompatActivity() {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var endX: Float = 0f
    private var endY: Float = 0f

    private lateinit var binding: GetStarted3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GetStarted3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBarColor(window, R.color.status_bar_color)

        binding.arrowBack.setOnClickListener {
            val intent = Intent(this, GetStarted2::class.java)
            startActivity(intent)
            overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right)
        }

        binding.btnGetstarted.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
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
                            val intent = Intent(this@GetStarted3, GetStarted2::class.java)
                            val options = ActivityOptions.makeCustomAnimation(
                                this@GetStarted3,
                                R.animator.slide_in_left,
                                R.animator.slide_out_right
                            )
                            startActivity(intent, options.toBundle())
                        } else if (isSwipeLeft(startX, endX)) {
                            println()
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
        val circleClean = binding.circleClean
        val circleB1 = binding.circleB1
        val circleB2 = binding.circleB2

        ViewCompat.animate(circleClean).translationX(+100f).setDuration(50).setListener(object : ViewPropertyAnimatorListenerAdapter() {
            override fun onAnimationEnd(view: View) {
                circleClean.translationX = 0f
            }
        }).start()

        ViewCompat.animate(circleB1).translationX(+100f).setDuration(50).setListener(object : ViewPropertyAnimatorListenerAdapter() {
            override fun onAnimationEnd(view: View) {
                circleB1.translationX = 0f
            }
        }).start()

        ViewCompat.animate(circleB2).translationX(+100f).setDuration(50).setListener(object : ViewPropertyAnimatorListenerAdapter() {
            override fun onAnimationEnd(view: View) {
                circleB2.translationX = 0f
            }
        }).start()
    }
}
