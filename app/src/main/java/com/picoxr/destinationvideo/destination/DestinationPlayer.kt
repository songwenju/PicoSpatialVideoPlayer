package com.picoxr.destinationvideo.destination

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.platform.containers.OpenStageResult
import com.pico.spatial.ui.platform.containers.StageStyle
import com.pico.spatial.ui.platform.containers.closeStage
import com.pico.spatial.ui.platform.containers.closeWindowContainer
import com.pico.spatial.ui.platform.containers.openStage
import com.pico.spatial.ui.platform.containers.openWindowContainer
import kotlinx.coroutines.launch

/**
 * Displays the selected video in either the playback window or Full Space stage.
 */
@Composable
fun DestinationPlayerWindow(
    videoId: Int?,
    initialPositionMs: Long = 0L,
    isFullSpace: Boolean = false,
) {
    val context = LocalContext.current
    val video = remember(videoId) {
        videoId?.let(DestinationCatalog::findVideo)
    }

    if (video == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "无法打开播放页面",
                style = PicoTheme.typography.bodyMedium,
                color = Color.White,
            )
        }
        return
    }

    DestinationPlayer(
        video = video,
        initialPositionMs = initialPositionMs,
        isFullSpace = isFullSpace,
        onReturnToDetails = {
            DestinationReturnRoute.requestDetail(context, video.id)
            context.openWindowContainer(DESTINATION_HOME_WINDOW_ID)
            if (isFullSpace) {
                closeStage()
            } else {
                context.closeWindowContainer(DESTINATION_PLAYER_WINDOW_ID)
            }
        },
    )
}

/**
 * Plays a catalog item with Media3 and transfers playback position between containers.
 */
@Composable
private fun DestinationPlayer(
    video: DestinationVideo,
    initialPositionMs: Long,
    isFullSpace: Boolean,
    onReturnToDetails: suspend () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val player = remember(video.id, initialPositionMs) {
        ExoPlayer.Builder(context.applicationContext).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            setMediaItem(
                MediaItem.fromUri(videoSourceUri(video, context.packageName)),
                initialPositionMs.coerceAtLeast(video.startSeconds * 1_000L),
            )
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        onDispose {
            player.release()
        }
    }

    fun playbackBundle() = Bundle().apply {
        putInt(DESTINATION_PLAYER_VIDEO_ID, video.id)
        putLong(DESTINATION_PLAYER_POSITION_MS, player.currentPosition.coerceAtLeast(0L))
    }

    val exitPlayback: () -> Unit = {
        player.pause()
        scope.launch {
            onReturnToDetails()
        }
        Unit
    }

    BackHandler(onBack = exitPlayback)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    useController = true
                    controllerAutoShow = true
                    controllerShowTimeoutMs = 3_000
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    keepScreenOn = true
                    this.player = player
                }
            },
            update = { playerView ->
                playerView.player = player
            },
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PlayerAction(
                text = "返回详情",
                onClick = exitPlayback,
            )
            if (isFullSpace) {
                PlayerAction(
                    text = "退出全空间",
                    onClick = {
                        player.pause()
                        val resumeBundle = playbackBundle()
                        scope.launch {
                            closeStage()
                            context.openWindowContainer(
                                DESTINATION_PLAYER_WINDOW_ID,
                                bundle = resumeBundle,
                            )
                        }
                    },
                )
            } else {
                PlayerAction(
                    text = "全空间",
                    onClick = {
                        player.pause()
                        scope.launch {
                            val result = context.openStage(
                                DESTINATION_PLAYER_STAGE_ID,
                                StageStyle.Full,
                                bundle = playbackBundle(),
                            )
                            if (result is OpenStageResult.Allowed) {
                                context.closeWindowContainer(DESTINATION_PLAYER_WINDOW_ID)
                            } else {
                                player.play()
                            }
                        }
                    },
                )
            }
        }
    }
}

private fun videoSourceUri(
    video: DestinationVideo,
    packageName: String,
) = when (val source = video.source) {
    is VideoSource.Raw -> "android.resource://$packageName/${source.resourceId}".toUri()
    is VideoSource.Remote -> source.url.toUri()
}

@Composable
private fun PlayerAction(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xCC242424))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = PicoTheme.typography.bodyMedium,
            color = Color.White,
        )
    }
}
