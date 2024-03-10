package com.ardclient.esikap.database.kapal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ardclient.esikap.model.KapalModel

@Database(entities = [KapalModel::class], version = 1)
abstract class KapalRoomDatabase: RoomDatabase() {
    companion object{
        @Volatile
        private var INSTANCE: KapalRoomDatabase?=null
        fun getDatabase(context: Context): KapalRoomDatabase {
            return KapalRoomDatabase.INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KapalRoomDatabase::class.java,
                    "kapal_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                KapalRoomDatabase.INSTANCE = instance
                instance
            }
        }
    }

    abstract fun getKapalDAO():KapalDAO
}