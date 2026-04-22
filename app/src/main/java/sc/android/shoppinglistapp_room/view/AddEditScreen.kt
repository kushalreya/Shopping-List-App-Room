package sc.android.shoppinglistapp_room.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onUnitSelect : () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var menuExpanded by remember { mutableStateOf(false) }

    val snackBarHostState = remember{ SnackbarHostState() }

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
                    onThemeChange = onThemeChange
                )
            },
            snackbarHost = {SwipeableSnackBar(snackBarHostState)}
            ) {
            //parent column
            Column(
                modifier = Modifier
                    .clickable{focusManager.clearFocus()}
                    .padding(it)
                    .padding(top = 32.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //item name text field
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

                //item quantity and unit row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Box {

                        //item quantity
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                "Item Quantity",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            //icon buttons and quantity text field
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                //decrease button
                                IconButton(
                                    onClick = { onDecrease() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RemoveCircleOutline,
                                        contentDescription = "decrease"
                                    )
                                }

                                //quantity text field
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

                                //increase button
                                IconButton(
                                    onClick = { onIncrease() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircleOutline,
                                        contentDescription = "decrease"
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    //item unit
                    Box {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Item Unit",
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = { menuExpanded = true },
                                modifier = Modifier.width(120.dp).height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Text(
                                    //TODO add unit if selected
                                    text = "select",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )

                                Icon(
                                    imageVector = if (menuExpanded) Icons.Default.ArrowDropUp
                                    else Icons.Default.ArrowDropDown,
                                    contentDescription = "unit",
                                    Modifier.size(35.dp)
                                )
                            }

                            //dropdown
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false },
                                shape = RoundedCornerShape(12.dp),
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                DropdownMenuItem(
                                    text = { Text("kgs") },
                                    onClick = {
                                        onUnitSelect()
                                        menuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("lts") },
                                    onClick = {
                                        onUnitSelect()
                                        menuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("pieces") },
                                    onClick = {
                                        onUnitSelect()
                                        menuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("packets") },
                                    onClick = {
                                        onUnitSelect()
                                        menuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                //add item button
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(50.dp),
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
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