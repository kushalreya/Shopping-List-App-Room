package sc.android.shoppinglistapp_room.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import sc.android.shoppinglistapp_room.model.LocationData

@Composable
fun LocationSelector(
    location: LocationData,
    onLocationSelected : (LocationData) -> Unit
) {

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
            17f
        )
    }

    val mapProperties by remember {
        (mutableStateOf(
            MapProperties(isMyLocationEnabled = true)   //for the current location blue dot
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

        Card(
            modifier = Modifier
                .height(700.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            border = BorderStroke(
                width = 3.dp,
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
                    flat = true
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        var newLocation : LocationData

        Button(
            modifier = Modifier
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