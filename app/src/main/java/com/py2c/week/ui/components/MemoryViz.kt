package com.py2c.week.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun MemoryViz(variant: String, modifier: Modifier = Modifier) {
    val caption = when (variant) {
        "pointer_basic" -> "动画：p 里存的是 x 的地址。解引用 *p 就是顺着箭头改 x。"
        "array_decay" -> "数组 a[3] 在函数参数里变成指向 a[0] 的指针。"
        "cstring" -> "\"hi\" 实际是 h i \\\\0 三格。少了最后一格，%s 就会越界。"
        "heap" -> "栈上的 p 指向堆上的 int。free 后箭头必须作废。"
        else -> "内存示意"
    }
    val t by rememberInfiniteTransition(label = "mv").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "t",
    )
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                .padding(8.dp),
        ) {
            when (variant) {
                "pointer_basic" -> drawPointer(t)
                "array_decay" -> drawArray(t)
                "cstring" -> drawCString(t)
                else -> drawHeap(t)
            }
        }
        Text(caption, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendDot(Color(0xFF5EEAD4), "变量格子")
            LegendDot(Color(0xFFFBBF24), "指针 / 地址")
            LegendDot(Color(0xFF93C5FD), "数据")
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Text("● $label", color = color, style = MaterialTheme.typography.labelSmall)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPointer(t: Float) {
    val box = 88f
    val y = size.height / 2 - box / 2
    drawRoundRect(Color(0xFF164E63), Offset(40f, y), Size(box, box), CornerRadius(12f))
    drawLabel("x = 5", 40f + box / 2, y + box / 2)
    val px = 40f + box + 36f + t * 12f
    drawRoundRect(Color(0xFF854D0E), Offset(px, y), Size(box, box), CornerRadius(12f))
    drawLabel("p", px + box / 2, y + 28f)
    drawLabel("&x", px + box / 2, y + box / 2 + 10f)
    val start = Offset(px, y + box / 2)
    val end = Offset(40f + box, y + box / 2)
    drawLine(Color(0xFFFBBF24), start, end, strokeWidth = 6f)
    drawCircle(Color(0xFFFBBF24), 8f, end)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArray(t: Float) {
    val cell = size.width / 6f
    val y = 36f
    listOf("10", "20", "30").forEachIndexed { i, v ->
        val x = 24f + i * (cell + 8f)
        drawRoundRect(Color(0xFF1E3A5F), Offset(x, y), Size(cell, 64f), CornerRadius(10f))
        drawLabel("a[$i]=$v", x + cell / 2, y + 32f)
    }
    val arrowX = 24f + t * (cell + 8f) * 2
    drawRoundRect(Color(0xFF854D0E), Offset(24f, y + 88f), Size(cell, 48f), CornerRadius(8f))
    drawLabel("ptr", 24f + cell / 2, y + 112f)
    drawLine(Color(0xFFFBBF24), Offset(24f + cell / 2, y + 88f), Offset(arrowX + cell / 2, y + 64f), 5f)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCString(t: Float) {
    val labels = listOf("h", "i", "\\0", "??")
    val cell = size.width / 5.5f
    labels.forEachIndexed { i, v ->
        val x = 20f + i * (cell + 6f)
        val col = if (i == 2) Color(0xFF14532D) else if (i == 3) Color(0xFF7F1D1D).copy(alpha = 0.35f + 0.4f * t) else Color(0xFF1E3A5F)
        drawRoundRect(col, Offset(x, 48f), Size(cell, 70f), CornerRadius(10f))
        drawLabel(v, x + cell / 2, 83f)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHeap(t: Float) {
    drawRoundRect(Color(0xFF1E293B), Offset(20f, 20f), Size(size.width * 0.38f, size.height - 40f), CornerRadius(12f))
    drawLabel("STACK", 20f + size.width * 0.19f, 40f)
    drawRoundRect(Color(0xFF164E63), Offset(40f, 70f), Size(size.width * 0.28f, 50f), CornerRadius(8f))
    drawLabel("int *p", 40f + size.width * 0.14f, 95f)
    drawRoundRect(Color(0xFF1E293B), Offset(size.width * 0.52f, 20f), Size(size.width * 0.44f, size.height - 40f), CornerRadius(12f), style = Stroke(4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))))
    drawLabel("HEAP", size.width * 0.74f, 40f)
    val glow = 0.5f + 0.5f * kotlin.math.abs(t * 2 - 1)
    drawRoundRect(Color(0xFF1D4ED8).copy(alpha = glow), Offset(size.width * 0.58f, 70f), Size(size.width * 0.28f, 50f), CornerRadius(8f))
    drawLabel("int 0", size.width * 0.72f, 95f)
    drawLine(Color(0xFFFBBF24), Offset(40f + size.width * 0.28f, 95f), Offset(size.width * 0.58f, 95f), 5f)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLabel(text: String, x: Float, y: Float) {
    drawContext.canvas.nativeCanvas.drawText(
        text,
        x,
        y,
        android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 28f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        },
    )
}
