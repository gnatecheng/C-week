package com.py2c.week.ui.components

/** Schematic VS Code mock. Primary teaching UI is [VsCodeVideoPlayer] with real recordings. */

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AdsClick
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.py2c.week.data.PointerTarget
import com.py2c.week.data.VsActivity
import com.py2c.week.data.VsCodeDemo
import com.py2c.week.data.VsCodeStep
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val VsBg = Color(0xFF1E1E1E)
private val VsSide = Color(0xFF252526)
private val VsAct = Color(0xFF333333)
private val VsAccent = Color(0xFF007ACC)
private val VsText = Color(0xFFD4D4D4)
private val VsDim = Color(0xFF9DA5B4)
private val VsGreen = Color(0xFF3FA266)

@Composable
fun VsCodeWalkthrough(
    demo: VsCodeDemo,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    var index by remember(demo.id) { mutableIntStateOf(0) }
    var playing by remember(demo.id) { mutableStateOf(true) }
    val step = demo.steps[index.coerceIn(0, demo.steps.lastIndex)]

    LaunchedEffect(index, playing, demo.id) {
        if (!playing) return@LaunchedEffect
        delay(step.durationMs.toLong())
        if (index < demo.steps.lastIndex) index++ else playing = false
    }

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (!compact) {
            Text(demo.title, style = MaterialTheme.typography.titleLarge)
            Text(demo.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = VsBg,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 300.dp else 360.dp),
        ) {
            FakeVsCode(step)
        }
        LinearProgressIndicator(
            progress = { (index + 1f) / demo.steps.size },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "步骤 ${index + 1} / ${demo.steps.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(step.caption, style = MaterialTheme.typography.bodyLarge)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    playing = false
                    index = (index - 1).coerceAtLeast(0)
                },
            ) { Icon(Icons.Outlined.SkipPrevious, contentDescription = "上一步") }
            FilledTonalButton(onClick = { playing = !playing }) {
                Icon(if (playing) Icons.Outlined.Pause else Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (playing) "暂停" else "播放")
            }
            IconButton(
                onClick = {
                    if (index < demo.steps.lastIndex) {
                        index++
                        playing = true
                    } else {
                        playing = false
                    }
                },
            ) { Icon(Icons.Outlined.SkipNext, contentDescription = "下一步") }
        }
    }
}

@Composable
private fun FakeVsCode(step: VsCodeStep) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "p",
    )
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val w = maxWidth
        val h = maxHeight
        val target = pointerOffset(step.pointer, w, h)
        val x = remember { Animatable(target.first.value) }
        val y = remember { Animatable(target.second.value) }
        LaunchedEffect(step) {
            x.animateTo(target.first.value, tween(450, easing = FastOutSlowInEasing))
            y.animateTo(target.second.value, tween(450, easing = FastOutSlowInEasing))
        }
        Column(Modifier.fillMaxSize()) {
            TitleBar(step)
            Row(Modifier.weight(1f)) {
                ActivityBar(step.activity)
                when (step.activity) {
                    VsActivity.EXTENSIONS -> ExtensionsPane(step)
                    else -> ExplorerPane(step)
                }
                EditorPane(step, Modifier.weight(1f))
            }
            if (step.terminalLines.isNotEmpty()) {
                TerminalPane(step)
            }
            StatusBar(step)
        }
        val px = with(density) { x.value.dp.toPx().roundToInt() }
        val py = with(density) { y.value.dp.toPx().roundToInt() }
        Box(
            Modifier
                .offset { IntOffset(px, py) }
                .size(28.dp)
                .alpha(if (step.click) pulse else 0.9f),
        ) {
            Icon(
                Icons.Outlined.AdsClick,
                contentDescription = "当前点击/焦点",
                tint = Color(0xFFFFD54F),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun TitleBar(step: VsCodeStep) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(Color(0xFF3C3C3C))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Traffic()
        Text("File", color = if (step.menuLabel != null) Color.White else VsDim, fontSize = 11.sp)
        Text("Terminal", color = VsDim, fontSize = 11.sp)
        if (step.menuLabel != null) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(VsAccent)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(step.menuLabel, color = Color.White, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.weight(1f))
        Text(step.tabName.ifEmpty { "VS Code" }, color = VsText, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun Traffic() {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF27C93F)))
    }
}

@Composable
private fun ActivityBar(active: VsActivity) {
    Column(
        Modifier
            .width(40.dp)
            .fillMaxSize()
            .background(VsAct),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        ActIcon(Icons.Filled.Folder, active == VsActivity.EXPLORER)
        ActIcon(Icons.Filled.Search, active == VsActivity.SEARCH)
        ActIcon(Icons.Filled.Extension, active == VsActivity.EXTENSIONS)
        ActIcon(Icons.Filled.PlayArrow, active == VsActivity.RUN)
        ActIcon(Icons.Filled.BugReport, active == VsActivity.DEBUG)
        Spacer(Modifier.weight(1f))
        ActIcon(Icons.Filled.AccountTree, false)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ActIcon(icon: ImageVector, on: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (on) {
            Box(
                Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .align(Alignment.Start)
                    .background(Color.White),
            )
        }
        Icon(icon, null, tint = if (on) Color.White else VsDim, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ExplorerPane(step: VsCodeStep) {
    Column(
        Modifier
            .width(120.dp)
            .fillMaxSize()
            .background(VsSide)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text("EXPLORER", color = VsDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text("+ 新建文件", color = if (step.pointer == PointerTarget.EXPLORER_NEW) Color.White else VsDim, fontSize = 11.sp)
        step.explorerFiles.forEach { name ->
            val sel = name == step.selectedFile
            Text(
                name,
                color = if (sel) Color.White else VsText,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (sel) Color(0xFF094771) else Color.Transparent)
                    .padding(4.dp),
            )
        }
    }
}

@Composable
private fun ExtensionsPane(step: VsCodeStep) {
    Column(
        Modifier
            .width(150.dp)
            .fillMaxSize()
            .background(VsSide)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("EXTENSIONS", color = VsDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF3C3C3C))
                .padding(6.dp),
        ) {
            Text(step.extensionQuery ?: "Search", color = VsText, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, if (step.pointer == PointerTarget.EXTENSION_INSTALL) VsAccent else Color.Transparent, RoundedCornerShape(6.dp))
                .padding(6.dp),
        ) {
            Text("C/C++", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("Microsoft", color = VsDim, fontSize = 10.sp)
            Box(
                Modifier
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (step.statusText.contains("已安装")) VsGreen else VsAccent)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    if (step.statusText.contains("已安装")) "Installed" else "Install",
                    color = Color.White,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun EditorPane(step: VsCodeStep, modifier: Modifier) {
    Column(modifier.background(VsBg)) {
        if (step.tabName.isNotEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D2D2D))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(step.tabName, color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
        Column(Modifier.padding(8.dp)) {
            if (step.editorLines.isEmpty() && step.typing != null) {
                Text(step.typing, color = VsText, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            }
            step.editorLines.forEachIndexed { i, line ->
                val bp = step.breakpointLine == i
                val hi = step.highlightLine == i
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(when {
                            hi -> Color(0xFF3A3D41)
                            else -> Color.Transparent
                        })
                        .padding(vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.width(14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (bp) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE51400)))
                        }
                    }
                    Text(
                        "${i + 1}".padStart(2),
                        color = VsDim,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(22.dp),
                    )
                    Text(line, color = VsText, fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun TerminalPane(step: VsCodeStep) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(Color(0xFF1C1C1C))
            .padding(8.dp),
    ) {
        Text("TERMINAL", color = VsDim, fontSize = 10.sp)
        step.terminalLines.takeLast(4).forEach {
            Text(it, color = Color(0xFFD4E157), fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
        }
    }
}

@Composable
private fun StatusBar(step: VsCodeStep) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(22.dp)
            .background(VsAccent)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(step.statusText, color = Color.White, fontSize = 10.sp)
    }
}

private fun pointerOffset(target: PointerTarget, w: Dp, h: Dp): Pair<Dp, Dp> = when (target) {
    PointerTarget.ACTIVITY_EXPLORER -> 10.dp to 48.dp
    PointerTarget.ACTIVITY_EXTENSIONS -> 10.dp to 110.dp
    PointerTarget.ACTIVITY_RUN -> 10.dp to 150.dp
    PointerTarget.ACTIVITY_DEBUG -> 10.dp to 178.dp
    PointerTarget.EXPLORER_NEW -> 48.dp to 52.dp
    PointerTarget.EXPLORER_FILE -> 70.dp to 88.dp
    PointerTarget.EDITOR -> 210.dp to 90.dp
    PointerTarget.GUTTER -> 168.dp to 120.dp
    PointerTarget.TERMINAL -> 200.dp to (h - 70.dp)
    PointerTarget.MENU -> 70.dp to 8.dp
    PointerTarget.STATUS -> 40.dp to (h - 28.dp)
    PointerTarget.EXTENSION_INSTALL -> 90.dp to 130.dp
    PointerTarget.RUN_BUTTON -> 12.dp to 150.dp
}
