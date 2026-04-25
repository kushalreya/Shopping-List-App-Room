package sc.android.nearcart.view

import sc.android.nearcart.R
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import sc.android.nearcart.model.LocationData
import sc.android.nearcart.util.LocationUtil
import sc.android.nearcart.util.distanceBetweenPlaces
import sc.android.nearcart.viewmodel.LocationViewModel
import androidx.core.net.toUri

@Composable
fun LocationSelector(
    isDark: Boolean,
    location: LocationData,
    onLocationSelected: (LocationData) -> Unit,
    navController: NavHostController,
    locationViewModel: LocationViewModel,
    locationUtil: LocationUtil
) {

    val context = LocalContext.current
    val activity = context as? Activity

    var pressedBackOnce by remember { mutableStateOf(false) }
    val hasLocation = locationViewModel.lastSavedLocation.value != null

    // Back handling
    BackHandler {
        when {
            hasLocation -> navController.navigateUp()
            pressedBackOnce -> activity?.finish()
            else -> {
                pressedBackOnce = true
                Toast.makeText(
                    context,
                    "Press back again to exit or select a location",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Location Permission check
    val hasLocationPermission = remember {
        mutableStateOf(locationUtil.checkPermission(context))
    }

    //  User location state
    val userLocation = remember {
        mutableStateOf(
            LatLng(location.latitude, location.longitude)
        )
    }

    //how the map shows
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value, 16.9f)
    }

    val mapProperties = MapProperties(
        isBuildingEnabled = true,
        isTrafficEnabled = true,
        isMyLocationEnabled = hasLocationPermission.value,
        mapStyleOptions = if (isDark) {
            try {
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style_dark
                )
            } catch (e: Exception) {
                null
            }
        } else null
    )

    val uiSettings = MapUiSettings(
        myLocationButtonEnabled = hasLocationPermission.value
    )

    val places by locationViewModel.places

    // Find nearest place
    val nearestPlace by remember(places, userLocation.value) {
        derivedStateOf {
            places.minByOrNull { place ->
                val latLng = LatLng(
                    place.geometry.location.lat,
                    place.geometry.location.lng
                )
                distanceBetweenPlaces(userLocation.value, latLng)
            }
        }
    }

    // Prevent duplicate API calls
    var lastFetchedLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(userLocation.value.latitude, userLocation.value.longitude) {

        val last = lastFetchedLocation

        val shouldFetch =
            last == null ||
                    last.latitude != userLocation.value.latitude ||
                    last.longitude != userLocation.value.longitude

        if (shouldFetch) {
            lastFetchedLocation = userLocation.value

            locationViewModel.fetchNearbyPlaces(
                LocationData(
                    userLocation.value.latitude,
                    userLocation.value.longitude
                )
            )
        }
    }

    val userMarkerState = rememberMarkerState(position = userLocation.value)

    LaunchedEffect(userLocation.value) {
        userMarkerState.position = userLocation.value
    }

    // ================= UI =================

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        // Title
        Box(
            modifier = Modifier
                .weight(0.2f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select location",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(8.dp))

        // Map Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {

            Column(modifier = Modifier.fillMaxSize()) {

                // Map
                Box(modifier = Modifier.weight(1f)) {

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        properties = mapProperties,
                        uiSettings = uiSettings,
                        cameraPositionState = cameraPositionState,
                        onMapClick = { userLocation.value = it }
                    ) {

                        // User marker (blue)
                        Marker(
                            state = userMarkerState,
                            draggable = true,
                            flat = true,
                            anchor = Offset(0.5f, 1.1f),
                            title = "You are here",
                            icon = BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_AZURE
                            )
                        )

                        // Places markers (red)
                        places.forEach { place ->

                            val latLng = LatLng(
                                place.geometry.location.lat,
                                place.geometry.location.lng
                            )

                            val markerState = remember { MarkerState(position = latLng) }
                            markerState.position = latLng

                            Marker(
                                state = markerState,
                                title = place.name,
                                icon = BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_RED
                                )
                            )

                        }
                    }
                }

                // Nearest shop
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    nearestPlace?.let { place ->

                        val placeLatLng = LatLng(
                            place.geometry.location.lat,
                            place.geometry.location.lng
                        )

                        val distance = distanceBetweenPlaces(
                            userLocation.value,
                            placeLatLng
                        )

                        val distanceText =
                            if (distance < 1000)
                                "${distance.toInt()} m away"
                            else
                                "${"%.2f".format(distance / 1000)} km away"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(
                                0.dp, 0.dp,
                                16.dp, 16.dp
                            ),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // LEFT SIDE
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = "Nearest Shop",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    Text(
                                        text = place.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = distanceText,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                // RIGHT SIDE
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    val isOpen = place.opening_hours?.open_now

                                    val statusText = when (isOpen) {
                                        true -> "Open"
                                        false -> "Closed"
                                        else -> "Unknown"
                                    }

                                    val statusColor = when (isOpen) {
                                        true -> MaterialTheme.colorScheme.primary
                                        false -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = statusColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = statusText,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            color = statusColor,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    IconButton(
                                        onClick = {
                                            val origin = userLocation.value
                                            val destination = place.geometry.location

                                            val uri = ("https://www.google.com/maps/dir/?api=1" +
                                                    "&origin=${origin.latitude},${origin.longitude}" +
                                                    "&destination=${destination.lat},${destination.lng}" +
                                                    "&travelmode=driving")
                                                .toUri()

                                            try {
                                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "No maps app found", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Navigation,
                                            contentDescription = "Navigate",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    } ?: Text("No nearby shops found")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Update button
        Button(
            modifier = Modifier
                .wrapContentHeight()
                .padding(bottom = 32.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                onLocationSelected(
                    LocationData(
                        latitude = userLocation.value.latitude,
                        longitude = userLocation.value.longitude
                    )
                )
            },
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "Update Location",
                fontWeight = FontWeight.Bold
            )
        }
    }
}