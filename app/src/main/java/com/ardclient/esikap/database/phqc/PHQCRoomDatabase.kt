package com.ardclient.esikap.database.phqc

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ardclient.esikap.model.PHQCModel

@Database(entities = [PHQCModel::class], version = 1)
abstract class PHQCRoomDatabase: RoomDatabase() {
    companion object{
        @Volatile
        private var INSTANCE: PHQCRoomDatabase?=null
        fun getDatabase(context: Context): PHQCRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PHQCRoomDatabase::class.java,
                    "phqc_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun getPHQCDao() : PHQCDao
}