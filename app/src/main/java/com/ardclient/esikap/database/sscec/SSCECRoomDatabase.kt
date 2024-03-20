package com.ardclient.esikap.database.sscec

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ardclient.esikap.model.SSCECModel

@Database(entities = [SSCECModel::class], version = 1)
abstract class SSCECRoomDatabase : RoomDatabase() {
    companion object{
        @Volatile
        private var INSTANCE: SSCECRoomDatabase?=null
        fun getDatabase(context: Context): SSCECRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SSCECRoomDatabase::class.java,
                    "sscec_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun getSSCECDao() : SSCECDao
}