package com.gmail.volkovskiyda.podlodka.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gmail.volkovskiyda.podlodka.data.SessionRepository
import com.gmail.volkovskiyda.podlodka.model.Session
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    repository: SessionRepository,
    navigateToSession: (String) -> Unit,
    onFinish: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val snackbarErrorState = remember { mutableStateOf("") }
    val finishDialogState = remember { mutableStateOf(false) }

    val sessions: List<Session> = repository.getSessions()
    val favorites: Set<String> by repository.observeFavorites().collectAsState(initial = setOf())
    val textState = rememberSaveable { mutableStateOf("") }

    BackHandler(true) {
        finishDialogState.value = finishDialogState.value.not()
    }

    if (finishDialogState.value) {
        AlertDialog(
            title = { Text("Выход") },
            text = { Text("Вы уверены, что хотите выйти из приложения?") },
            onDismissRequest = { finishDialogState.value = false },
            confirmButton = {
                Text(
                    text = "Да",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onFinish() },
                    color = MaterialTheme.colors.onSurface
                )
            },
            dismissButton = {
                Text(
                    text = "Отмена",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { finishDialogState.value = false },
                )
            }
        )
    }

    Box {
        LazyColumn(Modifier.padding(8.dp)) {
            item {
                TextField(
                    value = textState.value,
                    onValueChange = { value -> textState.value = value },
                    label = { Text(text = "Поиск") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                val favoriteSessions = sessions.filter { it.id in favorites }
                if (favoriteSessions.isNotEmpty()) {
                    Column {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "Избранное",
                            style = MaterialTheme.typography.h6,
                        )

                        LazyRow {
                            items(favoriteSessions) { session ->
                                FavoriteCard(
                                    timeInterval = session.timeInterval,
                                    date = session.date,
                                    speaker = session.speaker,
                                    description = session.description,
                                    onClick = { navigateToSession(session.id) },
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                        text = "Сессии",
                        style = MaterialTheme.typography.h6,
                    )
                }
            }

            val searchText = textState.value
            for ((date, sessionsForDate) in sessions.filter { session ->
                searchText.isEmpty() ||
                        session.description.contains(searchText, ignoreCase = true) ||
                        session.speaker.contains(searchText, ignoreCase = true)
            }.groupBy { it.date }) {
                stickyHeader {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = date,
                            maxLines = 1,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }

                items(sessionsForDate) { session ->
                    val sessionId = session.id
                    val isFavorite = sessionId in favorites
                    SessionCard(
                        imageUrl = session.imageUrl,
                        speaker = session.speaker,
                        timeInterval = session.timeInterval,
                        description = session.description,
                        isFavorite = isFavorite,
                        onToggleFavorite = {
                            if (favorites.size < 3 || isFavorite) {
                                repository.toggleFavorite(sessionId)
                            } else {
                                snackbarErrorState.value = "Не удалось добавить сессию в избранное"
                            }
                        },
                        onClick = { navigateToSession(sessionId) }
                    )
                }
            }
        }
        if (snackbarErrorState.value.isNotEmpty()) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            ) { Text(snackbarErrorState.value) }
            LaunchedEffect(scaffoldState) {
                delay(2_000)
                snackbarErrorState.value = ""
            }
        }
    }
}

@Composable
fun FavoriteCard(
    timeInterval: String,
    date: String,
    speaker: String,
    description: String,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .size(140.dp, 140.dp)
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp,

        ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = timeInterval,
                maxLines = 1,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
            Text(text = date, maxLines = 1, style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = speaker,
                maxLines = 1,
                style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
fun SessionCard(
    imageUrl: String,
    speaker: String,
    timeInterval: String,
    description: String,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp,
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberCoilPainter(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = speaker,
                    maxLines = 1,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = timeInterval,
                    maxLines = 1,
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2
                )
            }
            IconToggleButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                checked = isFavorite,
                onCheckedChange = { onToggleFavorite() }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    tint = if (isFavorite) Color.Red else LocalContentColor.current,
                    contentDescription = null // handled by click label of parent
                )
            }
        }
    }
}