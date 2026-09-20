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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("第 ${day.id} 天") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
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
            Text("今日重点：${day.todayFocus}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        if (dayDone) "本关已点亮，计入学习日历第 ${day.id} 天"
                        else "一关一天：做完下面全部微课、实验和测验，首页日历会点亮 D${day.id}",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    if (!dayDone) {
                        val lessonsDone = day.lessons.count { progress.completedLessons.contains(it.id) }
                        val labDone = if (progress.completedLabs.contains(day.lab.id)) 1 else 0
                        val quizDone = if (progress.completedQuizzes.contains(day.id)) 1 else 0
                        Text(
                            "进度 $lessonsDone/${day.lessons.size} 课 · $labDone/1 实验 · $quizDone/1 测验",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            Text("微课", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            day.lessons.forEachIndexed { i, lesson ->
                val done = progress.completedLessons.contains(lesson.id)
                Card(onClick = { onLesson(lesson.id) }, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (done) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = "已学完", tint = MaterialTheme.colorScheme.primary)
                        } else {
                            Text("${i + 1}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(lesson.title, style = MaterialTheme.typography.titleMedium)
                            Text("${lesson.minutes} 分钟 · ${lesson.summary}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Card(onClick = onLab, modifier = Modifier.fillMaxWidth().height(88.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.Terminal, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Column {
                        Text(if (day.lab.isCapstone) "大作业实验" else "今日实验", style = MaterialTheme.typography.labelLarge)
                        Text(day.lab.title, style = MaterialTheme.typography.titleMedium)
                    }
                    if (progress.completedLabs.contains(day.lab.id)) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = "实验已通过", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Card(onClick = onQuiz, modifier = Modifier.fillMaxWidth().height(88.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.Quiz, contentDescription = null)
                    Column(Modifier.weight(1f)) {
                        Text("今日测验 · ${day.quiz.size} 题", style = MaterialTheme.typography.labelLarge)
                        Text(
                            if (progress.completedQuizzes.contains(day.id)) {
                                "已提交，得分 ${progress.quizScores[day.id] ?: 0}/${day.quiz.size}"
                            } else {
                                "提交后记入进度"
                            },
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}
