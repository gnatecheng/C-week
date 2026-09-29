package com.py2c.week.ui.day

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.py2c.week.data.CourseDay
import com.py2c.week.data.ProgressSnapshot
import com.py2c.week.data.isFullyComplete
import com.py2c.week.ui.strings.rememberStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    day: CourseDay,
    progress: ProgressSnapshot,
    onBack: () -> Unit,
    onLesson: (String) -> Unit,
    onQuiz: () -> Unit,
    onLab: () -> Unit,
) {
    val strings = rememberStrings()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.dayTitle(day.id)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(day.title, style = MaterialTheme.typography.headlineSmall)
            Text(day.outcome, style = MaterialTheme.typography.bodyLarge)
            Text(strings.dayTodayFocus(day.todayFocus), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            val dayDone = day.isFullyComplete(progress)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (dayDone) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        if (dayDone) strings.dayCompleteBanner(day.id)
                        else strings.dayIncompleteBanner(day.id),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    if (!dayDone) {
                        val lessonsDone = day.lessons.count { progress.completedLessons.contains(it.id) }
                        val labDone = if (progress.completedLabs.contains(day.lab.id)) 1 else 0
                        val quizDone = if (progress.completedQuizzes.contains(day.id)) 1 else 0
                        Text(
                            strings.dayProgressDetail(lessonsDone, day.lessons.size, labDone, quizDone),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            Text(strings.dayLessons, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            day.lessons.forEachIndexed { i, lesson ->
                val done = progress.completedLessons.contains(lesson.id)
                Card(onClick = { onLesson(lesson.id) }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (done) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = strings.lessonDone, tint = MaterialTheme.colorScheme.primary)
                        } else {
                            Text("${i + 1}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(lesson.title, style = MaterialTheme.typography.titleMedium)
                            Text(strings.lessonCardSubtitle(lesson.minutes, lesson.summary), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Card(onClick = onLab, modifier = Modifier.fillMaxWidth().height(88.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.Terminal, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Column {
                        Text(if (day.lab.isCapstone) strings.dayCapstoneLab else strings.dayTodayLab, style = MaterialTheme.typography.labelLarge)
                        Text(day.lab.title, style = MaterialTheme.typography.titleMedium)
                    }
                    if (progress.completedLabs.contains(day.lab.id)) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = strings.labPassed, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Card(onClick = onQuiz, modifier = Modifier.fillMaxWidth().height(88.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.Quiz, contentDescription = null)
                    Column(Modifier.weight(1f)) {
                        Text(strings.dayQuizLabel(day.quiz.size), style = MaterialTheme.typography.labelLarge)
                        Text(
                            if (progress.completedQuizzes.contains(day.id)) {
                                strings.dayQuizSubmitted(progress.quizScores[day.id] ?: 0, day.quiz.size)
                            } else {
                                strings.dayQuizPending
                            },
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}
