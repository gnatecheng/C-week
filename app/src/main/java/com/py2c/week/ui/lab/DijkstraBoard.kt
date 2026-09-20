package com.py2c.week.ui.lab

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private const val INF = 1_000_000_000

private data class DNode(val id: Int, val x: Float, val y: Float)
private data class DEdge(val u: Int, val v: Int, val w: Int)

private val NODES = listOf(
    DNode(0, 0.18f, 0.28f),
    DNode(2, 0.82f, 0.28f),
    DNode(1, 0.18f, 0.78f),
    DNode(3, 0.82f, 0.78f),
)

private val EDGES = listOf(
    DEdge(0, 1, 4),
    DEdge(0, 2, 1),
    DEdge(2, 1, 1),
    DEdge(1, 3, 1),
    DEdge(2, 3, 5),
)

/** Shortest path on the course graph: 0 → 2 → 1 → 3, cost 3. */
private val PATH_EDGES = setOf(0 to 2, 2 to 1, 1 to 3)
private val PATH_ORDER = listOf(0, 2, 1, 3)

private data class DijkstraFrame(
    val caption: String,
    val dist: List<Int>,
    val used: Set<Int>,
    val current: Int?,
    val highlight: Pair<Int, Int>?,
)

private val FRAMES: List<DijkstraFrame> = listOf(
    DijkstraFrame("起点盖章前：只有 0 的距离是 0，其余是 INF。", listOf(0, INF, INF, INF), emptySet(), null, null),
    DijkstraFrame("选出未确定里 dist 最小的点 0，准备松弛出边。", listOf(0, INF, INF, INF), emptySet(), 0, null),
    DijkstraFrame("松弛 0→1 权 4：dist[1] 从 INF 变成 4。", listOf(0, 4, INF, INF), setOf(0), 0, 0 to 1),
    DijkstraFrame("松弛 0→2 权 1：dist[2] 变成 1。点 0 确定。", listOf(0, 4, 1, INF), setOf(0), 0, 0 to 2),
    DijkstraFrame("下一轮选 2（比还是 4 的点 1 更近）。", listOf(0, 4, 1, INF), setOf(0), 2, null),
    DijkstraFrame("松弛 2→1：1+1=2，比直边 4 更短，dist[1]=2。", listOf(0, 2, 1, INF), setOf(0, 2), 2, 2 to 1),
    DijkstraFrame("松弛 2→3：1+5=6，dist[3]=6。点 2 确定。", listOf(0, 2, 1, 6), setOf(0, 2), 2, 2 to 3),
    DijkstraFrame("选出点 1（dist 2 小于点 3 的 6）。", listOf(0, 2, 1, 6), setOf(0, 2), 1, null),
    DijkstraFrame("松弛 1→3：2+1=3，比 6 更短，dist[3]=3。", listOf(0, 2, 1, 3), setOf(0, 2, 1), 1, 1 to 3),
    DijkstraFrame("最后确定点 3。最短路 0→2→1→3，距离 0 2 1 3。", listOf(0, 2, 1, 3), setOf(0, 1, 2, 3), 3, null),
)

@Composable
fun DijkstraBoard(
    unlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    var step by remember { mutableIntStateOf(if (unlocked) FRAMES.lastIndex else 0) }
    var playing by remember { mutableStateOf(false) }
    val frame = FRAMES[step.coerceIn(0, FRAMES.lastIndex)]

    LaunchedEffect(unlocked) {
        step = if (unlocked) FRAMES.lastIndex else 0
        playing = false
    }

    LaunchedEffect(playing, step, unlocked) {
        if (!playing || !unlocked) return@LaunchedEffect
        if (step >= FRAMES.lastIndex) {
            playing = false
            return@LaunchedEffect
        }
        delay(900)
        if (playing) step++
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("走格子 / 看路径", color = Color(0xFF5EEAD4), style = MaterialTheme.typography.titleMedium)
            Text(
                if (unlocked) {
                    "通过检查后可以逐步看 Dijkstra 怎样把距离钉死。高亮边是当前松弛，青边是最终最短路。"
                } else {
                    "补全松弛并通过检查后，这里会播放 0→2→1→3 怎么把 dist 从 INF 写成 0 2 1 3。"
                },
                color = Color(0xFFCBD5E1),
                style = MaterialTheme.typography.bodyMedium,
            )
            GraphCanvas(frame = frame, showFinalPath = unlocked && step == FRAMES.lastIndex)
            PathTiles(frame = frame, unlocked = unlocked)
            Text(
                "步骤 ${step + 1}/${FRAMES.size}　${frame.caption}",
                color = Color(0xFFFDE68A),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "dist = ${frame.dist.joinToString(" ") { if (it >= INF) "INF" else it.toString() }}",
                color = Color(0xFFE2E8F0),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { playing = false; step = (step - 1).coerceAtLeast(0) }, enabled = unlocked) {
                    Icon(Icons.Outlined.SkipPrevious, contentDescription = "上一步", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        if (!unlocked) return@IconButton
                        if (step >= FRAMES.lastIndex) step = 0
                        playing = !playing
                    },
                    enabled = unlocked,
                ) {
                    Icon(
                        if (playing) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                        contentDescription = if (playing) "暂停" else "播放",
                        tint = Color.White,
                    )
                }
                IconButton(
                    onClick = { playing = false; step = (step + 1).coerceAtMost(FRAMES.lastIndex) },
                    enabled = unlocked,
                ) {
                    Icon(Icons.Outlined.SkipNext, contentDescription = "下一步", tint = Color.White)
                }
                IconButton(
                    onClick = { playing = false; step = 0 },
                    enabled = unlocked,
                ) {
                    Icon(Icons.Outlined.Replay, contentDescription = "从头", tint = Color.White)
                }
            }
            if (!unlocked) {
                FilledTonalButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                    Text("先通过模拟检查，再解锁走格子")
                }
            }
        }
    }
}

@Composable
private fun PathTiles(frame: DijkstraFrame, unlocked: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        PATH_ORDER.forEachIndexed { idx, node ->
            val d = frame.dist[node]
            val used = node in frame.used
            val current = frame.current == node
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .background(
                        when {
                            current -> Color(0xFFFBBF24)
                            used -> Color(0xFF0F766E)
                            else -> Color(0xFF1E293B)
                        },
                        RoundedCornerShape(10.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("格 $node", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    Text(
                        if (!unlocked && idx > 0 && d >= INF) "？" else if (d >= INF) "INF" else d.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
            if (idx < PATH_ORDER.lastIndex) {
                Box(
                    modifier = Modifier.height(56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("→", color = Color(0xFF94A3B8))
                }
            }
        }
    }
}

@Composable
private fun GraphCanvas(frame: DijkstraFrame, showFinalPath: Boolean) {
    val pulse by animateFloatAsState(targetValue = if (frame.current != null) 1f else 0.4f, label = "pulse")
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color(0xFF020617), RoundedCornerShape(12.dp)),
    ) {
        val pos = NODES.associate { it.id to Offset(it.x * size.width, it.y * size.height) }
        EDGES.forEach { e ->
            val a = pos.getValue(e.u)
            val b = pos.getValue(e.v)
            val onPath = showFinalPath && (e.u to e.v) in PATH_EDGES
            val relaxing = frame.highlight == (e.u to e.v)
            val color = when {
                relaxing -> Color(0xFFFBBF24)
                onPath -> Color(0xFF5EEAD4)
                else -> Color(0xFF475569)
            }
            val width = when {
                relaxing -> 10f
                onPath -> 8f
                else -> 4f
            }
            drawArrow(a, b, color, width)
            val mid = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f - 16f)
            drawLabel("${e.w}", mid.x, mid.y, Color(0xFF93C5FD), 28f)
        }
        NODES.forEach { n ->
            val o = pos.getValue(n.id)
            val d = frame.dist[n.id]
            val used = n.id in frame.used
            val current = frame.current == n.id
            val fill = when {
                current -> Color(0xFFFBBF24)
                used -> Color(0xFF0F766E)
                else -> Color(0xFF1E3A5F)
            }
            val r = if (current) 34f + 4f * pulse else 32f
            drawCircle(fill, r, o)
            drawCircle(Color.White.copy(alpha = 0.7f), r, o, style = Stroke(3f))
            drawLabel("${n.id}", o.x, o.y - 6f, Color.White, 34f)
            val dText = if (d >= INF) "∞" else d.toString()
            drawLabel("d=$dText", o.x, o.y + 22f, Color(0xFFFEF3C7), 24f)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArrow(
    from: Offset,
    to: Offset,
    color: Color,
    stroke: Float,
) {
    val v = Offset(to.x - from.x, to.y - from.y)
    val len = kotlin.math.sqrt(v.x * v.x + v.y * v.y).coerceAtLeast(1f)
    val ux = v.x / len
    val uy = v.y / len
    val start = Offset(from.x + ux * 36f, from.y + uy * 36f)
    val end = Offset(to.x - ux * 36f, to.y - uy * 36f)
    drawLine(color, start, end, strokeWidth = stroke, cap = StrokeCap.Round)
    val path = Path().apply {
        moveTo(end.x, end.y)
        lineTo(end.x - ux * 16f - uy * 8f, end.y - uy * 16f + ux * 8f)
        lineTo(end.x - ux * 16f + uy * 8f, end.y - uy * 16f - ux * 8f)
        close()
    }
    drawPath(path, color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLabel(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    textSize: Float,
) {
    drawContext.canvas.nativeCanvas.drawText(
        text,
        x,
        y,
        android.graphics.Paint().apply {
            this.color = android.graphics.Color.argb(
                (color.alpha * 255).toInt(),
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt(),
            )
            this.textSize = textSize
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        },
    )
}
