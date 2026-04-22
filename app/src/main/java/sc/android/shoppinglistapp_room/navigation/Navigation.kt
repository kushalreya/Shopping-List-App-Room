@file:OptIn(ExperimentalAnimationApi::class)

package sc.android.shoppinglistapp_room.navigation

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import sc.android.shoppinglistapp_room.model.ShoppingItem
import sc.android.shoppinglistapp_room.ui.theme.ThemeMode
import sc.android.shoppinglistapp_room.view.AddEditScreen
import sc.android.shoppinglistapp_room.view.HomeScreen
import sc.android.shoppinglistapp_room.view.ShoppingItemView

@Composable
fun Navigation (
    modifier : Modifier,
    themeMode : ThemeMode,
    isDark : Boolean,
    onThemeChange : (ThemeMode) -> Unit,
    navController : NavHostController,
    context : Context
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screens.AddEditScreen.route
    ){

        //home screen
        composable(
            route = Screens.HomeScreen.route,
            enterTransition = { fadeIn( tween(300) ) },
            popEnterTransition = { fadeIn( tween(300) ) },
            exitTransition = { fadeOut( tween(300) ) },
            popExitTransition = { fadeOut( tween(300) ) }
        ){
            HomeScreen(
                themeMode = themeMode,
                isDark = isDark,
                onThemeChange = onThemeChange,
                navController = navController
            )
        }

        //add-edit screen
        composable(
            route= Screens.AddEditScreen.route,
            enterTransition = { fadeIn( tween(300) ) },
            popEnterTransition = { fadeIn( tween(300) ) },
            exitTransition = { fadeOut( tween(300) ) },
            popExitTransition = { fadeOut( tween(300) ) }
        ){
            AddEditScreen(
                id=0L,
                isDark=isDark,
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                navController = navController,
                onValueChange={},
                onDecrease = {},
                onIncrease = {},
                onUnitSelect = {}
            )
        }

        //location selection dialog
    }
}