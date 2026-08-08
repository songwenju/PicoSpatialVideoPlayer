package com.picoxr.destinationvideo.destination

import android.app.Activity
import android.app.Application
import android.app.SpaceContext
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.pico.spatial.core.platform.SpatialBuild
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.platform.containers.OpenStageResult
import com.pico.spatial.ui.platform.containers.StageStyle
import com.pico.spatial.ui.platform.containers.closeStage
import com.pico.spatial.ui.platform.containers.closeWindowContainer
import com.pico.spatial.ui.platform.containers.minimizeWindowContainer
import com.pico.spatial.ui.platform.containers.openStage
import com.pico.spatial.ui.platform.containers.openWindowContainer
import com.pico.spatial.ui.platform.containers.restoreWindowContainer
import com.picopui.fusionapp.logger.logging.Logger
import com.picoxr.destinationvideo.DestinationPlayerActivity
import com.picoxr.mediapack.common.PXRPlayer
import com.picoxr.mediapack.common.PXRPlayer.RepeatMode
import com.picoxr.mediapack.core.base.const.PLAYBACK_SPACE_ID
import com.picoxr.mediapack.core.ecs.entity.AmbisonicManager
import com.picoxr.mediapack.core.base.const.VideoType
import com.picoxr.mediapack.core.base.model.PlayerItem
import com.picoxr.mediapack.player.PicoPlayer
import com.picoxr.mediapack.ui.PlaybackController
import com.picoxr.mediapack.ui.VideoPlaybackView
import com.picoxr.mediapack.ui.controller.bean.PlaybackEvent
import com.picoxr.mediapack.ui.controller.bean.PlaybackEventType
import com.picoxr.mediapack.ui.controller.bean.PlaybackStates
import com.picoxr.mediapack.ui.controller.bean.rememberPlaybackStates
import com.picoxr.mediapack.ui.utils.PlaybackControllerManager
import kotlinx.coroutines.launch

private const val DESTINATION_WINDOW_ID = DESTINATION_PLAYER_WINDOW_ID
private const val TAG = "DestinationPlayback"

/**
 * Bridges MediaPack's immersive state changes to the MediaPack playback stage.
 */
class DestinationPlaybackController(
    private val context: Application,
) : PlaybackController(context) {
    var shareSpaceActivity: Activity? = null
    var onFullSpaceClosed: (() -> Unit)? = null
    private var isFullSpaceOpen = false
    private var isClosingFullSpace = false
    var hasShowWindowContainer = true

    override fun onControllerEvent(
        event: PlaybackEvent,
        playbackStates: PlaybackStates,
    ) {
        Log.i(TAG, "onControllerEvent: ${event.type}, immersive=${isImmersive.value}")
        if (event.type == PlaybackEventType.CLICK_FULL_SCREEN) {
            currentPlayItem.value?.id
                ?.toIntOrNull()
                ?.let { DestinationReturnRoute.requestDetail(context, it) }
            context.openWindowContainer(DESTINATION_HOME_WINDOW_ID)
            context.closeWindowContainer(DESTINATION_PLAYER_WINDOW_ID)
            return
        }
        super.onControllerEvent(event, playbackStates)
    }

    override fun openImmersiveSpace() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            context.openStage(PLAYBACK_SPACE_ID, StageStyle.Full)
        }
    }

    override fun hideWindowContainer() {
        Logger.i(TAG, "hideWindowContainer.minimizeWindowContainer")

        val isStage = stageOpenChecker()
        Logger.i(TAG, "hideWindowContainer: isStage:$isStage")
        if (isStage) {
            // only system app
            context.minimizeWindowContainer(DESTINATION_WINDOW_ID)
        } else {
            shareSpaceActivity?.moveTaskToBack(false)
        }
        hasShowWindowContainer = false
    }

    override fun showWindowContainer() {

        if (hasShowWindowContainer) {
            return
        }
        val isStage = stageOpenChecker()
        Logger.i(TAG, "showWindowContainer: $isStage")
        if (isStage) {
            val result = context.restoreWindowContainer(DESTINATION_WINDOW_ID)
            Logger.i(TAG, "showWindowContainer: $result")
        } else {
            Logger.i(TAG, "showWindowContainer")
            val intent = Intent(context, DestinationPlayerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        hasShowWindowContainer = true
    }


    var stageOpenChecker: () -> Boolean = {
        if (SpatialBuild.isSpatialPlatform()) {
            SpaceContext.checkSelfFullSpace() == SpaceContext.STATE_FULL_SPACE
        } else {
            false
        }
    }
}

/**
 * Presents the MediaPack-backed window player and opens its Full Space stage.
 */
@Composable
fun DestinationPlayerWindow(videoId: Int?) {
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
            )
        }
        return
    }

    DestinationPlayer(
        video = video,
        onClose = {
            context.closeWindowContainer(DESTINATION_PLAYER_WINDOW_ID)
        },
        onFullSpaceClosed = {
            // MediaPack restores the playback window; keep this Activity mounted.
        },
    )
}

/**
 * Presents the MediaPack-backed window player and opens its Full Space stage.
 */
@Composable
fun DestinationPlayer(
    video: DestinationVideo,
    onClose: () -> Unit,
    onFullSpaceClosed: () -> Unit,
) {
    val context = LocalContext.current
    val controller = remember { PlaybackControllerManager.getController() }
    val isConnected = controller.hasConnectedPlayer()
    val ownsPlayer = !isConnected
    val player: PXRPlayer = remember(controller, video.id) {
        if (!isConnected) {
            PicoPlayer(context)
        } else {
            controller.player.value ?: PicoPlayer(context)
        }
    }
    val playbackStates = rememberPlaybackStates()

    BackHandler(onBack = onClose)

    DisposableEffect(controller, playbackStates, video.id) {
        controller.bindPlaybackState(playbackStates)
        (controller as? DestinationPlaybackController)?.onFullSpaceClosed = onFullSpaceClosed

        player.setRepeatMode(RepeatMode.REPEAT_MODE_ONE_OFF)
        if (!controller.hasConnectedPlayer()) {
            controller.connectPlayer(player)
        }

        val videoUri = when (val source = video.source) {
            is VideoSource.Raw -> "android.resource://${context.packageName}/${source.resourceId}"
            is VideoSource.Remote -> source.url
        }.toUri().toString()

        Logger.i(
            TAG,
            "DestinationPlayer.setPlayItem: $videoUri, previous=${controller.currentPlayItem.value?.path}",
        )
        controller.setPlayItem(
            PlayerItem().apply {
                id = video.id.toString()
                name = video.name
                path = videoUri
                videoOriginType = VideoType.TYPE_2D.type
                videoType = VideoType.TYPE_2D.type
                playedTime = video.startSeconds * 1_000L
            },
        )

        // Keep the page setup aligned with SpatialVideoPage: configure controller first,
        // then start the connected player so MediaPack owns share-space and full-space UI.
        controller.setVideoType(VideoType.TYPE_2D.type)
        playbackStates.setVideoType(VideoType.TYPE_2D.type)
        controller.setUseMono2Stereo(true)
        controller.setWindowBoundsSize(1280f to 720f)

        player.prepare()
        if (video.startSeconds > 0) {
            player.seekTo(video.startSeconds * 1_000L)
        }
        player.play()

        onDispose {
            Logger.i(TAG, "DestinationPlayer: onDispose")
            (controller as? DestinationPlaybackController)?.onFullSpaceClosed = null
            if (controller.isImmersive.value) {
                controller.setImmersive(false)
            }
            player.stop()
            player.release()
            AmbisonicManager.releaseAmbisonicResource()
            controller.setPlayedProgress(
                controller.currentPlayItem.value?.path,
                playbackStates.playedProgress.floatValue,
            )
            controller.disconnectPlayer()
        }
    }

    controller.setShowPlayList(false)
    controller.setShowSettings(false)
    controller.setShowPlaybackControls(true)
    controller.setShowEnterFullSpace(true)
    controller.setEnterFullSpaceEnabled(true)
    controller.setShowFullScreenButton(!controller.isImmersive.value)
    controller.setFullScreenButtonEnabled(true)
    controller.setShowSpeedButton(true)
    controller.setSpeedOptions(listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f))

    VideoPlaybackView(
        playbackController = controller,
        playbackStates = playbackStates,
        modifier = Modifier.fillMaxSize(),
        showsPlaybackControls = true,
    )
}
