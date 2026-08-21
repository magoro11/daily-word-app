package com.dailyword.nativeapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings as SystemSettings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyword.nativeapp.data.Settings
import com.dailyword.nativeapp.data.UserSettings
import com.dailyword.nativeapp.domain.RotationEngine
import com.dailyword.nativeapp.overlay.OverlayService
import com.dailyword.nativeapp.service.DailyWordService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Composable
fun SettingsScreen(engine: RotationEngine, settings: Settings) {
    var prefs by remember { mutableStateOf(UserSettings()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        settings.flow.collect { prefs = it }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item { Text("Settings", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground) }

            item { SettingsSection("Bible") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Enabled", color = MaterialTheme.colorScheme.onSurface)
                    Switch(checked = prefs.bibleEnabled, onCheckedChange = { scope.launch { settings.update { it.copy(bibleEnabled = it) }; engine.reschedule("BIBLE") } })
                }
                if (prefs.bibleEnabled) {
                    Spacer(Modifier.height(8.dp))
                    Text("Interval", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 13.sp)
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expanded = true }) { Text("${prefs.bibleMinutes} min") }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf(15, 30, 60, 120, 240).forEach { m ->
                                DropdownMenuItem(text = { Text("$m min") }, onClick = {
                                    scope.launch { settings.update { it.copy(bibleMinutes = m) }; engine.reschedule("BIBLE") }; expanded = false
                                })
                            }
                        }
                    }
                }
            }}

            item { SettingsSection("Motivation") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Enabled", color = MaterialTheme.colorScheme.onSurface)
                    Switch(checked = prefs.quoteEnabled, onCheckedChange = { scope.launch { settings.update { it.copy(quoteEnabled = it) }; engine.reschedule("QUOTE") } })
                }
                if (prefs.quoteEnabled) {
                    Spacer(Modifier.height(8.dp))
                    Text("Interval", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 13.sp)
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expanded = true }) { Text("${prefs.quoteMinutes} min") }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf(1, 5, 10, 15, 30).forEach { m ->
                                DropdownMenuItem(text = { Text("$m min") }, onClick = {
                                    scope.launch { settings.update { it.copy(quoteMinutes = m) }; engine.reschedule("QUOTE") }; expanded = false
                                })
                            }
                        }
                    }
                }
            }}

            item { SettingsSection("Appearance") {
                Text("Theme", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("light", "dark", "system").forEach { mode ->
                        FilterChip(
                            selected = prefs.theme == mode,
                            onClick = { scope.launch { settings.update { it.copy(theme = mode) } } },
                            label = { Text(mode.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Dynamic color", color = MaterialTheme.colorScheme.onSurface)
                    Switch(checked = prefs.dynamicColor, onCheckedChange = { scope.launch { settings.update { it.copy(dynamicColor = it) } } })
                }
            }}

            item { SettingsSection("Notifications") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Content alerts", color = MaterialTheme.colorScheme.onSurface)
                    Switch(checked = prefs.notificationsEnabled, onCheckedChange = { scope.launch { settings.update { it.copy(notificationsEnabled = it) } } })
                }
            }}

            item { SettingsSection("Overlay") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Enable bubble", color = MaterialTheme.colorScheme.onSurface)
                    Switch(checked = prefs.overlayEnabled, onCheckedChange = { enabled ->
                    scope.launch {
                        settings.update { it.copy(overlayEnabled = enabled) }
                        if (enabled && !SystemSettings.canDrawOverlays(context)) {
                            context.startActivity(Intent(SystemSettings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                        } else if (enabled) OverlayService.start(context) else OverlayService.stop(context)
                    }
                    })
                }
                if (prefs.overlayEnabled) {
                    Spacer(Modifier.height(8.dp))
                    Text("Position", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("top", "center", "bottom").forEach { pos ->
                            FilterChip(
                                selected = prefs.overlayPosition == pos,
                                onClick = { scope.launch { settings.update { it.copy(overlayPosition = pos) } } },
                                label = { Text(pos.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Click-through", color = MaterialTheme.colorScheme.onSurface)
                        Switch(checked = prefs.overlayClickThrough, onCheckedChange = { scope.launch { settings.update { it.copy(overlayClickThrough = it) } } })
                    }
                }
            }}

            item { SettingsSection("System") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Start with system", color = MaterialTheme.colorScheme.onSurface)
                    Switch(checked = prefs.startWithSystem, onCheckedChange = { scope.launch { settings.update { it.copy(startWithSystem = it) } } })
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val intent = Intent(SystemSettings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        context.startActivity(intent)
                    }
                }) { Text("Allow exact alarms") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = {
                    val intent = Intent(SystemSettings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    context.startActivity(intent)
                }) { Text("Battery optimization") }
            }}

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            content()
        }
    }
}
