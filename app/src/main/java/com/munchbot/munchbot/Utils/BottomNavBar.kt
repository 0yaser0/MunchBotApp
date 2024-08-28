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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
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
import com.munchbot.munchbot.ui.fragments.home.Home1Fragment
import com.munchbot.munchbot.ui.fragments.home.PlannerFragment
import com.munchbot.munchbot.ui.fragments.home.VetFragment

@Composable
fun BottomNavBar(
    selectedIndexState: MutableState<Int>,
    onItemSelected: (Int) -> Unit
) {
    val navigationBarItems = remember { NavigationBarItems.entries.toTypedArray() }
    val selectedIndex = selectedIndexState.value

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
                navigationBarItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable {
                                selectedIndexState.value = item.item.index // Update state
                                onItemSelected(item.item.index) // Trigger callback to update ViewPager
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(35.dp),
                                painter = painterResource(id = item.item.iconResId),
                                contentDescription = item.item.description,
                                tint = if (selectedIndex == item.item.index) MaterialTheme.colorScheme.surface
                                else MaterialTheme.colorScheme.inverseSurface
                            )
                            if (selectedIndex == item.item.index) {
                                Text(
                                    text = item.item.label,
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

enum class NavigationBarItems(val item: NavigationItem) {
    HOME(NavigationItem(0, "Home", R.drawable.ic_nav_home, "Home")),
    PLANNER(NavigationItem(1, "Planner", R.drawable.ic_nav_planner, "Planner")),
    VET(NavigationItem(2, "Vet", R.drawable.ic_nav_vet, "Vet")),
    HEALTH(NavigationItem(3, "Health", R.drawable.ic_nav_health, "Health"))
}


data class NavigationItem(
    val index: Int,
    val label: String,
    val iconResId: Int,
    val description: String
)
