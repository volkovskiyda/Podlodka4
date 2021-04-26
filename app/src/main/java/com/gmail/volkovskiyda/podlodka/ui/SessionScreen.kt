package com.gmail.volkovskiyda.podlodka.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gmail.volkovskiyda.podlodka.data.SessionRepository
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun SessionScreen(repository: SessionRepository, sessionId: String) {
    val session = repository.getSession(sessionId)
    SessionDetail(
        imageUrl = session.imageUrl,
        speaker = session.speaker,
        dateTime = "${session.date}, ${session.timeInterval}",
        description = session.description
    )
}

@Composable
fun SessionDetail(
    imageUrl: String,
    speaker: String,
    dateTime: String,
    description: String,
) {
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterVertically)) {
            Image(
                painter = rememberCoilPainter(imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(240.dp)
                    .padding(16.dp)
                    .clip(CircleShape)
            )
            Text(
                text = speaker,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
            )
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = dateTime,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.body2
                )
            }
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = description,
                style = MaterialTheme.typography.body1
            )
        }
    }
}