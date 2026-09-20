package com.py2c.week.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.py2c.week.data.CourseDay
import com.py2c.week.data.ProgressSnapshot
import com.py2c.week.data.WeekCurriculum
import com.py2c.week.data.completedCount
import com.py2c.week.data.completedCourseDayIds
import com.py2c.week.data.itemCount
import com.py2c.week.ui.navigation.LocalContainer
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    curriculum: WeekCurriculum,
    progress: ProgressSnapshot,
    onOpenDay: (Int) -> Unit,
    onReset: () -> Unit,
) {
    val percent = progress.overallPercent(curriculum)
    val store = LocalContainer.current.progressStore
    val scope = rememberCoroutineScope()
    var confirmReset by remember { mutableStateOf(false) }

    LaunchedEffect(progress, curriculum) {
        store.syncCompletedCourseDays(progress.completedCourseDayIds(curriculum))
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(curriculum.brand, style = MaterialTheme.typography.displaySmall)
        Text(curriculum.brandEn, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Text(curriculum.tagline, style = MaterialTheme.typography.bodyLarge)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("一周进度  $percent%", style = MaterialTheme.typography.titleLarge)
                LinearProgressIndicator(
                    progress = { percent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "总体完成 $percent 百分之" },
                )
                Text(
                    "课文用生活场景打比方（门牌号、食谱、储物柜），再落到精确的 C。每天微课 + 实验 + 测验。真正的 gcc 在电脑 VS Code 里跑，手机上是讲解与模拟评测。",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        WeekCheckInCard(
            curriculum = curriculum,
            progress = progress,
            onOpenDay = onOpenDay,
            onCheckInToday = { scope.launch { store.checkInToday() } },
        )
        curriculum.days.forEach { day ->
            DayCard(day, progress, onOpen = { onOpenDay(day.id) })
        }
        FilledTonalButton(onClick = { confirmReset = true }, modifier = Modifier.height(48.dp)) {
            Text("清除本地进度")
        }
        Spacer(Modifier.height(12.dp))
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("清除进度？") },
            text = { Text("课程内容仍在，只删除本机 DataStore 里的完成记录。") },
            confirmButton = {
                TextButton(onClick = {
                    confirmReset = false
                    scope.launch { store.resetAll() }
                    onReset()
                }) { Text("清除") }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("取消") } },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayCard(day: CourseDay, progress: ProgressSnapshot, onOpen: () -> Unit) {
    val done = day.completedCount(progress)
    val total = day.itemCount()
    val complete = done == total
    Card(
        onClick = onOpen,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "第${day.id}天 ${day.title}" },
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                if (complete) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = if (complete) "已完成" else "未完成",
                tint = if (complete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(32.dp),
            )
            Column(Modifier.weight(1f)) {
                Text("第 ${day.id} 天 · ${day.minutes} 分钟", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(day.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(day.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(progress = { done / total.toFloat() }, modifier = Modifier.fillMaxWidth())
                Text("$done / $total 项", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
