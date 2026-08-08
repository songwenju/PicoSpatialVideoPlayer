package com.picoxr.destinationvideo.destination

import android.content.Context

private const val RETURN_PREFS_NAME = "destination_return_route"
private const val KEY_DETAIL_VIDEO_ID = "detail_video_id"
private const val NO_VIDEO_ID = -1

object DestinationReturnRoute {
    fun requestDetail(context: Context, videoId: Int) {
        context.getSharedPreferences(RETURN_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_DETAIL_VIDEO_ID, videoId)
            .apply()
    }

    fun consumeDetail(context: Context): DestinationVideo? {
        val preferences = context.getSharedPreferences(RETURN_PREFS_NAME, Context.MODE_PRIVATE)
        val videoId = preferences.getInt(KEY_DETAIL_VIDEO_ID, NO_VIDEO_ID)
        if (videoId == NO_VIDEO_ID) {
            return null
        }
        preferences.edit()
            .remove(KEY_DETAIL_VIDEO_ID)
            .apply()
        return DestinationCatalog.findVideo(videoId)
    }
}
