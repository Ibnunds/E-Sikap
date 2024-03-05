package com.ardclient.esikap.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sample")
data class Sample(
    @ColumnInfo(name = "Title") var title: String = "",
    @ColumnInfo(name = "Message") var message: String = ""
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
