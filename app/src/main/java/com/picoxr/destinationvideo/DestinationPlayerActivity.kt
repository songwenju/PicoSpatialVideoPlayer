package com.picoxr.destinationvideo

import android.os.Bundle
import com.pico.spatial.ui.platform.stub.SpatialStubActivity
import com.picoxr.destinationvideo.destination.DestinationPlaybackController
import com.picoxr.mediapack.core.base.const.SceneConfig
import com.picoxr.mediapack.ui.utils.PlaybackControllerManager

/**
 * Activity backing the dedicated playback window container.
 */
class DestinationPlayerActivity : SpatialStubActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (PlaybackControllerManager.getController() as? DestinationPlaybackController)
            ?.shareSpaceActivity = this
        SceneConfig.enableBlackSceneScreenEntity(false)
    }

    override fun onDestroy() {
        (PlaybackControllerManager.getController() as? DestinationPlaybackController)
            ?.takeIf { it.shareSpaceActivity === this }
            ?.shareSpaceActivity = null
        super.onDestroy()
    }
}
