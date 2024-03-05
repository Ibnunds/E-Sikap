package com.ardclient.esikap.database.sample

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ardclient.esikap.model.Sample

@Database(entities = [Sample::class], version = 1)
abstract class SampleRoomDatabase : RoomDatabase() {

    companion object{
        @Volatile
        private var INSTANCE: SampleRoomDatabase?=null
        fun getDatabase(context: Context): SampleRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SampleRoomDatabase::class.java,
                    "sample_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    abstract fun getSampleDao() : SampleDAO
}