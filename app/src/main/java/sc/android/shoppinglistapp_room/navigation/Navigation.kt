package sc.android.shoppinglistapp_room.navigation

import android.content.Context
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import sc.android.shoppinglistapp_room.ui.theme.ThemeMode
import sc.android.shoppinglistapp_room.util.LocationUtil
import sc.android.shoppinglistapp_room.view.AddEditScreen
import sc.android.shoppinglistapp_room.view.HomeScreen
import sc.android.shoppinglistapp_room.view.LocationSelector
import sc.android.shoppinglistapp_room.viewmodel.LocationViewModel

@Composable
fun Navigation (
    modifier : Modifier,
    themeMode : ThemeMode,
    isDark : Boolean,
    onThemeChange : (ThemeMode) -> Unit,
    locationViewModel: LocationViewModel,
    navController : NavHostController,
    context : Context,
    locationUtil: LocationUtil
) {
    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen.route
    ){

        //home screen
        composable( route = Screens.HomeScreen.route ){
            HomeScreen(
                themeMode = themeMode,
                isDark = isDark,
                onThemeChange = onThemeChange,
                navController = navController,
                locationViewModel = locationViewModel,
                locationUtil = locationUtil
            )
        }

        //add-edit screen
        composable( route = Screens.AddEditScreen.route ){
            AddEditScreen(
                id = 0L,
                isDark = isDark,
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                locationUtil = locationUtil,
                locationViewModel = locationViewModel,
                navController = navController,
                onValueChange = {},
                onDecrease = {},
                onIncrease = {},
                onUnitSelect = {}
            )
        }

        //location selection dialog
        composable( route = Screens.LocationSelector.route ){

            //starts with the last selected location, if not available, starts with the current live location
            val startLocation = locationViewModel.lastSavedLocation.value
                    ?: locationViewModel.location.value

            if (startLocation != null){
                LocationSelector(
                    isDark = isDark,
                    location = startLocation,
                    onLocationSelected = {
                            locationData ->

                        //saving the current location
                        locationViewModel.saveManualLocation(locationData)

                        //fetching the formatted address for the current location
                        locationViewModel.fetchAddress("${locationData.latitude}, ${locationData.longitude}")

                        //goes back to the home screen
                        navController.navigateUp()
                    },
                    navController = navController,
                    locationViewModel = locationViewModel
                )
            } else {
                //map loading
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
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