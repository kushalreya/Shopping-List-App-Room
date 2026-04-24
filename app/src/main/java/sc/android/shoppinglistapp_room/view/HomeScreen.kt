package sc.android.shoppinglistapp_room.view

import android.util.Log.i
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import sc.android.shoppinglistapp_room.R
import sc.android.shoppinglistapp_room.model.ShoppingItem
import sc.android.shoppinglistapp_room.navigation.Screens
import sc.android.shoppinglistapp_room.ui.theme.ShoppingListApp_RoomTheme
import sc.android.shoppinglistapp_room.ui.theme.ThemeMode
import sc.android.shoppinglistapp_room.util.LocationUtil
import sc.android.shoppinglistapp_room.viewmodel.LocationViewModel
import sc.android.shoppinglistapp_room.viewmodel.ShoppingViewModel
import sc.android.wishlistapp.ui.theme.GreenPrimaryContainerDark

@Composable
fun HomeScreen(
    themeMode: ThemeMode,
    isDark : Boolean,
    onThemeChange : (ThemeMode) -> Unit,
    navController: NavHostController,
    locationUtil: LocationUtil,
    locationViewModel: LocationViewModel,
    shoppingViewModel: ShoppingViewModel
) {
    val snackBarHostState = remember{ SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var pendingItem by remember { mutableStateOf<ShoppingItem?>(null) }

    val haptic = LocalHapticFeedback.current

    ShoppingListApp_RoomTheme (darkTheme = isDark) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppBar(
                    title = "Shopping List",
                    themeMode = themeMode,
                    onThemeChange = onThemeChange,
                    locationUtil = locationUtil,
                    locationViewModel = locationViewModel,
                    navController = navController
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screens.AddEditScreen.route+"/0L")
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(30.dp)
                        .size(70.dp)
                        .border(1.5.dp, GreenPrimaryContainerDark, CircleShape),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = "shopping item add",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            snackbarHost = {SwipeableSnackBar(snackBarHostState)}
        ) {
            val shoppingList = shoppingViewModel.getAllItems.collectAsState(initial = listOf())

            val scope = rememberCoroutineScope()

            if (shoppingList.value.isEmpty()){
                // Empty State UI
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.empty_shoppinglist),
                            contentDescription = "Empty wishlist",
                            modifier = Modifier.size(470.dp)
                        )

                        Text(
                            text = "No items yet",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.displayLarge
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "Tap + to add one!",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Gray
                        )
                    }
                }
            }
            else {
                LazyColumn(
                    modifier = Modifier.padding(it)
                ) {
                    items(
                        shoppingList.value,
                        key={it.id}
                    ){
                        item->

                        val dismissState= rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                    value ->
                                if (value == SwipeToDismissBoxValue.EndToStart){
                                    pendingItem = item
                                    showDialog = true
                                    false
                                } else {
                                    false
                                }
                            },
                            positionalThreshold = {0.5f}
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {

                                val direction = dismissState.dismissDirection
                                val bgColor =
                                    if (direction == SwipeToDismissBoxValue.StartToEnd)
                                        Color.LightGray.copy(0.5f)
                                    else Color(0xFFFF5050)

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(bgColor),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.DeleteSweep,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .padding(end = 16.dp)
                                            .size(35.dp)
                                    )
                                }
                            },
                            enableDismissFromEndToStart = true,
                            enableDismissFromStartToEnd = true
                        ) {

                            ShoppingItemView(
                                item=item,
                                isDark=isDark,
                                onClick = {
                                    navController.navigate(route = Screens.AddEditScreen.route+"/${item.id}")
                                }
                            )
                        }
                        var hapticTriggered by remember { mutableStateOf(false) }

                        LaunchedEffect(dismissState.progress) {
                            if (dismissState.progress >= 0.5f && !hapticTriggered) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                hapticTriggered = true
                            }

                            if (dismissState.progress < 0.5f) {
                                hapticTriggered = false
                            }
                        }
                        if (showDialog && pendingItem != null) {
                            AlertDialog(
                                onDismissRequest = {
                                    showDialog = false
                                    pendingItem = null
                                },
                                title = {
                                    Text("Delete Item")
                                },
                                text = {
                                    Text("Are you sure you want to delete \"${pendingItem?.name}\"?")
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            shoppingViewModel.deleteItem(pendingItem!!)
                                            scope.launch {
                                                snackBarHostState.showSnackbar("${pendingItem?.name} deleted")
                                            }
                                            showDialog = false
                                            pendingItem = null
                                        }

                                    ) {
                                        Text("Delete", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            showDialog = false
                                            pendingItem = null
                                        }
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }

                        LaunchedEffect(showDialog) {
                            if (!showDialog && dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                                dismissState.reset()
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ShoppingItemView (
    item : ShoppingItem,
    isDark: Boolean,
    onClick : () -> Unit
){

    val haptic = LocalHapticFeedback.current

    var checked by remember { mutableStateOf(false) }

    val containerColor = if (checked) {
        if (isDark) Color.Gray else Color.LightGray
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val textColor = if (checked) {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    val strikeColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clickable { onClick() }
            .padding(top = 12.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = textColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it

                    haptic.performHapticFeedback(
                        HapticFeedbackType.TextHandleMove
                    )
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.background
                )
            )

            Row(
                modifier = Modifier
                    .drawWithContent {
                        drawContent()
                        if (checked) {
                            val strokeWidth = 2.dp.toPx()
                            drawLine(
                                color = strikeColor,
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width, size.height / 2),
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ){
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Name:",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.name
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Quantity:",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.quantity.toString()
                    )
                    Text(
                        text = item.unit,
                        style=MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

        }
    }

}