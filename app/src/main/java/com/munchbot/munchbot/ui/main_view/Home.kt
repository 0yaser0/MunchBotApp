package com.munchbot.munchbot.ui.main_view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.munchbot.munchbot.MunchBotActivity
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.BottomNavBar
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.database.getPetId
import com.munchbot.munchbot.data.database.getUserId
import com.munchbot.munchbot.data.viewmodel.PetViewModel
import com.munchbot.munchbot.data.viewmodel.UserViewModel
import com.munchbot.munchbot.databinding.HomeBinding
import com.munchbot.munchbot.ui.adapters.HomeAdapter
import com.munchbot.munchbot.ui.main_view.auth.AuthViewModel
import com.munchbot.munchbot.ui.main_view.auth.Login

class Home : MunchBotActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: HomeBinding
    lateinit var viewPager: ViewPager
    lateinit var adapter: HomeAdapter
    private val userViewModel: UserViewModel by viewModels()
    private val petViewModel: PetViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
    private lateinit var drawerLayout: DrawerLayout

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

        setupSideBar()

        var isOpen = 0

        binding.actionBarSetting.setOnClickListener {
            if (isOpen == 0) {
                drawerLayout.openDrawer(GravityCompat.START)
                isOpen = 1
            } else {
                drawerLayout.closeDrawer(GravityCompat.START)
                isOpen = 0
            }
        }

        binding.actionBarProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
        }

        setupGetter()

        userViewModel.userLiveData.observe(this) { user ->
            user?.let {
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                val email = currentUser?.email

                val nameOfUser = findViewById<TextView>(R.id.nameOfUser)
                val emailOfUser = findViewById<TextView>(R.id.emailOfUser)
                val userProfileMenu = findViewById<ImageView>(R.id.UserProfilMenu)
                val userImageView = binding.actionBarProfile

                nameOfUser?.text = getString(R.string.name_of_user, it.username)
                emailOfUser?.text = getString(R.string.email_user, email ?: "No Email")

                if (it.userProfileImage.isNotEmpty()) {
                    Log.d("Home1Fragment", "Loading image URL: ${it.userProfileImage}")

                    Glide.with(this)
                        .load(it.userProfileImage)
                        .override(200, 200)
                        .error(R.drawable.ic_error)
                        .into(userImageView)

                    Glide.with(this)
                        .load(it.userProfileImage)
                        .error(R.drawable.ic_error)
                        .into(userProfileMenu)
                }
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> adapter.navigateToFragment(viewPager, 0)
            R.id.nav_settings -> Toast.makeText(this, "Settings Coming Soon!", Toast.LENGTH_SHORT)
                .show()

            R.id.nav_about -> Toast.makeText(this, "About Us Coming Soon!", Toast.LENGTH_SHORT)
                .show()

            R.id.nav_logout -> {
                authViewModel.signOut()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()

            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("This method has been deprecated in favor of using the OnBackPressedDispatcher.")
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSideBar() {
        drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    private fun setupGetter() {
        val userId = getUserId()
        Log.d(TAG, "User ID $userId")

        if (userId != null) {
            val petId = getPetId(userId)
            Log.d(TAG, "Pet ID $petId")
            userViewModel.loadUser(userId)
            petViewModel.loadPet(userId, petId)
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

@GlideModule
class MyAppGlideModule : AppGlideModule() {
}
