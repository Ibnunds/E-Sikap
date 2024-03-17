package com.ardclient.esikap.database.cop

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ardclient.esikap.model.COPModel

@Database(entities = [COPModel::class], version = 1)
abstract class COPRoomDatabase : RoomDatabase() {
    companion object{
        @Volatile
        private var INSTANCE: COPRoomDatabase?=null
        fun getDatabase(context: Context): COPRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    COPRoomDatabase::class.java,
                    "cop_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun getCOPDao() : COPDao
}