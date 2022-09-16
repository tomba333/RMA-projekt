package com.example.bike_app.di

import android.app.Application
import com.example.bike_app.db.RideDAO
import com.example.bike_app.db.RidingDatabase
import com.example.bike_app.db.WalkDao
import com.example.bike_app.db.WalkingDatabase
import com.example.bike_app.db.repository.RideRepository
import com.example.bike_app.db.repository.RideRepositoryImpl
import com.example.bike_app.db.repository.WalkRepository
import com.example.bike_app.db.repository.WalkRepositoryImpl
import com.example.bike_app.presentation.MainViewModel
import com.example.bike_app.presentation.WalkViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module{
    fun provideDatabase(application: Application): RidingDatabase{
        return RidingDatabase.getDataBase(application)
    }
    fun provideRideDAO(database: RidingDatabase):RideDAO{
        return database.getRideDao()
    }
    fun provideWalkingDB(application: Application):WalkingDatabase{
        return  WalkingDatabase.getDataBase(application)
    }
    fun provideWalkDao(database: WalkingDatabase):WalkDao{
        return database.getWalkDao()
    }
    single<RidingDatabase>{provideDatabase(get())}
    single<RideDAO>{provideRideDAO(get())}
    single<WalkingDatabase> { provideWalkingDB(get()) }
    single<WalkDao> { provideWalkDao(get()) }

}
val repositoryModule = module {
    single<RideRepository> { RideRepositoryImpl(get())}
    single<WalkRepository>{ WalkRepositoryImpl(get())}
}
val viewmodelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel{WalkViewModel(get())}

}

