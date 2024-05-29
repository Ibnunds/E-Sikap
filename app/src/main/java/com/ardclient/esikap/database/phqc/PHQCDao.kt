package com.ardclient.esikap.database.phqc

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ardclient.esikap.model.PHQCModel
import com.ardclient.esikap.model.PHQCStatusUpdateModel

@Dao
interface PHQCDao {
    @Insert
    fun createPHQC(phqc: PHQCModel)

    @Query("SELECT * FROM phqc WHERE kapalId = :kapalId AND flag = :flag ORDER BY id DESC")
    fun getAllPHQC(kapalId: Int, flag: String): List<PHQCModel>

    @Delete
    fun deletePHQC(id: PHQCModel)

    @Update
    fun updatePHQC(phqc: PHQCModel)

    @Update(entity = PHQCModel::class)
    fun updateStatusPHQC(phqcStatus: PHQCStatusUpdateModel)

    @Query("SELECT * FROM phqc WHERE id = :id")
    fun getPHQCById(id: Int): List<PHQCModel>
}