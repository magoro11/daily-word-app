package com.dailyword.nativeapp.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyword.nativeapp.data.HistoryEntry
import com.dailyword.nativeapp.data.WordDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(dao: WordDao) {
    var search by remember { mutableStateOf("") }
    var history by remember { mutableStateOf<List<HistoryEntry>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        history = withContext(Dispatchers.IO) { dao.history(200) }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("History", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search history…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface)
        )

        val filtered = history.filter { it.bibleVerseId?.contains(search, true) == true || it.quoteId?.contains(search, true) == true }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No history yet. Your displayed verses and quotes will appear here.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered, key = { it.id }) { entry ->
                    HistoryCard(entry, dateFormat.format(Date(entry.displayedAt))) {
                        scope.launch {
                            val text = buildString {
                                entry.bibleVerseId?.let { id -> dao.verses().firstOrNull { v -> v.id == id }?.let { v -> append("\"${v.text}\" — ${v.book} ${v.chapter}:${v.verse}") } }
                                entry.quoteId?.let { id -> dao.quotes().firstOrNull { q -> q.id == id }?.let { q -> append("${if (isNotEmpty()) "\n" else ""}\"${q.text}\" — ${q.author}") } }
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Daily Word", text))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(entry: HistoryEntry, date: String, onCopy: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(14.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(entry.bibleVerseId?.replace("_", " ") ?: entry.quoteId?.replace("_", " ") ?: "", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(date, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 12.sp)
            }
            IconButton(onClick = onCopy) { Icon(Icons.Default.ContentCopy, "Copy", tint = MaterialTheme.colorScheme.primary) }
        }
    }
}
