package sc.android.nearcart.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ThemeSelectorDropdown(
    current: ThemeMode,
    onChange: (ThemeMode) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { expanded = true }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        // Selected Icon
        Icon(
            imageVector = getThemeIcon(current),
            contentDescription = "current theme",
            tint = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(6.dp))

        // Dropdown arrow
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Open",
            tint = MaterialTheme.colorScheme.onSurface
        )

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.inverseOnSurface
        ) {
            ThemeMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Icon(
                                imageVector = getThemeIcon(mode),
                                contentDescription = null
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(getThemeLabel(mode))
                        }
                    },
                    onClick = {
                        onChange(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun getThemeIcon(mode: ThemeMode) = when (mode) {
    ThemeMode.LIGHT -> Icons.Default.LightMode
    ThemeMode.DARK -> Icons.Default.DarkMode
    ThemeMode.SYSTEM -> Icons.Default.Settings
}

fun getThemeLabel(mode: ThemeMode) = when (mode) {
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
    ThemeMode.SYSTEM -> "System"
}