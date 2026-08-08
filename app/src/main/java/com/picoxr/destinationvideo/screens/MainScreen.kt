package com.picoxr.destinationvideo.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pico.spatial.ui.design.PicoTheme
import com.picoxr.destinationvideo.destination.DestinationApp

/**
 * Displays the Destination Video experience in the primary spatial window.
 */
@Preview(widthDp = 1080)
@Composable
fun MainScreen() {
    PicoTheme {
        DestinationApp()
    }
}
