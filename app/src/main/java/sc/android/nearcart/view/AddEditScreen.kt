@file:OptIn(ExperimentalMaterial3Api::class)

package sc.android.nearcart.view

import sc.android.nearcart.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sc.android.nearcart.model.ShoppingItem
import sc.android.nearcart.ui.theme.ShoppingListApp_RoomTheme
import sc.android.nearcart.ui.theme.ThemeMode
import sc.android.nearcart.util.LocationUtil
import sc.android.nearcart.viewmodel.LocationViewModel
import sc.android.nearcart.viewmodel.ShoppingViewModel

@Composable
fun AddEditScreen(
    id: Long,
    isDark : Boolean,
    themeMode: ThemeMode,
    onThemeChange : (ThemeMode) -> Unit,
    shoppingViewModel: ShoppingViewModel,
    locationViewModel: LocationViewModel,
    locationUtil: LocationUtil,
    navController: NavHostController
) {

    val snackBarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val haptic = LocalHapticFeedback.current

    var menuExpanded by remember { mutableStateOf(false) }

    var quantityText by remember {
        mutableStateOf(shoppingViewModel.shoppingItemQuantity.toString())
    }

    val validInput by remember {
        derivedStateOf {
            shoppingViewModel.shoppingItemName.isNotEmpty() &&
                    shoppingViewModel.shoppingItemQuantity > 0 &&
                    shoppingViewModel.shoppingItemUnit.isNotEmpty()
        }
    }

    val itemNameLimit = 25

    //configuring what to show once inside add-edit screen
    LaunchedEffect(id) {
        if (id != 0L) {
            val item = shoppingViewModel.getItemById(id).first()

            shoppingViewModel.shoppingItemName = item.name
            shoppingViewModel.shoppingItemQuantity = item.quantity
            shoppingViewModel.shoppingItemUnit = item.unit
            quantityText = item.quantity.toString()
        } else {
            shoppingViewModel.shoppingItemName = ""
            shoppingViewModel.shoppingItemQuantity = 0
            shoppingViewModel.shoppingItemUnit = ""
            quantityText = ""
        }
    }

    ShoppingListApp_RoomTheme(darkTheme = isDark) {

        Scaffold(
            topBar = {
                AppBar(
                    title = if (id != 0L) {
                        stringResource(id = R.string.update_item)
                    } else {
                        stringResource(id = R.string.add_item)
                    },
                    themeMode = themeMode,
                    onThemeChange = onThemeChange,
                    locationViewModel = locationViewModel,
                    locationUtil = locationUtil,
                    navController = navController,
                    onBackNavClicked = { navController.navigateUp() }
                )
            },
            snackbarHost = { SwipeableSnackBar(snackBarHostState) }
        ) {
            //parent column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(top = 33.dp)
                    .clickable { focusManager.clearFocus() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //name field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    value = shoppingViewModel.shoppingItemName,
                    onValueChange = { shoppingViewModel.onNameChange(it) },
                    label = {
                        Text(
                            "Item Name",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    },
                    supportingText = {
                        Text(
                            text = "${shoppingViewModel.shoppingItemName.length}/$itemNameLimit",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                Spacer(Modifier.height(32.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Box {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            //item quantity title
                            Text(
                                "Item Quantity",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                //decrease icon button
                                IconButton(
                                    onClick = {
                                        shoppingViewModel.decrementQuantity()
                                        quantityText = shoppingViewModel.shoppingItemQuantity.toString()

                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RemoveCircle,
                                        contentDescription = "decrease",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                //quantity
                                OutlinedTextField(
                                    modifier = Modifier
                                        .width(70.dp),
                                    value = quantityText,
                                    onValueChange = {
                                            input ->
                                        // allow only digits and max 3 chars
                                        val filtered = input.filter { it.isDigit() }.take(3)
                                        quantityText = filtered
                                        shoppingViewModel.onQuantityChange(filtered)
                                    },
                                    textStyle = LocalTextStyle.current.copy(
                                        textAlign = TextAlign.Center
                                    ),
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    )
                                )

                                //increase icon button
                                IconButton(
                                    onClick = {
                                        shoppingViewModel.incrementQuantity()
                                        quantityText = shoppingViewModel.shoppingItemQuantity.toString()

                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircle,
                                        contentDescription = "decrease",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Box {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            //item unit title
                            Text(
                                "Item Unit",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            val units = listOf("kgs", "gms", "lts", "mls", "pieces", "packets")

                            //dropdown menu
                            ExposedDropdownMenuBox(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(32.dp)),
                                expanded = menuExpanded,
                                onExpandedChange = { menuExpanded = !menuExpanded }
                            ) {
                                TextField(
                                    value = shoppingViewModel.shoppingItemUnit.ifEmpty { "select" },
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .menuAnchor()
                                        .width(140.dp),
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded)
                                    }
                                )

                                ExposedDropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false },
                                    shape = RoundedCornerShape(16.dp),
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    units.forEach { unit ->
                                        DropdownMenuItem(
                                            text = {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(
                                                            if (unit == shoppingViewModel.shoppingItemUnit)
                                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                                            else Color.Transparent
                                                        )
                                                        .padding(vertical = 8.dp, horizontal = 16.dp)
                                                ) {
                                                    Text(
                                                        text = unit,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            },
                                            onClick = {
                                                shoppingViewModel.onUnitSelected(unit)
                                                menuExpanded = false

                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            },
                                            colors = MenuDefaults.itemColors(
                                                textColor = if (unit == shoppingViewModel.shoppingItemUnit)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                //add-update button
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(50.dp),
                    onClick = {

                        val quantity = shoppingViewModel.shoppingItemQuantity

                        if (shoppingViewModel.shoppingItemName.isNotEmpty()
                            && quantity > 0
                            && shoppingViewModel.shoppingItemUnit.isNotEmpty()){

                            if (id != 0L){
                                //update item
                                shoppingViewModel.updateItem(
                                    ShoppingItem(
                                        id = id,
                                        name = shoppingViewModel.shoppingItemName.trim(),
                                        quantity = quantity,
                                        unit = shoppingViewModel.shoppingItemUnit
                                    )
                                )

                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = "\"${shoppingViewModel.shoppingItemName}\" has been updated",
                                        duration = SnackbarDuration.Short
                                    )
                                    navController.navigateUp()
                                }

                            } else {
                                //add item
                                shoppingViewModel.addItem(
                                    ShoppingItem(
                                        name = shoppingViewModel.shoppingItemName.trim(),
                                        quantity = quantity,
                                        unit = shoppingViewModel.shoppingItemUnit
                                    )
                                )

                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = "\"${shoppingViewModel.shoppingItemName}\" has been added to the list",
                                        duration = SnackbarDuration.Short
                                    )
                                    navController.navigateUp()
                                }
                            }
                        } else {
                            haptic.performHapticFeedback(HapticFeedbackType.Reject) // subtle error feedback
                            scope.launch {
                                snackBarHostState.showSnackbar("Please fill all the fields to proceed!")
                            }
                        }

                        focusManager.clearFocus()   //clears focus from textbox
                        keyboardController?.hide()  //hides keyboard
                    },
                    enabled = validInput,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = if (isDark) Color.Gray else Color.LightGray,
                        disabledContentColor = if (isDark) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = if (id != 0L){
                            stringResource(id = R.string.update_item)
                        } else {
                            stringResource(id = R.string.add_item)
                        },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            }
        }
    }
}