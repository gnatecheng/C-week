package com.py2c.week.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.py2c.week.data.CourseDay
import com.py2c.week.data.QuizQuestion
import com.py2c.week.data.verdict
import com.py2c.week.data.wrongReason
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    day: CourseDay,
    alreadyDone: Boolean,
    lastScore: Int?,
    onBack: () -> Unit,
    onSubmit: suspend (Int) -> Unit,
) {
    val answers = remember { mutableStateMapOf<String, Int>() }
    var submitted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val score = day.quiz.count { q -> answers[q.id] == q.correctIndex }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("第 ${day.id} 天测验") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (alreadyDone && lastScore != null && !submitted) {
                Text("上次得分 $lastScore / ${day.quiz.size}。可以重做，新分数会覆盖。", style = MaterialTheme.typography.bodyMedium)
            }
            if (submitted) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (score == day.quiz.size) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.tertiaryContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("答对 $score / ${day.quiz.size}", style = MaterialTheme.typography.titleLarge)
                        Text(
                            if (score == day.quiz.size) "全部正确。错因栏不会出现——你已经选对了。"
                            else "错题下面有「判断 / 你选了 / 错因 / 正确」四行说明，针对你点的那个选项。",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            day.quiz.forEachIndexed { idx, q ->
                QuestionCard(
                    index = idx,
                    question = q,
                    selected = answers[q.id],
                    submitted = submitted,
                    onSelect = { if (!submitted) answers[q.id] = it },
                )
            }
            Button(
                onClick = {
                    submitted = true
                    scope.launch { onSubmit(score) }
                },
                enabled = !submitted && answers.size == day.quiz.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(if (submitted) "已提交 · $score / ${day.quiz.size}" else "提交测验")
            }
            if (submitted) {
                OutlinedButton(
                    onClick = {
                        submitted = false
                        answers.clear()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                ) { Text("再测一次") }
            }
        }
    }
}

@Composable
private fun QuestionCard(
    index: Int,
    question: QuizQuestion,
    selected: Int?,
    submitted: Boolean,
    onSelect: (Int) -> Unit,
) {
    val ok = selected == question.correctIndex
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Q${index + 1}. ${question.prompt}", style = MaterialTheme.typography.titleMedium)
            question.choices.forEachIndexed { ci, choice ->
                val chosen = selected == ci
                val showMark = submitted && (ci == question.correctIndex || chosen)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .selectable(
                            selected = chosen,
                            onClick = { onSelect(ci) },
                            enabled = !submitted,
                            role = Role.RadioButton,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = chosen,
                        onClick = { if (!submitted) onSelect(ci) },
                        enabled = !submitted,
                    )
                    Text(
                        buildString {
                            append(choice)
                            if (showMark && ci == question.correctIndex) append("  ✓")
                            if (showMark && chosen && ci != question.correctIndex) append("  ✗")
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = when {
                            !submitted -> MaterialTheme.colorScheme.onSurface
                            ci == question.correctIndex -> MaterialTheme.colorScheme.primary
                            chosen -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }
            if (submitted && selected != null) {
                val container = if (ok) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                Card(colors = CardDefaults.cardColors(containerColor = container), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(question.verdict(selected), style = MaterialTheme.typography.titleSmall)
                        if (!ok) {
                            Text("你选了：${question.choices[selected]}", style = MaterialTheme.typography.bodyMedium)
                            Text("错因：${question.wrongReason(selected)}", style = MaterialTheme.typography.bodyMedium)
                            Text("正确：${question.choices[question.correctIndex]}", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Text(question.explanation, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
