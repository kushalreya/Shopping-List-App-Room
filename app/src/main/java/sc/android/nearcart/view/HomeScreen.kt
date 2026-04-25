package sc.android.nearcart.view

import sc.android.nearcart.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import sc.android.nearcart.model.ShoppingItem
import sc.android.nearcart.navigation.Screens
import sc.android.nearcart.ui.theme.ShoppingListApp_RoomTheme
import sc.android.nearcart.ui.theme.ThemeMode
import sc.android.nearcart.util.LocationUtil
import sc.android.nearcart.viewmodel.LocationViewModel
import sc.android.nearcart.viewmodel.ShoppingViewModel
import sc.android.wishlistapp.ui.theme.GreenPrimaryContainerDark

@Composable
fun HomeScreen(
    themeMode: ThemeMode,
    isDark : Boolean,
    onThemeChange : (ThemeMode) -> Unit,
    navController: NavHostController,
    shoppingViewModel: ShoppingViewModel,
    locationViewModel: LocationViewModel,
    locationUtil: LocationUtil
) {

    val snackBarHostState = remember { SnackbarHostState() }

    var showDialog by remember { mutableStateOf(false) }
    var pendingItem by remember { mutableStateOf<ShoppingItem?>(null) }

    var activeDismissState by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

    val haptic = LocalHapticFeedback.current

    ShoppingListApp_RoomTheme (darkTheme = isDark) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppBar(
                    title = "NearCart",
                    themeMode = themeMode,
                    onThemeChange = onThemeChange,
                    locationViewModel = locationViewModel,
                    locationUtil = locationUtil,
                    navController = navController
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(Screens.AddEditScreen.route + "/0") },
                    modifier = Modifier
                        .padding(30.dp)
                        .size(70.dp)
                        .border(
                            width = 1.dp,
                            color = GreenPrimaryContainerDark,
                            CircleShape
                        ),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "add item",
                        modifier = Modifier.size(35.dp)
                    )
                }
            },
            snackbarHost = { SwipeableSnackBar(snackBarHostState) }
        ) {

            val shoppingList = shoppingViewModel.getAllItems.collectAsState(initial = listOf()) //initially empty list

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
                            contentDescription = "Empty shopping cart",
                            modifier = Modifier.size(450.dp)
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
            } else {

                //shopping list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    items(
                        shoppingList.value,
                        key = { it.id }
                    ){
                            item ->

                        val currentDismissState = remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {

                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                    pendingItem = item
                                    showDialog = true

                                    activeDismissState = currentDismissState.value

                                    false
                                } else false
                            },
                            positionalThreshold = { 0.5f }
                        )

                        // assign after creation
                        currentDismissState.value = dismissState

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
                                        .padding(horizontal = 10.dp)
                                        .padding(top = 10.dp)
                                        .clip(RoundedCornerShape(16.dp))
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
                        ){
                            ShoppingItemView(
                                item = item,
                                isDark = isDark,
                                onClick = {
                                    navController.navigate(route = Screens.AddEditScreen.route+"/${item.id}")
                                }
                            )
                        }

                        var hapticTriggered by remember { mutableStateOf(false) }

                        LaunchedEffect(dismissState.progress) {
                            if (dismissState.progress >= 0.3f && !hapticTriggered) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                hapticTriggered = true
                            }

                            if (dismissState.progress < 0.3f) {
                                hapticTriggered = false
                            }
                        }
                    }
                }

                if (showDialog && pendingItem != null) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialog = false
                            pendingItem = null
                        },
                        title = {
                            Text(
                                "Delete Item",
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        text = {
                            Text("Are you sure you want to delete \"${pendingItem?.name}\"?")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    val deletedName = pendingItem?.name ?: ""
                                    shoppingViewModel.deleteItem(pendingItem!!)
                                    scope.launch {
                                        snackBarHostState.showSnackbar("$deletedName deleted")
                                    }
                                    showDialog = false
                                    pendingItem = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Delete", fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    showDialog = false
                                    pendingItem = null
                                },
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    "Cancel", fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    )
                }

                LaunchedEffect(showDialog) {
                    if (!showDialog) {
                        activeDismissState?.reset()
                        activeDismissState = null
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
            .padding(horizontal = 10.dp)
            .clickable { onClick() }
            .padding(top = 10.dp),
        shape = RoundedCornerShape(16.dp),
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
                        HapticFeedbackType.ToggleOn
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant                    )
                }
            }
        }
    }
}