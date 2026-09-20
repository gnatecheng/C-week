package com.py2c.week.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.py2c.week.data.VideoDemo
import com.py2c.week.ui.FullscreenVideoActivity
import kotlinx.coroutines.delay

internal val DefaultLessonSpeed = 0.75f
private val SpeedChoices = listOf(0.5f, 0.75f, 1.0f, 1.25f)

@OptIn(UnstableApi::class)
@Composable
fun VsCodeVideoPlayer(
    demo: VideoDemo,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val player = remember(demo.file) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse("asset:///${demo.file}")))
            repeatMode = Player.REPEAT_MODE_OFF
            setPlaybackSpeed(DefaultLessonSpeed)
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(player) {
        onDispose { player.release() }
    }

    var speed by remember(demo.id) { mutableFloatStateOf(DefaultLessonSpeed) }
    LaunchedEffect(player, speed) {
        player.setPlaybackSpeed(speed)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val pos = result.data?.getLongExtra(FullscreenVideoActivity.EXTRA_POSITION, -1L) ?: -1L
        if (pos >= 0L) player.seekTo(pos)
        val returned = result.data?.getFloatExtra(FullscreenVideoActivity.EXTRA_SPEED, speed) ?: speed
        speed = returned
        player.setPlaybackSpeed(returned)
        player.playWhenReady = true
    }

    fun enterFullscreen() {
        val pos = player.currentPosition
        player.pause()
        launcher.launch(FullscreenVideoActivity.intent(context, demo.id, pos, speed))
    }

    var active by remember(demo.id) { mutableIntStateOf(0) }
    var positionMs by remember(demo.id) { mutableLongStateOf(0L) }
    var durationMs by remember(demo.id) { mutableLongStateOf(0L) }
    LaunchedEffect(player, demo.id) {
        while (true) {
            positionMs = player.currentPosition.coerceAtLeast(0L)
            val dur = player.duration
            if (dur > 0L) durationMs = dur
            active = demo.captions.indexOfLast { it.atMs <= positionMs }.coerceAtLeast(0)
            delay(120)
        }
    }

    val currentCaption = demo.captions.getOrNull(active)?.text.orEmpty()

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column {
            Text(demo.title, style = MaterialTheme.typography.titleLarge)
            Text(
                demo.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        useController = true
                        controllerAutoShow = true
                        controllerShowTimeoutMs = 4_000
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        this.player = player
                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                        setFullscreenButtonClickListener { _ -> enterFullscreen() }
                        contentDescription = "VS Code 实机演示：${demo.title}"
                    }
                },
                update = { view ->
                    view.player = player
                    view.setFullscreenButtonClickListener { _ -> enterFullscreen() }
                },
                modifier = Modifier.fillMaxSize(),
            )
            if (currentCaption.isNotEmpty()) {
                Text(
                    currentCaption,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xCC0B1416))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                )
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "语速（默认 0.75×，便于看清操作）",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                formatClock(positionMs) + " / " + formatClock(durationMs),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SpeedChoices.forEach { choice ->
                FilterChip(
                    selected = speed == choice,
                    onClick = { speed = choice },
                    label = { Text(speedLabel(choice)) },
                )
            }
        }
        Text(
            "操作清单随进度高亮（第 ${active + 1} / ${demo.captions.size} 步）",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        demo.captions.forEachIndexed { i, cap ->
            val on = i == active
            val passed = i < active
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            on -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surface
                        },
                    )
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IconCheck(done = passed || on, active = on)
                Column(Modifier.weight(1f)) {
                    Text(
                        if (on) "正在讲解 · ${formatClock(cap.atMs)}" else formatClock(cap.atMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (on) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(cap.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun IconCheck(done: Boolean, active: Boolean) {
    androidx.compose.material3.Icon(
        if (done) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
        contentDescription = null,
        tint = when {
            active -> MaterialTheme.colorScheme.primary
            done -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        },
    )
}

private fun speedLabel(speed: Float): String = when (speed) {
    0.5f -> "0.5×"
    0.75f -> "0.75×"
    1.0f -> "1.0×"
    else -> "1.25×"
}

private fun formatClock(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val total = (ms / 1000L).toInt()
    return "%d:%02d".format(total / 60, total % 60)
}
