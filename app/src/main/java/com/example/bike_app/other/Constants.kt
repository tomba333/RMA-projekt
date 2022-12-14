package com.example.bike_app.other

import android.graphics.Color

object Constants {
    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    const val ACTION_START_STICKY = "ACTION_START_STICKY"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val LOCATION_UPDATE_INTERVAL = 7000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 9f
    const val MAP_ZOOM = 17f

    const val NOTIFICATION_CH_ID = "tracking_channel"
    const val NOTIFICATION_CH_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val TIMER_UPDATE_INTERVAL = 50L



}