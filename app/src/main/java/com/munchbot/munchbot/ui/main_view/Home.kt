package com.munchbot.munchbot.ui.main_view

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.BottomNavBar
import com.munchbot.munchbot.Utils.StatusBarUtils

class Home : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomBarDemoTheme {
                BottomNavBar()
            }
        }
        StatusBarUtils.setStatusBarColor(this.window, R.color.black)
        hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }
}

val DarkBlue = Color(0xFF0F2032)
val Black = Color(0xFF000000)
val Blue = Color(0xFF0491E9)
val Blue2 = Color(0xFF0099FF)
val White = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    secondary = Blue2,
    background = DarkBlue,
    onBackground = Black,
    surface = White
)

@Composable
fun BottomBarDemoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
