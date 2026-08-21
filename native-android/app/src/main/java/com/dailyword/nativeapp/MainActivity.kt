package com.dailyword.nativeapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dailyword.nativeapp.data.Settings
import com.dailyword.nativeapp.data.WordDao
import com.dailyword.nativeapp.domain.RotationEngine
import com.dailyword.nativeapp.service.DailyWordService
import com.dailyword.nativeapp.ui.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var dao: WordDao
    @Inject lateinit var engine: RotationEngine
    @Inject lateinit var settings: Settings

    private val notifPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    private var onboardingComplete by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DailyWordService.start(this)
        lifecycleScope.launch {
            engine.initialize()
            onboardingComplete = settings.flow.first().onboardingDone
        }
        setContent { DailyWordTheme { AppRoot() } }
    }

    @Composable
    fun AppRoot() {
        var tab by remember { mutableIntStateOf(0) }
        val tabs = listOf("Today", "Favorites", "History", "Settings")

        if (!onboardingComplete) {
            OnboardingScreen(onFinish = {
                onboardingComplete = true
                lifecycleScope.launch { settings.update { it.copy(onboardingDone = true) } }
            }, engine = engine, settings = settings)
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
                        tabs.forEachIndexed { i, label ->
                            NavigationBarItem(
                                selected = tab == i,
                                onClick = { tab = i },
                    icon = {
                        when (i) {
                            0 -> Icon(Icons.Default.Home, contentDescription = "Today")
                            1 -> Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                            2 -> Icon(Icons.Default.History, contentDescription = "History")
                            3 -> Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    },
                                label = { Text(label) },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)
                            )
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    when (tab) {
                        0 -> TodayScreen(engine = engine, dao = dao, settings = settings)
                        1 -> FavoritesScreen(dao = dao)
                        2 -> HistoryScreen(dao = dao)
                        3 -> SettingsScreen(engine = engine, settings = settings)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
