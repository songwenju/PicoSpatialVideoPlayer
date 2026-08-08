package com.picoxr.destinationvideo

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.picoxr.destinationvideo.destination.DestinationPlaybackController
import com.picoxr.mediapack.core.base.MediaPackInit
import com.picoxr.mediapack.ui.utils.PlaybackControllerManager

/**
 * Starts the PICO spatial application and exposes its application context.
 */
class LocalDemoApplication : Application() {
    companion object {
        lateinit var context: Application
    }

    override fun onCreate() {
        super.onCreate()
        MediaPackInit.init(this, "DestinationVideo")
        context = this
        PlaybackControllerManager.initialize(DestinationPlaybackController(this))
        launch(::mainApp)
    }
}
