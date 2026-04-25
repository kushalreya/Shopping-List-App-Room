package sc.android.nearcart.ui.theme

import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import sc.android.wishlistapp.ui.theme.DarkBackground
import sc.android.wishlistapp.ui.theme.DarkOnBackground
import sc.android.wishlistapp.ui.theme.DarkOnSurface
import sc.android.wishlistapp.ui.theme.DarkSurface
import sc.android.wishlistapp.ui.theme.GreenOnPrimary
import sc.android.wishlistapp.ui.theme.GreenOnPrimaryContainer
import sc.android.wishlistapp.ui.theme.GreenOnPrimaryContainerDark
import sc.android.wishlistapp.ui.theme.GreenOnPrimaryDark
import sc.android.wishlistapp.ui.theme.GreenPrimary
import sc.android.wishlistapp.ui.theme.GreenPrimaryContainer
import sc.android.wishlistapp.ui.theme.GreenPrimaryContainerDark
import sc.android.wishlistapp.ui.theme.GreenPrimaryDark
import sc.android.wishlistapp.ui.theme.LightBackground
import sc.android.wishlistapp.ui.theme.LightOnBackground
import sc.android.wishlistapp.ui.theme.LightOnSurface
import sc.android.wishlistapp.ui.theme.LightSurface
import sc.android.wishlistapp.ui.theme.RedAccent
import sc.android.wishlistapp.ui.theme.RedAccentDark
import sc.android.wishlistapp.ui.theme.YellowOnSecondary
import sc.android.wishlistapp.ui.theme.YellowOnSecondaryContainer
import sc.android.wishlistapp.ui.theme.YellowOnSecondaryContainerDark
import sc.android.wishlistapp.ui.theme.YellowOnSecondaryDark
import sc.android.wishlistapp.ui.theme.YellowSecondary
import sc.android.wishlistapp.ui.theme.YellowSecondaryContainer
import sc.android.wishlistapp.ui.theme.YellowSecondaryContainerDark
import sc.android.wishlistapp.ui.theme.YellowSecondaryDark

// --------------------
// LIGHT COLOR SCHEME
// --------------------
private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,

    secondary = YellowSecondary,
    onSecondary = YellowOnSecondary,
    secondaryContainer = YellowSecondaryContainer,
    onSecondaryContainer = YellowOnSecondaryContainer,

    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,

    error = RedAccent
)

// --------------------
// DARK COLOR SCHEME
// --------------------
private val DarkColors = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = GreenOnPrimaryDark,
    primaryContainer = GreenPrimaryContainerDark,
    onPrimaryContainer = GreenOnPrimaryContainerDark,

    secondary = YellowSecondaryDark,
    onSecondary = YellowOnSecondaryDark,
    secondaryContainer = YellowSecondaryContainerDark,
    onSecondaryContainer = YellowOnSecondaryContainerDark,

    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,

    error = RedAccentDark
)

// --------------------
// MAIN THEME FUNCTION
// --------------------
@Composable
fun ShoppingListApp_RoomTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    Crossfade(
        targetState = darkTheme,
        animationSpec = tween(700)
    ) { isDark ->

        val colorScheme = when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (isDark) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            }
            isDark -> DarkColors
            else -> LightColors
        }

        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

@Composable
fun resolveDark(mode: ThemeMode) : Boolean{
    return when (mode){
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
}