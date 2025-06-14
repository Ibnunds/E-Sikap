package com.ardclient.esikap.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "kapal")
@Parcelize
data class KapalModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id:Int = 0,
    @ColumnInfo(name = "nama_kapal") var namaKapal: String = "",
    @ColumnInfo(name = "nama_agen") var namaAgen: String = "",
    @ColumnInfo(name = "kapten_kapal") var kaptenKapal: String = "",
    @ColumnInfo(name = "gross_tone") var grossTone: String = "",
    @ColumnInfo(name = "bendera") var bendera: String = "",
    @ColumnInfo(name = "imo") var imo: String = "",
    @ColumnInfo(name = "negara_asal") var negaraAsal: String = "",
    @ColumnInfo(name = "tipe_kapal") var tipeKapal: String = "",
    @ColumnInfo(name = "flag") var flag: String = "",
    @ColumnInfo(name = "tipe_dokumen") var tipeDokumen: String = ""
) : Parcelable
