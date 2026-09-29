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
import com.py2c.week.ui.strings.rememberStrings
import com.py2c.week.ui.theme.CodeBgDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabListScreen(
    curriculum: WeekCurriculum,
    progress: ProgressSnapshot,
    onOpen: (String) -> Unit,
) {
    val strings = rememberStrings()
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(strings.labsListTitle, style = MaterialTheme.typography.headlineMedium)
        Text(
            strings.labsListIntro,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        curriculum.days.forEach { day ->
            val done = progress.completedLabs.contains(day.lab.id)
            Card(onClick = { onOpen(day.lab.id) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        if (done) Icons.Outlined.CheckCircle else Icons.Outlined.Terminal,
                        contentDescription = if (done) strings.passed else strings.notPassed,
                        tint = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Column(Modifier.weight(1f)) {
                        Text(strings.labsListDay(day.id, day.lab.isCapstone), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
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
    val strings = rememberStrings()
    var code by remember(lab.id) { mutableStateOf(lab.starterCode) }
    var result by remember { mutableStateOf<LabEvaluation?>(null) }
    var showSolution by remember { mutableStateOf(progress.revealedSolutions.contains(lab.id)) }
    val attempts = progress.labAttempts[lab.id] ?: 0
    val scope = rememberCoroutineScope()
    val canReveal = attempts >= lab.attemptsBeforeReveal || showSolution || progress.revealedSolutions.contains(lab.id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (lab.isCapstone) strings.labCapstoneTitle else strings.labTitle) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(strings.labDayLabel(day.id), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(lab.title, style = MaterialTheme.typography.headlineSmall)
            Text(lab.brief, style = MaterialTheme.typography.bodyLarge)
            Text(lab.task, style = MaterialTheme.typography.bodyMedium)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Text(
                    strings.labCompileHint(lab.isCapstone),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (lab.testCases.isNotEmpty()) {
                Text(strings.labTestCasesHeader(lab.testCases.size), style = MaterialTheme.typography.titleMedium)
                lab.testCases.forEach { tc ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(tc.name, style = MaterialTheme.typography.titleSmall)
                            Text(tc.inputDesc, style = MaterialTheme.typography.bodyMedium)
                            if (tc.input.isNotBlank()) {
                                Text("${strings.labSimulatedInput}${tc.input}", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("${strings.labExpected}${tc.expected}", fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            Text(strings.labEditor, style = MaterialTheme.typography.titleMedium)
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
                            store.recordLabResult(day.id, lab, eval)
                            if (eval.passed) {
                                store.markLab(lab.id)
                                val lessonsDone = day.lessons.count { progress.completedLessons.contains(it.id) } == day.lessons.size
                                val quizDone = progress.completedQuizzes.contains(day.id)
                                if (lessonsDone && quizDone) {
                                    store.checkInCourseDay(day.id)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                ) { Text(strings.labRunCheck) }
                OutlinedButton(
                    onClick = { code = lab.starterCode; result = null },
                    modifier = Modifier.height(48.dp),
                ) { Text(strings.reset) }
            }
            result?.let { eval ->
                SimulatedResultCard(eval)
            }
            if (lab.isCapstone) {
                DijkstraBoard(unlocked = result?.passed == true || progress.completedLabs.contains(lab.id))
            }
            Text(strings.labHintsTitle(attempts), style = MaterialTheme.typography.titleMedium)
            lab.hints.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
            Text(strings.labExpectedOutput, style = MaterialTheme.typography.titleMedium)
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
                    when {
                        showSolution -> strings.labSolutionShown
                        canReveal -> strings.labRevealSolution
                        else -> strings.labRevealAfterAttempts(lab.attemptsBeforeReveal - attempts)
                    },
                )
            }
            if (showSolution) {
                CodePane("c", lab.solutionCode, strings.labReferenceSolution)
            }
        }
    }
}

@Composable
private fun SimulatedResultCard(eval: LabEvaluation) {
    val strings = rememberStrings()
    val container = when {
        eval.passed -> MaterialTheme.colorScheme.primaryContainer
        eval.hasPartialCredit -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
    Card(colors = CardDefaults.cardColors(containerColor = container)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                when {
                    eval.passed -> strings.labSimPassed()
                    eval.hasPartialCredit -> strings.labSimPartial(eval.scorePercent)
                    else -> strings.labSimFailed(eval.scorePercent)
                },
                style = MaterialTheme.typography.titleMedium,
            )
            if (eval.totalCases > 0) {
                Text(strings.labCasesProgress(eval.passedCases, eval.totalCases))
                LinearProgressIndicator(
                    progress = { eval.passedCases / eval.totalCases.coerceAtLeast(1).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (eval.totalChecks > 0) {
                Text(strings.labChecksProgress(eval.passedChecks, eval.totalChecks))
                LinearProgressIndicator(
                    progress = { eval.passedChecks / eval.totalChecks.coerceAtLeast(1).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            eval.simulatedOutput?.let {
                Text("${strings.labSimStdout}\n$it", fontFamily = FontFamily.Monospace)
            }
            if (!eval.passed) {
                Text(
                    if (eval.hasPartialCredit) strings.labPartialHint else strings.labFailHint,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            eval.caseOutcomes.forEach { outcome ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        if (outcome.passed) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
                        contentDescription = if (outcome.passed) strings.passed else strings.notPassed,
                        tint = if (outcome.passed) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                    )
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (outcome.passed) strings.labCasePassed(outcome.name)
                            else strings.labCaseFailed(outcome.name),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        if (!outcome.passed) {
                            Text(
                                strings.labCaseCompare(
                                    outcome.expected.trim(),
                                    outcome.actual?.trim() ?: strings.labNoOutput,
                                ),
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            outcome.hint?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }
            }
            eval.checkOutcomes.forEach { outcome ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        if (outcome.passed) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
                        contentDescription = if (outcome.passed) strings.passed else strings.notPassed,
                        tint = if (outcome.passed) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                    )
                    Text(
                        if (outcome.passed) strings.labCheckPassed(outcome.id)
                        else strings.labCheckFailed(outcome.id, outcome.failHint),
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
    val strings = rememberStrings()
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(strings.labGradeHintTitle(hint.title), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.error)
            Text(hint.detail, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
