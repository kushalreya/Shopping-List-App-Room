package sc.android.shoppinglistapp_room.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCartCheckout
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import sc.android.shoppinglistapp_room.model.ShoppingItem
import sc.android.shoppinglistapp_room.navigation.Screens
import sc.android.shoppinglistapp_room.ui.theme.ShoppingListApp_RoomTheme
import sc.android.shoppinglistapp_room.ui.theme.ThemeMode
import sc.android.wishlistapp.ui.theme.GreenPrimaryContainerDark

@Composable
fun HomeScreen(
    themeMode: ThemeMode,
    isDark : Boolean,
    onThemeChange : (ThemeMode) -> Unit,
    navController: NavHostController
) {
    val snackBarHostState = remember{ SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ShoppingListApp_RoomTheme (darkTheme = isDark) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppBar(
                    title = "Shopping List",
                    themeMode = themeMode,
                    onThemeChange = onThemeChange
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screens.AddEditScreen.route)
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                items(15){
                    ShoppingItemView(
                        ShoppingItem(
                            0L,
                            "Eggs",
                            "30",
                            unit = "pieces"
                        ),
                        isDark = isDark,
                        {}
                    )
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
                        text = item.quantity
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