package com.ardclient.esikap.database.sscec

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.model.SSCECUpdateStatusModel

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

    @Update(entity = SSCECModel::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateSSCECStatus(sscec: SSCECUpdateStatusModel)

    @Query("SELECT * FROM sscec WHERE id = :id")
    fun getSSCECById(id: Int): List<SSCECModel>
}