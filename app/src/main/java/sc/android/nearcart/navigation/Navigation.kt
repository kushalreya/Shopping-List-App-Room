package sc.android.nearcart.navigation

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import sc.android.nearcart.ui.theme.ThemeMode
import sc.android.nearcart.util.LocationUtil
import sc.android.nearcart.view.AddEditScreen
import sc.android.nearcart.view.HomeScreen
import sc.android.nearcart.view.LocationSelector
import sc.android.nearcart.viewmodel.LocationViewModel
import sc.android.nearcart.viewmodel.ShoppingViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation (
    modifier : Modifier,
    themeMode : ThemeMode,
    isDark : Boolean,
    onThemeChange : (ThemeMode) -> Unit,
    shoppingViewModel: ShoppingViewModel,
    locationViewModel: LocationViewModel,
    navController : NavHostController,
    context : Context,
    locationUtil: LocationUtil
) {

    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route,

        // Default → Fade Through (Material)
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 90
                )
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 90,
                    easing = FastOutSlowInEasing
                )
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = 90
                )
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 90
                )
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 90,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(90)
            )
        }
    ){

        // Home Screen
        composable(route = Screens.HomeScreen.route){
            HomeScreen(
                themeMode = themeMode,
                isDark = isDark,
                onThemeChange = onThemeChange,
                navController = navController,
                shoppingViewModel = shoppingViewModel,
                locationViewModel = locationViewModel,
                locationUtil = locationUtil
            )
        }

        // Add/Edit Screen → Shared Axis (X)
        composable(
            route = Screens.AddEditScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.LongType
                    defaultValue = 0L
                }
            ),

            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(
                        300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(
                        300,
                        easing = LinearOutSlowInEasing
                    )
                ) + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(
                        300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(
                        300,
                        easing = LinearOutSlowInEasing
                    )
                ) + fadeOut(tween(300))
            }

        ){ entry ->

            val id = entry.arguments?.getLong("id") ?: 0L

            AddEditScreen(
                id = id,
                isDark = isDark,
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                locationUtil = locationUtil,
                shoppingViewModel = shoppingViewModel,
                locationViewModel = locationViewModel,
                navController = navController
            )
        }

        // Location Selector → Bottom Sheet Motion
        composable(
            route = Screens.LocationSelector.route,

            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(
                        600,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(
                        300,
                        easing = LinearOutSlowInEasing
                    )
                ) + fadeOut(tween(200))
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(
                        400,
                        easing = FastOutSlowInEasing
                    )
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(
                        300,
                        easing = LinearOutSlowInEasing
                    )
                )
            }

        ){

            val startLocation = locationViewModel.lastSavedLocation.value
                    ?: locationViewModel.location.value

            if (startLocation != null){
                LocationSelector(
                    isDark = isDark,
                    location = startLocation,
                    onLocationSelected = { locationData ->

                        locationViewModel.saveManualLocation(locationData)

                        locationViewModel.fetchAddress(
                            "${locationData.latitude}, ${locationData.longitude}"
                        )

                        navController.navigateUp()
                    },
                    navController = navController,
                    locationViewModel = locationViewModel,
                    locationUtil = locationUtil
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        trackColor = MaterialTheme.colorScheme.primary,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 8.dp
                    )
                }
            }
        }
    }
}