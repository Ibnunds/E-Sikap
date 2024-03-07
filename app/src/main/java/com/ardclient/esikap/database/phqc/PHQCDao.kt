package com.ardclient.esikap.database.phqc

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ardclient.esikap.model.PHQCModel

@Dao
interface PHQCDao {
    @Insert
    fun createPHQC(phqc: PHQCModel)

    @Query("SELECT * FROM phqc ORDER BY id DESC")
    fun getAllPHQC(): List<PHQCModel>

    @Delete
    fun deletePHQC(id: PHQCModel)

    @Update
    fun updatePHQC(phqc: PHQCModel)

    @Query("SELECT * FROM phqc WHERE id = :id")
    fun getPHQCById(id: Int): List<PHQCModel>
}