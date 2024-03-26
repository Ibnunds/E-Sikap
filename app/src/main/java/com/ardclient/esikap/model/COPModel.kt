package com.ardclient.esikap.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.model.reusable.SanitasiModel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "cop")
@Parcelize
data class COPModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "kapalId") var kapalId: Int = 0,
    @Embedded(prefix = "kapal_") var kapal: KapalModel = KapalModel(),
    @ColumnInfo(name = "tujuan") var tujuan: String = "",
    @ColumnInfo(name = "tanggal_tiba") var tglTiba: String = "",
    @ColumnInfo(name = "lokasi_sandar") var lokasiSandar: String = "",
    @ColumnInfo(name = "jumlah_abk_asing") var jumlahABKAsing: Int = 0,
    @ColumnInfo(name = "abk_asing_sehat") var asingSehat: Int = 0,
    @ColumnInfo(name = "abk_asing_sakit") var asingSakit : Int = 0,
    @ColumnInfo(name = "jumlah_abk_wni") var jumlahABKWNI: Int = 0,
    @ColumnInfo(name = "abk_wni_sehat") var wniSehat: Int = 0,
    @ColumnInfo(name = "abk_wni_sakit") var wniSakit: Int = 0,
    @ColumnInfo(name = "jumlah_penumpang_wni") var jumlahPenumpangWNI: Int = 0,
    @ColumnInfo(name = "penumpang_wni_sehat") var penumpangSehat: Int = 0,
    @ColumnInfo(name = "penumpang_wni_sakit") var penumpangSakit: Int = 0,
    @ColumnInfo(name = "jumlah_penumpang_asing") var jumlahPenumpangAsing: Int = 0,
    @ColumnInfo(name = "penumpang_asing_sehat") var penumpangAsingSehat: Int = 0,
    @ColumnInfo(name = "penumpang_asing_sakit") var penumpangAsingSakit : Int = 0,
    @Embedded(prefix = "doc_") var dokumenKapal: DokumenKapalModel = DokumenKapalModel(),
    @Embedded(prefix = "sanitasi_") var sanitasiKapal: SanitasiModel = SanitasiModel(),
    @ColumnInfo(name = "rekomendasi") var rekomendasi: String = "",
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis(),
) : Parcelable
