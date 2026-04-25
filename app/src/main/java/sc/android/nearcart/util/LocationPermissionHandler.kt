package sc.android.shoppinglistapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import sc.android.nearcart.navigation.Screens
import sc.android.nearcart.util.LocationUtil
import sc.android.nearcart.viewmodel.LocationViewModel

@Composable
fun LocationPermissionHandler(
    locationViewModel: LocationViewModel,
    locationUtil: LocationUtil,
    navController: NavController,
    content: @Composable () -> Unit
) {

    val context = LocalContext.current
    val activity = context as Activity

    var permissionGranted by remember {
        mutableStateOf(locationUtil.checkPermission(context))
    }
    var showRationale by remember { mutableStateOf(false) }
    var permissionDenialCount by remember { mutableStateOf(0) }

    var openedSettings by remember { mutableStateOf(false) }


    //permission launcher for requesting permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        //checks if all the asked permissions are granted (all are true)
        val granted = permissions.values.all { it }

        if (granted) {
            permissionGranted = true
        } else {
            permissionDenialCount++

            if (permissionDenialCount == 1) {
                //if user denies only once, the rationale is shown
                showRationale = true
            } else {
                //if the user denies twice or permanently, the app exits
                activity.finish()
            }
        }
    }

    // Only run once
    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            //launches the permission launcher
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {

                val hasPermission = locationUtil.checkPermission(context)

                if (hasPermission) {
                    permissionGranted = true
                    showRationale = false
                } else if (openedSettings){
                    // re-request permission
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {

        permissionGranted -> {
            LaunchedEffect(Unit) {
                locationUtil.requestLocationUpdates(locationViewModel)
                if (locationViewModel.addresses.value.isEmpty()) {
                    navController.navigate(Screens.LocationSelector.route)
                }
            }
            content()
        }

        showRationale -> {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Location Permission Required") },
                text = { Text("Please enable location permission from settings") },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Button(
                            onClick = {
                                showRationale = false
                                openedSettings = true

                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )

                                context.startActivity(intent)
                            }
                        ) {
                            Text("Enable")
                        }

                        Button(
                            onClick = {
                                activity.finish()
                            }
                        ) {
                            Text("Exit")
                        }
                    }
                }
            )
        }
    }
}