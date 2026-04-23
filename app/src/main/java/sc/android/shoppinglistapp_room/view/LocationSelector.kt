package sc.android.shoppinglistapp_room.view

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import sc.android.shoppinglistapp_room.R
import sc.android.shoppinglistapp_room.model.LocationData
import sc.android.shoppinglistapp_room.viewmodel.LocationViewModel

@Composable
fun LocationSelector(
    isDark: Boolean,
    location: LocationData,
    onLocationSelected : (LocationData) -> Unit,
    navController: NavHostController,
    locationViewModel: LocationViewModel
) {

    val context = LocalContext.current
    val activity = context as? Activity
    var pressedBackOnce by remember { mutableStateOf(false) }
    val hasLocation = locationViewModel.lastSavedLocation.value != null

    //to handle the back button
    BackHandler {
        if (hasLocation){
            navController.navigateUp()
        } else if (pressedBackOnce) {
            activity?.finish()
        } else {
            pressedBackOnce = true
            Toast.makeText(
                context,
                "Press back again to exit or select a location",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    //live location
    val userLocation = remember {
        mutableStateOf(LatLng(
            location.latitude,
            location.longitude
        ))
    }

    //view of map when location selector opens
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation.value,
            16.9f
        )
    }

    val mapProperties by remember {
        (mutableStateOf(
            MapProperties(
                isBuildingEnabled = true,
                isTrafficEnabled = true,
                isMyLocationEnabled = true,     //for the current location blue dot
                mapStyleOptions = if (isDark) {
                    MapStyleOptions.loadRawResourceStyle(
                        context,
                        R.raw.map_style_dark    //dark themed map
                    )
                } else null     //normal light-themed map
            )
        ))
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(myLocationButtonEnabled = true    //for the target button for moving to current location
            ))
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Select location",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            border = BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {

            GoogleMap(
                properties = mapProperties,
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState,
                onMapClick = { userLocation.value = it }
            ){
                Marker(
                    state = MarkerState(position = userLocation.value),
                    draggable = true,
                    flat = true,
                    anchor = Offset(0.5f, 1.1f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        var newLocation : LocationData

        Button(
            modifier = Modifier
                .wrapContentHeight()
                .padding(bottom = 32.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                newLocation = LocationData(
                    latitude = userLocation.value.latitude,
                    longitude = userLocation.value.longitude
                )
                onLocationSelected(newLocation)
            },
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            )
        ) {
            Text(
                text = "Update Location",
                fontWeight = FontWeight.Bold
            )
        }
    }
}