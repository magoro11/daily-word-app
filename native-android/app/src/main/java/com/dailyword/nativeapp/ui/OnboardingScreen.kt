package com.dailyword.nativeapp.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyword.nativeapp.data.Settings
import com.dailyword.nativeapp.data.UserSettings
import com.dailyword.nativeapp.domain.RotationEngine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(onFinish: () -> Unit, engine: RotationEngine, settings: Settings) {
    var step by remember { mutableIntStateOf(0) }
    val steps = listOf("Welcome", "Translation", "Categories", "Intervals", "Overlay", "Notifications", "Finish")
    var prefs by remember { mutableStateOf(UserSettings()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        settings.flow.collect { prefs = it }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(40.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp), progress = { (step + 1) / steps.size.toFloat() }, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))

        when (step) {
            0 -> OnboardingWelcome { step++ }
            1 -> OnboardingTranslation(prefs) { step++ }
            2 -> OnboardingCategories(prefs) { step++ }
            3 -> OnboardingIntervals(prefs) { step++ }
            4 -> OnboardingOverlay(prefs) { step++ }
            5 -> OnboardingNotifications(prefs) { step++ }
            6 -> OnboardingFinish { onFinish() }
        }
    }
}

@Composable
fun OnboardingWelcome(onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Welcome to Daily Word", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))
        Text("Let every hour remind you of God's Word, and every five minutes give you a reason to keep moving forward.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontSize = 16.sp, lineHeight = 24.sp)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Get Started") }
    }
}

@Composable
fun OnboardingTranslation(prefs: UserSettings, onNext: () -> Unit) {
    var translation by remember { mutableStateOf(prefs.bibleTranslation) }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Choose your Bible translation", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        listOf("KJV", "WEB", "ESV", "NIV", "NKJV", "NLT").forEach { t ->
            FilterChip(selected = translation == t, onClick = { translation = t }, label = { Text(t) }, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = { scope.launch { /* save translation */ }; onNext() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Continue") }
    }
}

@Composable
fun OnboardingCategories(prefs: UserSettings, onNext: () -> Unit) {
    val bibleCats = listOf("Faith", "Strength", "Hope", "Love", "Wisdom", "Success", "Peace", "Courage", "Perseverance", "Gratitude")
    val quoteCats = listOf("Success", "Discipline", "Hard Work", "Career", "Education", "Confidence", "Leadership", "Resilience", "Personal Growth")
    var selectedBible by remember { mutableStateOf(prefs.bibleCategories) }
    var selectedQuote by remember { mutableStateOf(prefs.quoteCategories) }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Select categories", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        Text("Bible", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            bibleCats.forEach { cat ->
                val key = cat.lowercase()
                FilterChip(selected = selectedBible.contains(key), onClick = {
                    selectedBible = if (selectedBible.contains(key)) selectedBible - key else selectedBible + key
                }, label = { Text(cat) }, modifier = Modifier.fillMaxWidth())
            }
        }
        Text("Motivation", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            quoteCats.forEach { cat ->
                val key = cat.lowercase().replace(" ", "_")
                FilterChip(selected = selectedQuote.contains(key), onClick = {
                    selectedQuote = if (selectedQuote.contains(key)) selectedQuote - key else selectedQuote + key
                }, label = { Text(cat) }, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = { scope.launch { /* save categories */ }; onNext() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Continue") }
    }
}

@Composable
fun OnboardingIntervals(prefs: UserSettings, onNext: () -> Unit) {
    var bibleMinutes by remember { mutableIntStateOf(prefs.bibleMinutes) }
    var quoteMinutes by remember { mutableIntStateOf(prefs.quoteMinutes) }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Set your intervals", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        Text("Bible verse changes every", color = MaterialTheme.colorScheme.onSurface)
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text("$bibleMinutes min") }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf(15, 30, 60, 120, 240).forEach { m ->
                    DropdownMenuItem(text = { Text("$m min") }, onClick = { bibleMinutes = m; expanded = false })
                }
            }
        }
        Text("Motivational quote changes every", color = MaterialTheme.colorScheme.onSurface)
        var expanded2 by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded2 = true }, modifier = Modifier.fillMaxWidth()) { Text("$quoteMinutes min") }
            DropdownMenu(expanded = expanded2, onDismissRequest = { expanded2 = false }) {
                listOf(1, 5, 10, 15, 30).forEach { m ->
                    DropdownMenuItem(text = { Text("$m min") }, onClick = { quoteMinutes = m; expanded2 = false })
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = { scope.launch { /* save intervals */ }; onNext() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Continue") }
    }
}

@Composable
fun OnboardingOverlay(prefs: UserSettings, onNext: () -> Unit) {
    var enabled by remember { mutableStateOf(prefs.overlayEnabled) }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Floating overlay", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        Text("Show current verse or quote as a floating bubble on top of other apps.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Enable bubble", color = MaterialTheme.colorScheme.onSurface)
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = { scope.launch { /* save */ }; onNext() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Continue") }
    }
}

@Composable
fun OnboardingNotifications(prefs: UserSettings, onNext: () -> Unit) {
    var enabled by remember { mutableStateOf(prefs.notificationsEnabled) }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Notifications", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        Text("Get notified when a new Bible verse or motivation appears.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Enable alerts", color = MaterialTheme.colorScheme.onSurface)
            Switch(checked = enabled, onCheckedChange = { enabled = it })
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = { scope.launch { /* save */ }; onNext() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Continue") }
    }
}

@Composable
fun OnboardingFinish(onFinish: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(24.dp))
        Text("You're all set!", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))
        Text("Your Daily Word will now inspire you throughout the day.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 16.sp)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Start") }
    }
}
