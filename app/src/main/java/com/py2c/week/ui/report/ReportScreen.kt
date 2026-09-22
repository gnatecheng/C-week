package com.py2c.week.ui.report

import android.content.Intent
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
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.py2c.week.data.LearningReport
import com.py2c.week.data.WeekCurriculum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    curriculum: WeekCurriculum,
    report: LearningReport,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val shareBody = report.shareText(curriculum.brand)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习报告") },
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
            Text(curriculum.brand, style = MaterialTheme.typography.headlineSmall)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("连续打卡 ${report.streak} 天", style = MaterialTheme.typography.titleLarge)
                    Text("累计 ${report.checkinDays} 天有学习记录 · 总体进度 ${report.overallPercent}%")
                    LinearProgressIndicator(
                        progress = { report.overallPercent / 100f },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatChip(
                    title = "测验正确率",
                    value = if (report.quizAsked == 0) "—" else "${report.quizAccuracyPercent}%",
                    subtitle = if (report.quizAsked == 0) "还没交卷" else "${report.quizCorrect}/${report.quizAsked}",
                    modifier = Modifier.weight(1f),
                )
                StatChip(
                    title = "实验通过",
                    value = "${report.labsPassed}/${report.labsTotal}",
                    subtitle = "待订正 ${report.openWrongs} 题",
                    modifier = Modifier.weight(1f),
                )
            }
            Text("分天完成度", style = MaterialTheme.typography.titleMedium)
            report.days.forEach { day ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("第 ${day.dayId} 天 · ${day.title}", style = MaterialTheme.typography.titleMedium)
                        LinearProgressIndicator(
                            progress = { day.percent / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            "${day.percent}%（${day.done}/${day.total}） · " +
                                (day.quizScore?.let { "测验 $it/${day.quizTotal}" } ?: "测验未交") +
                                " · " + if (day.labDone) "实验已过" else "实验未过",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "${curriculum.brand}学习报告")
                        putExtra(Intent.EXTRA_TEXT, shareBody)
                    }
                    context.startActivity(Intent.createChooser(intent, "分享学习报告"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Icon(Icons.Outlined.Share, contentDescription = null)
                Text("  分享报告", style = MaterialTheme.typography.titleMedium)
            }
            Text(
                "会打开系统分享面板，可发到微信、邮件或其他应用。内容只含本机进度，不含账号。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatChip(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
