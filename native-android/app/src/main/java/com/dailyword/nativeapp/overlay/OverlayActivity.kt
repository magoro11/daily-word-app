package com.dailyword.nativeapp.overlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyword.nativeapp.data.BibleVerse
import com.dailyword.nativeapp.data.MotivationalQuote
import com.dailyword.nativeapp.ui.DailyWordTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OverlayActivity : ComponentActivity() {
    @Composable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DailyWordTheme { OverlayScreen() } }
    }
}

@Composable
fun OverlayScreen() {
    var verse by remember { mutableStateOf<BibleVerse?>(null) }
    var quote by remember { mutableStateOf<MotivationalQuote?>(null) }

    LaunchedEffect(Unit) {
        // In a real app, observe from ViewModel/RotationEngine
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF18231F))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF24362E))
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("SCRIPTURE", color = Color(0xFFDCCB98), style = MaterialTheme.typography.labelSmall)
                Text(
                    text = "\"${verse?.text ?: "Loading…"}\"",
                    color = Color.White,
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                )
                Text(
                    text = verse?.let { "— ${it.book} ${it.chapter}:${it.verse} · ${it.translation}" } ?: "",
                    color = Color(0xFFDCCB98)
                )
                Divider(color = Color.White.copy(alpha = 0.1f))
                Text("MOTIVATION", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                Text("\"${quote?.text ?: ""}\"", color = Color.White, fontSize = 18.sp)
                Text(quote?.author ?: "", color = Color(0xFFDCCB98), fontSize = 14.sp)
            }
        }
    }
}
