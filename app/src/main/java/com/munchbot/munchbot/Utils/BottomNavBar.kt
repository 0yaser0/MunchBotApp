package com.munchbot.munchbot.Utils

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import com.munchbot.munchbot.R
import com.munchbot.munchbot.ui.fragments.home.HealthFragment
import com.munchbot.munchbot.ui.fragments.home.PlannerFragment
import com.munchbot.munchbot.ui.fragments.home.VetFragment
import com.munchbot.munchbot.ui.fragments.home.home.Home1Fragment

@Composable
fun BottomNavBar() {
    val navigationBarItems = remember { NavigationBarItems.entries.toTypedArray() }
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            AnimatedNavigationBar(
                modifier = Modifier.height(90.dp),
                selectedIndex = selectedIndex,
                cornerRadius = shapeCornerRadius(30.dp, 30.dp, 0.dp, 0.dp),
                ballAnimation = Parabolic(tween(500)),
                indentAnimation = Height(tween(500)),
                barColor = MaterialTheme.colorScheme.onBackground,
                ballColor = MaterialTheme.colorScheme.onBackground
            ) {
                navigationBarItems.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { selectedIndex = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(35.dp),
                                painter = painterResource(id = item.iconResId),
                                contentDescription = item.description,
                                tint = if (selectedIndex == index) MaterialTheme.colorScheme.surface
                                else MaterialTheme.colorScheme.inverseSurface
                            )
                            if (selectedIndex == index) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                NavigationBarItems.HOME.ordinal -> Home1Fragment()
                NavigationBarItems.PLANNER.ordinal -> PlannerFragment()
                NavigationBarItems.VET.ordinal -> VetFragment()
                NavigationBarItems.HEALTH.ordinal -> HealthFragment()
            }
        }
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.then(
        clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onClick()
        }
    )
}
enum class NavigationBarItems(val label: String, val iconResId: Int, val description: String) {
    HOME("Home", R.drawable.ic_nav_home, "Home"),
    PLANNER("planner" ,R.drawable.ic_nav_planner, "Planner"),
    VET("Vet" ,R.drawable.ic_nav_vet, "Vet"),
    HEALTH("Health",R.drawable.ic_nav_health, "Health")
}

