package com.dailyword.nativeapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ShareCompat
import com.dailyword.nativeapp.R
import com.dailyword.nativeapp.data.BibleVerse
import com.dailyword.nativeapp.data.MotivationalQuote
import com.dailyword.nativeapp.data.Settings
import com.dailyword.nativeapp.domain.RotationEngine
import com.dailyword.nativeapp.overlay.OverlayService
import com.dailyword.nativeapp.service.DailyWordService
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun DailyWordTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val settings = remember { Settings(context.applicationContext) }
    var themeMode by remember { mutableStateOf("system") }
    var dynamicColor by remember { mutableStateOf(true) }
    var fontScale by remember { mutableStateOf(1.0f) }

    LaunchedEffect(Unit) {
        settings.flow.collect { prefs ->
            themeMode = prefs.theme
            dynamicColor = prefs.dynamicColor
            fontScale = prefs.fontScale
        }
    }

    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
        else dynamicLightColorScheme(LocalContext.current)
    } else {
        if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = {
            CompositionLocalProvider(LocalTextStyle provides LocalTextStyle.current.copy(fontSize = (14.sp * fontScale))) {
                content()
            }
        }
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5B7B67),
    secondary = Color(0xFF8A6A2B),
    tertiary = Color(0xFFDCCB98),
    background = Color(0xFFF8F7F2),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7FA088),
    secondary = Color(0xFFB8913D),
    tertiary = Color(0xFFDCCB98),
    background = Color(0xFF18231F),
    surface = Color(0xFF24362E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE6E1E1),
    onSurface = Color(0xFFE6E1E1)
)
