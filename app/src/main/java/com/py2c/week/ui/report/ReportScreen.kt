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
import com.py2c.week.ui.strings.rememberStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    curriculum: WeekCurriculum,
    report: LearningReport,
    onBack: () -> Unit,
) {
    val strings = rememberStrings()
    val context = LocalContext.current
    val brand = strings.brandName
    val shareBody = report.shareText(brand, strings)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.reportTitle) },
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
            Text(brand, style = MaterialTheme.typography.headlineSmall)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.reportStreak(report.streak), style = MaterialTheme.typography.titleLarge)
                    Text(strings.reportCheckins(report.checkinDays, report.overallPercent))
                    LinearProgressIndicator(
                        progress = { report.overallPercent / 100f },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                StatChip(
                    title = strings.reportQuizAccuracy,
                    value = if (report.quizAsked == 0) "—" else "${report.quizAccuracyPercent}%",
                    subtitle = if (report.quizAsked == 0) strings.reportQuizNotTaken
                    else strings.reportQuizSubtitle(report.quizCorrect, report.quizAsked),
                    modifier = Modifier.weight(1f),
                )
                StatChip(
                    title = strings.reportLabsPassed,
                    value = "${report.labsPassed}/${report.labsTotal}",
                    subtitle = strings.reportOpenWrongs(report.openWrongs),
                    modifier = Modifier.weight(1f),
                )
            }
            Text(strings.reportDayBreakdown, style = MaterialTheme.typography.titleMedium)
            report.days.forEach { day ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(strings.reportDayLine(day.dayId, day.title), style = MaterialTheme.typography.titleMedium)
                        LinearProgressIndicator(
                            progress = { day.percent / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        val quizPart = day.quizScore?.let { strings.reportQuizScore(it, day.quizTotal) }
                            ?: strings.reportQuizNotSubmitted
                        val labPart = if (day.labDone) strings.reportLabDone else strings.reportLabNotDone
                        Text(
                            strings.reportDayDetail(day.percent, day.done, day.total, quizPart, labPart),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, strings.reportShareSubject(brand))
                        putExtra(Intent.EXTRA_TEXT, shareBody)
                    }
                    context.startActivity(Intent.createChooser(intent, strings.reportShareChooser))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Icon(Icons.Outlined.Share, contentDescription = null)
                Text(strings.reportShare, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                strings.reportShareHint,
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
