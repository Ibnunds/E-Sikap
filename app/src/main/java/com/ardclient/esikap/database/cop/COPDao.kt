package com.ardclient.esikap.database.cop

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ardclient.esikap.model.COPModel

@Dao
interface COPDao {
    @Insert
    fun createCOP(cop: COPModel)

    @Query("SELECT * FROM cop WHERE kapalId = :kapalId ORDER BY id DESC")
    fun getAllCOP(kapalId: Int): List<COPModel>

    @Delete
    fun deleteCOP(id: COPModel)

    @Update
    fun updateCOP(cop: COPModel)

    @Query("SELECT * FROM cop WHERE id = :id")
    fun getCOPById(id: Int): List<COPModel>
}