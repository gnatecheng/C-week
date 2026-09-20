package com.py2c.week.ui.lesson

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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.py2c.week.data.CalloutKind
import com.py2c.week.data.ContentBlock
import com.py2c.week.data.CourseDay
import com.py2c.week.data.Lesson
import com.py2c.week.data.VideoDemo
import com.py2c.week.data.VsCodeDemo
import com.py2c.week.ui.components.CodePane
import com.py2c.week.ui.components.MemoryViz
import com.py2c.week.ui.components.VsCodeVideoPlayer
import com.py2c.week.ui.components.VsCodeWalkthrough
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    day: CourseDay,
    lesson: Lesson,
    demos: Map<String, VsCodeDemo>,
    videos: Map<String, VideoDemo>,
    completed: Boolean,
    onBack: () -> Unit,
    onOpenDemo: (String) -> Unit,
    onMarkDone: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson.title) },
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
            Text("第 ${day.id} 天 · ${lesson.minutes} 分钟", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            lesson.blocks.forEach { block ->
                when (block) {
                    is ContentBlock.Heading -> Text(block.text, style = MaterialTheme.typography.headlineSmall)
                    is ContentBlock.Paragraph -> Text(block.text, style = MaterialTheme.typography.bodyLarge)
                    is ContentBlock.Bullets -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        block.items.forEach { Text("• $it", style = MaterialTheme.typography.bodyLarge) }
                    }
                    is ContentBlock.Callout -> CalloutCard(block)
                    is ContentBlock.Example -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(block.title, style = MaterialTheme.typography.titleMedium)
                        CodePane(block.language, block.source)
                        Text(block.note, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    is ContentBlock.Code -> CodePane(block.language, block.source, block.caption)
                    is ContentBlock.VsCode -> {
                        val video = videos[block.demoId]
                        val schematic = demos[block.demoId]
                        if (video != null) {
                            VsCodeVideoPlayer(video)
                        } else if (schematic != null) {
                            Text("实机录像未打包，暂时使用示意图。", style = MaterialTheme.typography.bodySmall)
                            VsCodeWalkthrough(schematic)
                            TextButton(onClick = { onOpenDemo(block.demoId) }) { Text("查看示意图") }
                        }
                    }
                    is ContentBlock.MemoryViz -> MemoryViz(block.variant)
                }
            }
            Button(
                onClick = { scope.launch { onMarkDone(); onBack() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = true,
            ) {
                Text(if (completed) "已标记学完，返回" else "学完了，记入进度")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScreen(
    demo: VsCodeDemo,
    video: VideoDemo?,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VS Code 实机演示") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (video != null) {
                VsCodeVideoPlayer(video)
            } else {
                VsCodeWalkthrough(demo)
            }
        }
    }
}

@Composable
private fun CalloutCard(block: ContentBlock.Callout) {
    val (icon, container) = when (block.kind) {
        CalloutKind.TIP -> Icons.Outlined.Lightbulb to MaterialTheme.colorScheme.secondaryContainer
        CalloutKind.WARN -> Icons.Outlined.PriorityHigh to MaterialTheme.colorScheme.errorContainer
        CalloutKind.KEY -> Icons.Outlined.Info to MaterialTheme.colorScheme.primaryContainer
    }
    Card(colors = CardDefaults.cardColors(containerColor = container), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            Icon(icon as ImageVector, contentDescription = null)
            Column {
                Text(block.title, style = MaterialTheme.typography.titleMedium)
                Text(block.text, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
