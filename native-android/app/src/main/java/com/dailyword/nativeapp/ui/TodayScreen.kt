package com.dailyword.nativeapp.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyword.nativeapp.data.BibleVerse
import com.dailyword.nativeapp.data.MotivationalQuote
import com.dailyword.nativeapp.data.Settings
import com.dailyword.nativeapp.data.UserSettings
import com.dailyword.nativeapp.domain.RotationEngine
import com.dailyword.nativeapp.overlay.OverlayService
import com.dailyword.nativeapp.service.DailyWordService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TodayScreen(engine: RotationEngine, dao: com.dailyword.nativeapp.data.WordDao, settings: Settings) {
    var verse by remember { mutableStateOf<BibleVerse?>(null) }
    var quote by remember { mutableStateOf<MotivationalQuote?>(null) }
    var paused by remember { mutableStateOf(false) }
    var controlsVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        settings.flow.collect { prefs ->
            paused = prefs.paused
            verse = dao.latestVerse()
            quote = dao.latestQuote()
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            kotlinx.coroutines.delay(4000)
            controlsVisible = false
        }
    }

    fun refresh() = scope.launch {
        verse = dao.latestVerse()
        quote = dao.latestQuote()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        AnimatedContent(targetState = controlsVisible, transitionSpec = {
            fadeIn() + slideInVertically { it } togetherWith fadeOut() + slideOutVertically { -it }
        }) { visible ->
            if (visible) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Daily Word", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (paused) {
                            IconButton(onClick = { engine.resume(); refresh() }) {
                                Icon(Icons.Default.PlayArrow, "Resume", tint = MaterialTheme.colorScheme.primary)
                            }
                        } else {
                            IconButton(onClick = { engine.pause() }) {
                                Icon(Icons.Default.Pause, "Pause", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        IconButton(onClick = {
                            context.startActivity(Intent(context, com.dailyword.nativeapp.overlay.OverlayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        }) {
                            Icon(Icons.Default.OpenInNew, "Overlay", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF24362E))
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
                    Text("SCRIPTURE", color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                AnimatedContent(targetState = verse?.id ?: "", transitionSpec = { fadeIn() togetherWith fadeOut() }) { id ->
                    if (id.isNotEmpty()) {
                        Text(
                            text = "\"${verse?.text ?: ""}\"",
                            color = Color.White,
                            fontSize = 26.sp,
                            lineHeight = 34.sp,
                            fontFamily = FontFamily.Serif
                        )
                    } else {
                        Text("Loading your word…", color = Color.White.copy(alpha = 0.6f))
                    }
                }
                Text(
                    text = verse?.let { "— ${it.book} ${it.chapter}:${it.verse} · ${it.translation}" } ?: "",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 15.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = {
                        engine.skip("BIBLE"); refresh()
                        scope.launch { kotlinx.coroutines.delay(3000); controlsVisible = false }
                    }) { Icon(Icons.Default.SkipPrevious, "Previous", tint = MaterialTheme.colorScheme.tertiary) }
                    IconButton(onClick = {
                        engine.skip("BIBLE"); refresh()
                        scope.launch { kotlinx.coroutines.delay(3000); controlsVisible = false }
                    }) { Icon(Icons.Default.SkipNext, "Next", tint = MaterialTheme.colorScheme.tertiary) }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        scope.launch { verse?.id?.let { dao.toggleVerse(it) }; refresh() }
                    }) {
                        Icon(
                            if (verse?.favorite == true) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            "Favorite",
                            tint = if (verse?.favorite == true) Color(0xFFE57373) else MaterialTheme.colorScheme.tertiary
                        )
                    }
                    IconButton(onClick = {
                        val text = "\"${verse?.text}\" — ${verse?.book} ${verse?.chapter}:${verse?.verse}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Daily Word", text))
                        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                    }) { Icon(Icons.Default.ContentCopy, "Copy", tint = MaterialTheme.colorScheme.tertiary) }
                    IconButton(onClick = {
                        val text = "\"${verse?.text}\" — ${verse?.book} ${verse?.chapter}:${verse?.verse}"
                        val share = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
                        startActivity(context, Intent.createChooser(share, "Share verse"))
                    }) { Icon(Icons.Outlined.Share, "Share", tint = MaterialTheme.colorScheme.tertiary) }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                    Text("MOTIVATION", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
                AnimatedContent(targetState = quote?.id ?: "", transitionSpec = { fadeIn() togetherWith fadeOut() }) { id ->
                    if (id.isNotEmpty()) {
                        Text("\"${quote?.text ?: ""}\"", color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, lineHeight = 26.sp)
                    } else {
                        Text("Loading…", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(quote?.author ?: "", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = {
                            engine.skip("QUOTE"); refresh()
                            scope.launch { kotlinx.coroutines.delay(3000); controlsVisible = false }
                        }) { Icon(Icons.Default.SkipNext, "Next", tint = MaterialTheme.colorScheme.primary) }
                        IconButton(onClick = {
                            scope.launch { quote?.id?.let { dao.toggleQuote(it) }; refresh() }
                        }) {
                            Icon(
                                if (quote?.favorite == true) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                "Favorite",
                                tint = if (quote?.favorite == true) Color(0xFFE57373) else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    DisposableEffect(Unit) {
        onDispose { }
    }
}
