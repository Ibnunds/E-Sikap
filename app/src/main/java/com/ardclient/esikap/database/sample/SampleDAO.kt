package com.ardclient.esikap.database.sample

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ardclient.esikap.model.Sample

@Dao
interface SampleDAO {
    @Insert
    fun insertSample(sample: Sample)

    @Query("SELECT * FROM sample ORDER BY id DESC")
    fun getAllSample(): List<Sample>

    @Delete
    fun deleteSample(id: Sample)

    @Update
    fun updateSample(sample: Sample)

    @Query("SELECT * FROM sample WHERE id = :id")
    fun getById(id: Int): List<Sample>
}