package com.picoxr.destinationvideo

import android.os.Bundle
import com.pico.spatial.ui.platform.stub.SpatialLaunchActivity
import com.picoxr.destinationvideo.destination.DestinationPlaybackController
import com.picoxr.mediapack.ui.utils.PlaybackControllerManager

/**
 * Launch activity for the Destination Video spatial window.
 */
class MainActivity : SpatialLaunchActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (PlaybackControllerManager.getController() as? DestinationPlaybackController)
            ?.shareSpaceActivity = this
    }

    override fun onDestroy() {
        (PlaybackControllerManager.getController() as? DestinationPlaybackController)
            ?.takeIf { it.shareSpaceActivity === this }
            ?.shareSpaceActivity = null
        super.onDestroy()
    }
}
