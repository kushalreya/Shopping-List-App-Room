@file:OptIn(ExperimentalMaterial3Api::class)

package sc.android.shoppinglistapp_room.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import sc.android.shoppinglistapp_room.navigation.Screens
import sc.android.shoppinglistapp_room.ui.theme.ThemeMode
import sc.android.shoppinglistapp_room.ui.theme.ThemeSelectorDropdown
import sc.android.shoppinglistapp_room.util.LocationUtil
import sc.android.shoppinglistapp_room.viewmodel.LocationViewModel

@Composable
fun AppBar (
    title : String,
    onBackNavClicked : () -> Unit = {},
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    locationUtil: LocationUtil,
    locationViewModel: LocationViewModel,
    navController: NavHostController
) {

    //back icon visible if NOT in home screen
    val navigationIcon : (@Composable () -> Unit) = {
        if (!title.contains("Shopping List")){
            IconButton(
                onClick = { onBackNavClicked() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    //location and theme selector dropdown visible ONLY in home screen
    val actionIcons : (@Composable (RowScope.() -> Unit)) = {
        if (title.contains("Shopping List")){
            Row(
                modifier = Modifier
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //map icon
                IconButton(
                    onClick = {
                        locationUtil.requestLocationUpdates(locationViewModel)
                        navController.navigate(Screens.LocationSelector.route){
                            this.launchSingleTop
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "location",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }

                ThemeSelectorDropdown(
                    current = themeMode,
                    onChange = onThemeChange
                )
            }
        }
    }

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        title = {
            Text(
                text = title,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(30.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 35.sp
            )
        },
        windowInsets = TopAppBarDefaults.windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        //on left
        navigationIcon = navigationIcon,
        //on right
        actions = actionIcons
    )

}