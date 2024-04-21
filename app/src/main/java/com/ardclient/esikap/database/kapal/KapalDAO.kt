package com.ardclient.esikap.database.kapal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ardclient.esikap.model.KapalModel

@Dao
interface KapalDAO {
    @Insert
    fun insertKapal(kapal: KapalModel)

    @Query("SELECT * FROM kapal ORDER BY id DESC")
    fun getAllKapal(): List<KapalModel>

    @Delete
    fun deleteKapal(id: KapalModel)

    @Update
    fun updateKapal(kapal: KapalModel)

    @Query("SELECT * FROM kapal WHERE id = :id")
    fun getKapalById(id: Int): List<KapalModel>

    @Query("SELECT * FROM kapal WHERE nama_kapal LIKE :nama")
    fun searchKapalByName(nama: String): List<KapalModel>
}