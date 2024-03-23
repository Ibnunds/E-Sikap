package com.ardclient.esikap.database.p3k

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ardclient.esikap.model.P3KModel


@Database(entities = [P3KModel::class], version = 1)
abstract class P3KRoomDatabase : RoomDatabase() {
    companion object{
        @Volatile
        private var INSTANCE: P3KRoomDatabase?=null
        fun getDatabase(context: Context): P3KRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    P3KRoomDatabase::class.java,
                    "p3k_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun getP3KDAO(): P3KDao
}