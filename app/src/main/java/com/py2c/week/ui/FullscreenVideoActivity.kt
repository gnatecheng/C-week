package com.py2c.week.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.py2c.week.Py2CApplication
import com.py2c.week.R
import com.py2c.week.data.VideoDemo

/**
 * Dedicated landscape activity that owns the window. The PlayerView is MATCH_PARENT
 * on the Activity content view — not a Compose Dialog inside the lesson column.
 */
class FullscreenVideoActivity : ComponentActivity() {

    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var demo: VideoDemo? = null
    private val captionHandler = Handler(Looper.getMainLooper())
    private lateinit var captionText: TextView
    private lateinit var captionMeta: TextView

    private val captionTick = object : Runnable {
        override fun run() {
            val d = demo ?: return
            val pos = player?.currentPosition ?: 0L
            val index = d.captions.indexOfLast { it.atMs <= pos }.coerceAtLeast(0)
            captionText.text = d.captions.getOrNull(index)?.text.orEmpty()
            captionMeta.text = getString(
                R.string.fullscreen_caption_meta,
                index + 1,
                d.captions.size,
            )
            captionHandler.postDelayed(this, 200)
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        hideSystemBars()

        val demoId = intent.getStringExtra(EXTRA_DEMO_ID)
        val loaded = demoId?.let { (application as Py2CApplication).container.videos[it] }
        if (loaded == null) {
            finish()
            return
        }
        demo = loaded
        val startMs = savedInstanceState?.getLong(EXTRA_POSITION)
            ?: intent.getLongExtra(EXTRA_POSITION, 0L).coerceAtLeast(0L)
        val startSpeed = intent.getFloatExtra(EXTRA_SPEED, 0.75f).let { if (it > 0f) it else 0.75f }

        setContentView(R.layout.activity_fullscreen_video)

        captionText = findViewById(R.id.caption_text)
        captionMeta = findViewById(R.id.caption_meta)
        findViewById<View>(R.id.exit_fullscreen).setOnClickListener { finishWithPosition() }

        val exo = ExoPlayer.Builder(this).build().apply {
            setMediaItem(MediaItem.fromUri(android.net.Uri.parse("asset:///${loaded.file}")))
            setPlaybackSpeed(startSpeed)
            prepare()
            seekTo(startMs)
            playWhenReady = true
        }
        player = exo

        val view = findViewById<PlayerView>(R.id.fullscreen_player).apply {
            useController = true
            controllerShowTimeoutMs = 3_200
            controllerHideOnTouch = true
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
            setFullscreenButtonClickListener { _ -> finishWithPosition() }
            setControllerVisibilityListener(
                PlayerView.ControllerVisibilityListener { _ -> hideSystemBars() },
            )
            player = exo
            contentDescription = loaded.title
        }
        playerView = view

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishWithPosition()
                }
            },
        )
    }

    override fun onStart() {
        super.onStart()
        playerView?.player = player
        player?.playWhenReady = true
        captionHandler.post(captionTick)
        hideSystemBars()
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    override fun onStop() {
        captionHandler.removeCallbacks(captionTick)
        player?.pause()
        playerView?.player = null
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(EXTRA_POSITION, player?.currentPosition ?: 0L)
    }

    override fun onDestroy() {
        captionHandler.removeCallbacks(captionTick)
        playerView?.player = null
        player?.release()
        player = null
        super.onDestroy()
    }

    private fun finishWithPosition() {
        val pos = player?.currentPosition ?: 0L
        val speed = player?.playbackParameters?.speed ?: 0.75f
        setResult(
            RESULT_OK,
            Intent()
                .putExtra(EXTRA_POSITION, pos)
                .putExtra(EXTRA_SPEED, speed),
        )
        finish()
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT < 30) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        }
    }

    companion object {
        const val EXTRA_DEMO_ID = "demo_id"
        const val EXTRA_POSITION = "position_ms"
        const val EXTRA_SPEED = "playback_speed"

        fun intent(context: Context, demoId: String, positionMs: Long, speed: Float = 0.75f): Intent {
            return Intent(context, FullscreenVideoActivity::class.java)
                .putExtra(EXTRA_DEMO_ID, demoId)
                .putExtra(EXTRA_POSITION, positionMs)
                .putExtra(EXTRA_SPEED, speed)
        }
    }
}
