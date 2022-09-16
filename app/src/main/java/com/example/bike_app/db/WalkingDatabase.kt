package com.example.bike_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Walk::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class WalkingDatabase:RoomDatabase() {
    abstract fun getWalkDao(): WalkDao
    companion object{
        private const val databaseName ="WalkDB"

        @Volatile
        private var INSTANCE: WalkingDatabase? = null
        fun getDataBase(context: Context): WalkingDatabase{
            if(INSTANCE == null){
                synchronized(this){
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): WalkingDatabase? {
            return Room.databaseBuilder(
                context.applicationContext,
                WalkingDatabase::class.java,
                databaseName
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}