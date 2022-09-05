package com.example.bike_app.di

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.bike_app.ui.MainActivity
import com.example.bike_app.R
import com.example.bike_app.other.Constants
import com.example.bike_app.services.TrackingService
import com.google.android.gms.location.FusedLocationProviderClient
import org.koin.core.annotation.KoinReflectAPI

import org.koin.dsl.module

@OptIn(KoinReflectAPI::class)
val serviceModule = module{

    fun provideFusedLocationProviderClient(application: Context,) = FusedLocationProviderClient(application)

    fun provideMainActivityPendingIntent(application: Context) = PendingIntent.getActivity(
        application,
        0,
        Intent(application, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    fun provideBaseNotificationBuilder(application: Context,
                                       pendingIntent: PendingIntent)=
        NotificationCompat.Builder(application, Constants.NOTIFICATION_CH_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.icons_bicycle_24)
            .setContentTitle("Bike App")
            .setContentText("")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)


    scope<TrackingService> {
        scoped {provideBaseNotificationBuilder(get(),get())}
        scoped { provideFusedLocationProviderClient(get()) }
        scoped { provideMainActivityPendingIntent(get()) }
    }

    fun provideSharedPreferences(application: Context)=application.getSharedPreferences("sharedPrefs",Context.MODE_PRIVATE)

    single { provideSharedPreferences(get()) }
}

