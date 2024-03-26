package com.ardclient.esikap.database.p3k

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.model.P3KUpdateStatusModel

@Dao
interface P3KDao {
    @Insert
    fun createP3K(p3k: P3KModel)

    @Query("SELECT * FROM p3k WHERE kapalId = :kapalId ORDER BY id DESC")
    fun getAllP3K(kapalId: Int): List<P3KModel>

    @Delete
    fun deleteP3K(id: P3KModel)

    @Update
    fun updateP3K(p3k: P3KModel)

    @Update(entity = P3KModel::class)
    fun updateP3KStatus(p3k: P3KUpdateStatusModel)

    @Query("SELECT * FROM p3k WHERE id = :id")
    fun getP3KById(id: Int): List<P3KModel>
}