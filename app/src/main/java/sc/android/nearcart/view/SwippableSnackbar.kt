package sc.android.nearcart.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import sc.android.wishlistapp.ui.theme.GreenPrimaryContainerDark

@Composable
fun SwipeableSnackBar(
    snackBarHostState: SnackbarHostState
){

    val haptic = LocalHapticFeedback.current

    SnackbarHost(
        hostState = snackBarHostState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp),
        snackbar = {
                snackBarData ->

            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                        value ->
                    if (value != SwipeToDismissBoxValue.Settled){
                        haptic.performHapticFeedback(
                            HapticFeedbackType.LongPress
                        )
                        snackBarData.dismiss()
                        true
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .background(
                                Color.Green.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(16.dp)
                            )

                    )
                },
                content = {
                    Snackbar(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .border(
                                width = 1.dp,
                                color = GreenPrimaryContainerDark,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            text = snackBarData.visuals.message,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            )
        }
    )
}