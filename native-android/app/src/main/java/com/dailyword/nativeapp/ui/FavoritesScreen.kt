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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyword.nativeapp.data.BibleVerse
import com.dailyword.nativeapp.data.MotivationalQuote
import com.dailyword.nativeapp.data.WordDao
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(dao: WordDao) {
    var search by remember { mutableStateOf("") }
    var verses by remember { mutableStateOf<List<BibleVerse>>(emptyList()) }
    var quotes by remember { mutableStateOf<List<MotivationalQuote>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        kotlinx.coroutines.flow.combine(dao.favoriteVerses(), dao.favoriteQuotes()) { v, q ->
            verses = v
            quotes = q
        }.collect()
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Saved for later", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search favorites…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface)
        )

        val filteredVerses = verses.filter { it.text.contains(search, true) || it.book.contains(search, true) }
        val filteredQuotes = quotes.filter { it.text.contains(search, true) || it.author.contains(search, true) }

        if (filteredVerses.isEmpty() && filteredQuotes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet. Tap the heart on a verse or quote to save it.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredVerses, key = { it.id }) { v ->
                    FavoriteVerseCard(v) {
                        scope.launch {
                            dao.toggleVerse(v.id)
                            verses = dao.favoriteVerses().first()
                            quotes = dao.favoriteQuotes().first()
                        }
                    }
                }
                items(filteredQuotes, key = { it.id }) { q ->
                    FavoriteQuoteCard(q) {
                        scope.launch {
                            dao.toggleQuote(q.id)
                            verses = dao.favoriteVerses().first()
                            quotes = dao.favoriteQuotes().first()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteVerseCard(v: BibleVerse, onRemove: () -> Unit) {
    val context = LocalContext.current
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("\"${v.text}\"", color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Serif, fontSize = 16.sp)
            Text("— ${v.book} ${v.chapter}:${v.verse}", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Daily Word", "\"${v.text}\" — ${v.book} ${v.chapter}:${v.verse}"))
                }) { Icon(Icons.Default.ContentCopy, "Copy", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = {
                    val share = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "\"${v.text}\" — ${v.book} ${v.chapter}:${v.verse}")
                    context.startActivity(Intent.createChooser(share, "Share verse"))
                }) { Icon(Icons.Outlined.Share, "Share", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onRemove) { Icon(Icons.Default.Delete, "Remove", tint = Color(0xFFE57373)) }
            }
        }
    }
}

@Composable
fun FavoriteQuoteCard(q: MotivationalQuote, onRemove: () -> Unit) {
    val context = LocalContext.current
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("\"${q.text}\"", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            Text(q.author, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Daily Word", "\"${q.text}\" — ${q.author}"))
                }) { Icon(Icons.Default.ContentCopy, "Copy", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = {
                    val share = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "\"${q.text}\" — ${q.author}")
                    context.startActivity(Intent.createChooser(share, "Share quote"))
                }) { Icon(Icons.Outlined.Share, "Share", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onRemove) { Icon(Icons.Default.Delete, "Remove", tint = Color(0xFFE57373)) }
            }
        }
    }
}
