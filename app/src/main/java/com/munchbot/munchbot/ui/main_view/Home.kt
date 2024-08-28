package com.munchbot.munchbot.ui.main_view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.munchbot.munchbot.MunchBotActivity
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.BottomNavBar
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.HomeBinding
import com.munchbot.munchbot.ui.adapters.HomeAdapter
import com.munchbot.munchbot.ui.main_view.auth.AuthViewModel
import com.munchbot.munchbot.ui.main_view.auth.Login

class Home : MunchBotActivity() {
    private lateinit var binding: HomeBinding
    lateinit var viewPager: ViewPager
    lateinit var adapter: HomeAdapter
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private val selectedIndexState = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBarColor(this.window, R.color.black)
        SetupUI.setupUI(binding.root)

        viewPager = binding.viewPager
        adapter = HomeAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        binding.actionBarSetting.setOnClickListener {
            Log.d("Home", "clicked setting: ")
            authViewModel.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // No scroll
            }

            override fun onPageSelected(position: Int) {
                selectedIndexState.intValue = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                // No scroll
            }
        })

        binding.composeView.setContent {
            BottomBarDemoTheme {
                BottomNavBar(
                    selectedIndexState = selectedIndexState,
                    onItemSelected = { index ->
                        viewPager.setCurrentItem(index, true)
                    }
                )
            }
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
