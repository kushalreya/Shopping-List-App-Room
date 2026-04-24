package sc.android.shoppinglistapp_room.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import sc.android.shoppinglistapp_room.R
import sc.android.shoppinglistapp_room.ui.theme.ShoppingListApp_RoomTheme
import sc.android.shoppinglistapp_room.ui.theme.ThemeMode
import sc.android.shoppinglistapp_room.util.LocationUtil
import sc.android.shoppinglistapp_room.viewmodel.LocationViewModel
import sc.android.shoppinglistapp_room.viewmodel.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    id: Long,
    isDark : Boolean,
    themeMode: ThemeMode,
    onThemeChange : (ThemeMode) -> Unit,
    navController: NavHostController,
    onValueChange : () -> Unit,
    onDecrease : () -> Unit,
    onIncrease : () -> Unit,
    onUnitSelect : () -> Unit,
    locationUtil: LocationUtil,
    locationViewModel: LocationViewModel,
    shoppingViewModel: ShoppingViewModel
) {

    val snackBarHostState = remember { SnackbarHostState() }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var menuExpanded by remember { mutableStateOf(false) }

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
                    locationUtil = locationUtil,
                    locationViewModel = locationViewModel,
                    navController = navController,
                    onBackNavClicked = {navController.navigateUp()}
                )
            },
            snackbarHost = { SwipeableSnackBar(snackBarHostState) }
        ) {
            //parent column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(top = 32.dp)
                    .clickable{ focusManager.clearFocus() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //name field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    //TODO add name
                    value = "",
                    onValueChange = { onValueChange() },
                    label = {
                        Text(
                            "Item Name",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
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
                        .padding(horizontal = 32.dp),
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
                                    onClick = { onDecrease() }
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
                                        .width(55.dp),
                                    //TODO add quantity.toString()
                                    value = "",
                                    onValueChange = { onValueChange() },
                                    shape = RoundedCornerShape(16.dp),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    )
                                )

                                //increase icon button
                                IconButton(
                                    onClick = { onIncrease() }
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
                                    value = "select",
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
                                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
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
                                                Text(
                                                    text = unit,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            },

                                            onClick = {
                                                //TODO set unit
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
                        //TODO set snackbar, add or update item

                        focusManager.clearFocus()   //clears focus from textbox
                        keyboardController?.hide()  //hides keyboard
                    },
                    //TODO enable if name, quantity and unit is not empty
                    enabled = true,
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