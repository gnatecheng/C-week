package com.py2c.week.ui.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.py2c.week.data.CodeLab
import com.py2c.week.data.CourseDay
import com.py2c.week.data.GradeHint
import com.py2c.week.data.LabEvaluation
import com.py2c.week.data.ProgressSnapshot
import com.py2c.week.data.ProgressStore
import com.py2c.week.data.WeekCurriculum
import com.py2c.week.data.evaluateLab
import com.py2c.week.ui.components.CodePane
import com.py2c.week.ui.theme.CodeBgDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabListScreen(
    curriculum: WeekCurriculum,
    progress: ProgressSnapshot,
    onOpen: (String) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("代码实验", style = MaterialTheme.typography.headlineMedium)
        Text(
            "模拟评测会对照期望输出，并指出缺头文件、差一、指针、公式、TODO 空壳等具体错因。真实编译请在电脑 VS Code 使用 gcc/clang。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        curriculum.days.forEach { day ->
            val done = progress.completedLabs.contains(day.lab.id)
            Card(onClick = { onOpen(day.lab.id) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        if (done) Icons.Outlined.CheckCircle else Icons.Outlined.Terminal,
                        contentDescription = if (done) "已通过" else "未通过",
                        tint = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Column(Modifier.weight(1f)) {
                        Text("第 ${day.id} 天${if (day.lab.isCapstone) " · 大作业" else ""}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Text(day.lab.title, style = MaterialTheme.typography.titleMedium)
                        Text(day.lab.brief, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabScreen(
    day: CourseDay,
    lab: CodeLab,
    progress: ProgressSnapshot,
    store: ProgressStore,
    onBack: () -> Unit,
) {
    var code by remember(lab.id) { mutableStateOf(lab.starterCode) }
    var result by remember { mutableStateOf<LabEvaluation?>(null) }
    var showSolution by remember { mutableStateOf(progress.revealedSolutions.contains(lab.id)) }
    val attempts = progress.labAttempts[lab.id] ?: 0
    val scope = rememberCoroutineScope()
    val canReveal = attempts >= lab.attemptsBeforeReveal || showSolution || progress.revealedSolutions.contains(lab.id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (lab.isCapstone) "大作业" else "实验") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("第 ${day.id} 天", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(lab.title, style = MaterialTheme.typography.headlineSmall)
            Text(lab.brief, style = MaterialTheme.typography.bodyLarge)
            Text(lab.task, style = MaterialTheme.typography.bodyMedium)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Text(
                    "真实编译在电脑：gcc ${if (lab.isCapstone) "dijkstra.c -o dijkstra" else "lab.c -o lab"} -Wall。下面是教学模拟器。",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (lab.testCases.isNotEmpty()) {
                Text("测试用例", style = MaterialTheme.typography.titleMedium)
                lab.testCases.forEach { tc ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(tc.name, style = MaterialTheme.typography.titleSmall)
                            Text(tc.inputDesc, style = MaterialTheme.typography.bodyMedium)
                            Text("期望：${tc.expected}", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            Text("编辑器", style = MaterialTheme.typography.titleMedium)
            Card(colors = CardDefaults.cardColors(containerColor = CodeBgDark), modifier = Modifier.fillMaxWidth()) {
                BasicTextField(
                    value = code,
                    onValueChange = { code = it },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = androidx.compose.ui.graphics.Color(0xFFD4D4D4),
                    ),
                    cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 240.dp)
                        .padding(12.dp),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val eval = evaluateLab(code, lab)
                        result = eval
                        scope.launch {
                            store.bumpLabAttempt(lab.id)
                            if (eval.passed) {
                                store.markLab(lab.id)
                                val labDone = true
                                val lessonsDone = day.lessons.count { progress.completedLessons.contains(it.id) } == day.lessons.size
                                val quizDone = progress.completedQuizzes.contains(day.id)
                                if (labDone && lessonsDone && quizDone) {
                                    store.checkInCourseDay(day.id)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                ) { Text("模拟运行 / 检查") }
                OutlinedButton(
                    onClick = { code = lab.starterCode; result = null },
                    modifier = Modifier.height(48.dp),
                ) { Text("重置") }
            }
            result?.let { eval ->
                SimulatedResultCard(eval)
            }
            if (lab.isCapstone) {
                DijkstraBoard(unlocked = result?.passed == true || progress.completedLabs.contains(lab.id))
            }
            Text("提示（尝试 $attempts 次）", style = MaterialTheme.typography.titleMedium)
            lab.hints.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            Text("期望输出", style = MaterialTheme.typography.titleMedium)
            CodePane("output", lab.expectedOutput.trimEnd() + "\n", "golden")
            FilledTonalButton(
                onClick = {
                    if (canReveal) {
                        showSolution = true
                        scope.launch { store.revealSolution(lab.id) }
                    }
                },
                enabled = canReveal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(
                    if (showSolution) "参考答案已展开"
                    else if (canReveal) "揭晓参考答案"
                    else "再试 ${lab.attemptsBeforeReveal - attempts} 次后可看答案",
                )
            }
            if (showSolution) {
                CodePane("c", lab.solutionCode, "参考实现")
            }
        }
    }
}

@Composable
private fun SimulatedResultCard(eval: LabEvaluation) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (eval.passed) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                if (eval.passed) "模拟运行通过" else "模拟运行未通过",
                style = MaterialTheme.typography.titleMedium,
            )
            if (eval.totalChecks > 0) {
                Text("自动检查 ${eval.passedChecks} / ${eval.totalChecks}")
                LinearProgressIndicator(
                    progress = { eval.passedChecks / eval.totalChecks.coerceAtLeast(1).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            eval.simulatedOutput?.let {
                Text("模拟 stdout:\n$it", fontFamily = FontFamily.Monospace)
            }
            if (!eval.passed) {
                Text(
                    "对照黄金输出失败。下面是具体错因，按类别标出（缺头文件、差一、指针、公式、TODO 空壳等）。",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            eval.checkOutcomes.forEach { outcome ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        if (outcome.passed) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
                        contentDescription = if (outcome.passed) "通过" else "未通过",
                        tint = if (outcome.passed) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                    )
                    Text(
                        if (outcome.passed) "检查 ${outcome.id} 通过" else "检查 ${outcome.id}：${outcome.failHint}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            eval.gradeHints.distinctBy { it.detail }.forEach { hint ->
                HintCard(hint)
            }
        }
    }
}

@Composable
private fun HintCard(hint: GradeHint) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("错因 · ${hint.title}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
            Text(hint.detail, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
