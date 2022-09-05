package com.example.bike_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Ride::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class RidingDatabase:RoomDatabase() {

    abstract fun getRideDao(): RideDAO
    companion object{
        private const val databaseName ="BikeDB"

        @Volatile
        private var INSTANCE: RidingDatabase? = null
        fun getDataBase(context: Context): RidingDatabase{
            if(INSTANCE == null){
                synchronized(this){
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): RidingDatabase? {
            return Room.databaseBuilder(
                context.applicationContext,
                RidingDatabase::class.java,
                databaseName
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}