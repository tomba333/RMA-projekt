package com.example.bike_app

import android.app.Application
import com.example.bike_app.di.databaseModule
import com.example.bike_app.di.repositoryModule
import com.example.bike_app.di.serviceModule
import com.example.bike_app.di.viewmodelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BikeApp:Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@BikeApp)
            modules(
                databaseModule,
                serviceModule,
                repositoryModule,
                viewmodelModule
            )
        }
    }

    companion object{
        lateinit var application: Application
    }
}