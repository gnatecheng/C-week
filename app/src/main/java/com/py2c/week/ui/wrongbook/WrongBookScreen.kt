package com.py2c.week.ui.wrongbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.py2c.week.data.ProgressSnapshot
import com.py2c.week.data.WeekCurriculum
import com.py2c.week.data.WrongItem
import com.py2c.week.data.WrongSource
import com.py2c.week.data.groupedByDay
import com.py2c.week.ui.strings.rememberStrings

@Composable
fun WrongBookScreen(
    curriculum: WeekCurriculum,
    progress: ProgressSnapshot,
    onRedoQuiz: (dayId: Int, questionId: String) -> Unit,
    onRedoLab: (labId: String) -> Unit,
    onOpenReport: () -> Unit,
) {
    val strings = rememberStrings()
    val open = progress.wrongItems.filter { !it.resolved }
    val done = progress.wrongItems.filter { it.resolved }
    var showResolved by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(strings.wrongBookTitle, style = MaterialTheme.typography.headlineMedium)
        Text(
            strings.wrongBookIntro,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Outlined.AutoStories, contentDescription = null)
                Column(Modifier.weight(1f)) {
                    Text(strings.wrongBookOpenCount(open.size), style = MaterialTheme.typography.titleLarge)
                    Text(strings.wrongBookResolvedCount(done.size), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        FilledTonalButton(
            onClick = onOpenReport,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) { Text(strings.wrongBookOpenReport) }

        if (open.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(strings.wrongBookEmptyTitle, style = MaterialTheme.typography.titleMedium)
                    Text(strings.wrongBookEmptyBody, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            open.groupedByDay().forEach { (dayId, items) ->
                val title = curriculum.days.find { it.id == dayId }?.title.orEmpty()
                Text(strings.wrongBookDayHeader(dayId, title), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                items.forEach { item ->
                    WrongCard(item, onRedoQuiz, onRedoLab)
                }
            }
        }

        if (done.isNotEmpty()) {
            FilledTonalButton(onClick = { showResolved = !showResolved }) {
                Text(if (showResolved) strings.wrongBookHideResolved else strings.wrongBookShowResolved(done.size))
            }
            if (showResolved) {
                done.groupedByDay().forEach { (dayId, items) ->
                    Text(strings.wrongBookDayResolved(dayId), style = MaterialTheme.typography.titleSmall)
                    items.forEach { item ->
                        WrongCard(item, onRedoQuiz, onRedoLab)
                    }
                }
            }
        }
    }
}

@Composable
private fun WrongCard(
    item: WrongItem,
    onRedoQuiz: (dayId: Int, questionId: String) -> Unit,
    onRedoLab: (labId: String) -> Unit,
) {
    val strings = rememberStrings()
    val container = if (item.resolved) MaterialTheme.colorScheme.surfaceVariant
    else MaterialTheme.colorScheme.errorContainer
    Card(
        onClick = {
            if (item.source == WrongSource.QUIZ) onRedoQuiz(item.dayId, item.questionId)
            else onRedoLab(item.questionId)
        },
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                when {
                    item.resolved -> Icons.Outlined.CheckCircle
                    item.source == WrongSource.LAB -> Icons.Outlined.Terminal
                    else -> Icons.Outlined.Quiz
                },
                contentDescription = strings.wrongSourceLabel(item.source),
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "${strings.wrongSourceLabel(item.source)} · ${item.hintCategory}" +
                        if (item.resolved) strings.wrongResolvedSuffix else "",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(item.prompt, style = MaterialTheme.typography.titleMedium)
                Text("${strings.wrongYourAnswer}${item.userAnswer}", style = MaterialTheme.typography.bodySmall)
                Text("${strings.wrongCorrectAnswer}${item.correctAnswer}", style = MaterialTheme.typography.bodySmall)
                Text(
                    if (item.source == WrongSource.QUIZ) strings.wrongRedoQuiz else strings.wrongRedoLab,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
