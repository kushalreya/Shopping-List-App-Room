@file:OptIn(ExperimentalMaterial3Api::class)

package sc.android.shoppinglistapp_room.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    locationViewModel: LocationViewModel,
    locationUtil: LocationUtil,
    navController: NavHostController
) {
    //home screen title check
    val isHomeScreen = title == "Shopping List"

    //back icon visible if NOT in home screen
    val navigationIcon: (@Composable () -> Unit) = {
        if (!isHomeScreen) {
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
    val actionIcons: (@Composable (RowScope.() -> Unit)) = {
        if (isHomeScreen) {
            Box(
                modifier = Modifier.padding(end = 12.dp)
            ) {
                ThemeSelectorDropdown(
                    current = themeMode,
                    onChange = onThemeChange
                )
            }
        }
    }

    //formatted address from lat-long
    val address = locationViewModel.address.value
        .firstOrNull()
        ?.formatted_address
        .orEmpty()


    Column {

        TopAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            title = {
                // Title Row
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 30.sp
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

        // Address Row
        if (isHomeScreen && address.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary)
                    .clickable {
                        navController.navigate(Screens.LocationSelector.route) {
                            launchSingleTop = true
                        }
                    }
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "location",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = address,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f)
                )

            }
        }
    }
}