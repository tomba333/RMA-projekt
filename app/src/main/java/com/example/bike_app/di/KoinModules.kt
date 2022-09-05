package com.example.bike_app.di

import android.app.Application
import com.example.bike_app.db.RideDAO
import com.example.bike_app.db.RidingDatabase
import com.example.bike_app.db.repository.RideRepository
import com.example.bike_app.db.repository.RideRepositoryImpl
import com.example.bike_app.presentation.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module{
    fun provideDatabase(application: Application): RidingDatabase{
        return RidingDatabase.getDataBase(application)
    }
    fun provideRideDAO(database: RidingDatabase):RideDAO{
        return database.getRideDao()
    }
    single<RidingDatabase>{provideDatabase(get())}
    single<RideDAO>{provideRideDAO(get())}
}
val repositoryModule = module {
    single<RideRepository> { RideRepositoryImpl(get()) }
}
val viewmodelModule = module {
    viewModel { MainViewModel(get()) }

}

