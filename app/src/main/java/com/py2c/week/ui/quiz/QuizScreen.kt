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
import com.py2c.week.data.hintCategory
import com.py2c.week.data.verdict
import com.py2c.week.data.wrongReason
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    day: CourseDay,
    questions: List<QuizQuestion> = day.quiz,
    alreadyDone: Boolean,
    lastScore: Int?,
    redoMode: Boolean = false,
    onBack: () -> Unit,
    onSubmit: suspend (score: Int, answers: Map<String, Int>) -> Unit,
) {
    val answers = remember { mutableStateMapOf<String, Int>() }
    var submitted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val score = questions.count { q -> answers[q.id] == q.correctIndex }
    val allCorrect = submitted && score == questions.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (redoMode) "错题重练 · 第 ${day.id} 天" else "第 ${day.id} 天测验") },
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
            if (redoMode) {
                Text(
                    "只重练错题本里的题目。全部选对会标记为已订正。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else if (alreadyDone && lastScore != null && !submitted) {
                Text("上次得分 $lastScore / ${day.quiz.size}。可以重做，新分数会覆盖。", style = MaterialTheme.typography.bodyMedium)
            }
            if (submitted) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (allCorrect) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.tertiaryContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("答对 $score / ${questions.size}", style = MaterialTheme.typography.titleLarge)
                        Text(
                            when {
                                allCorrect && redoMode -> "全部正确，已从待订正列表清掉（仍可在「已订正」里看到）。"
                                allCorrect -> "全部正确。错因栏不会出现——你已经选对了。"
                                redoMode -> "还有错题。选对的会立刻标记已订正，错的会留在错题本。"
                                else -> "错题已记入错题本。可到「错题本」按天重练；下面仍有「判断 / 你选了 / 错因 / 正确」。"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            questions.forEachIndexed { idx, q ->
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
                    scope.launch { onSubmit(score, answers.toMap()) }
                },
                enabled = !submitted && answers.size == questions.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(
                    if (submitted) "已提交 · $score / ${questions.size}"
                    else if (redoMode) "提交重练"
                    else "提交测验",
                )
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
                ) { Text(if (redoMode) "再练一次" else "再测一次") }
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
                            Text("类别：${question.hintCategory()}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
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
