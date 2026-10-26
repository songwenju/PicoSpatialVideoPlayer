package com.picoxr.destinationvideo

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.DefaultWindowContainer
import com.pico.spatial.ui.foundation.dsl.Immersion
import com.pico.spatial.ui.foundation.dsl.SpatialAppScope
import com.pico.spatial.ui.foundation.dsl.Stage
import com.pico.spatial.ui.foundation.dsl.WindowContainer
import com.pico.spatial.ui.platform.resize.ContainerResizeRestriction
import com.picoxr.destinationvideo.destination.DESTINATION_PLAYER_POSITION_MS
import com.picoxr.destinationvideo.destination.DESTINATION_PLAYER_STAGE_ID
import com.picoxr.destinationvideo.destination.DESTINATION_PLAYER_VIDEO_ID
import com.picoxr.destinationvideo.destination.DESTINATION_PLAYER_WINDOW_ID
import com.picoxr.destinationvideo.destination.DestinationPlayerWindow
import com.picoxr.destinationvideo.screens.MainScreen

/**
 * Registers the catalog window, playback window, and Full Space playback stage.
 */
@SuppressLint("RestrictedApi")
fun mainApp(scope: SpatialAppScope) =
    with(scope) {
        DefaultWindowContainer {
            Box(
                modifier = Modifier.windowConstraints(
                    minWidth = 540.dp,
                    minHeight = 450.dp,
                    maxWidth = 3200.dp,
                    maxHeight = 1800.dp,
                ),
            ) {
                WindowBackground {
                    MainScreen()
                }
            }
        }
        WindowContainer(
            id = DESTINATION_PLAYER_WINDOW_ID,
            enableMaterialBackground = false,
            defaultResizeRestriction = ContainerResizeRestriction.UniformResizable,
            targetActivity = DestinationPlayerActivity::class.java,
        ) {
            val playerVideoId = bundle
                ?.takeIf { it.containsKey(DESTINATION_PLAYER_VIDEO_ID) }
                ?.getInt(DESTINATION_PLAYER_VIDEO_ID)
            val initialPositionMs = bundle
                ?.takeIf { it.containsKey(DESTINATION_PLAYER_POSITION_MS) }
                ?.getLong(DESTINATION_PLAYER_POSITION_MS)
                ?: 0L
            PicoTheme {
                Box(
                    modifier = Modifier
                        .windowConstraints(
                            minWidth = 540.dp,
                            minHeight = 450.dp,
                            maxWidth = 3200.dp,
                            maxHeight = 1800.dp,
                        )
                        .background(Color.Transparent),
                ) {
                    DestinationPlayerWindow(
                        videoId = playerVideoId,
                        initialPositionMs = initialPositionMs,
                    )
                }
            }
        }
        Stage(
            id = DESTINATION_PLAYER_STAGE_ID,
            immersion = Immersion.Default,
        ) {
            val playerVideoId = bundle
                ?.takeIf { it.containsKey(DESTINATION_PLAYER_VIDEO_ID) }
                ?.getInt(DESTINATION_PLAYER_VIDEO_ID)
            val initialPositionMs = bundle
                ?.takeIf { it.containsKey(DESTINATION_PLAYER_POSITION_MS) }
                ?.getLong(DESTINATION_PLAYER_POSITION_MS)
                ?: 0L
            PicoTheme {
                DestinationPlayerWindow(
                    videoId = playerVideoId,
                    initialPositionMs = initialPositionMs,
                    isFullSpace = true,
                )
            }
        }
    }

/**
 * Applies the reference app's #1C1C1C window background.
 */
@Composable
private fun WindowBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.bg_thickest)),
    ) {
        content()
    }
}
