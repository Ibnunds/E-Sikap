package com.ardclient.esikap.database.sscec

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ardclient.esikap.model.SSCECModel

@Dao
interface SSCECDao {
    @Insert
    fun createSSCEC(sscec: SSCECModel)

    @Query("SELECT * FROM sscec WHERE kapalId = :kapalId ORDER BY id DESC")
    fun getAllSSCEC(kapalId: Int): List<SSCECModel>

    @Delete
    fun deleteSSCEC(id: SSCECModel)

    @Update
    fun updateSSCEC(sscec: SSCECModel)

    @Query("SELECT * FROM sscec WHERE id = :id")
    fun getCOPById(id: Int): List<SSCECModel>
}