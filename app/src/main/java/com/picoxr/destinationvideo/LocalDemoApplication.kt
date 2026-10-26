package com.picoxr.destinationvideo

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch

/**
 * Starts the PICO spatial application and exposes its application context.
 */
class LocalDemoApplication : Application() {
    companion object {
        lateinit var context: Application
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        launch(::mainApp)
    }
}
