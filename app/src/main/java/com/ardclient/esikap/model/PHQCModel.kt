package com.ardclient.esikap.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "phqc")
@Parcelize
data class PHQCModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "nama_agen") var namaAgen: String = "",
    @ColumnInfo(name = "nama_kapal") var namaKapal: String = "",
    @ColumnInfo(name = "gros_tone") var grosTone: String = "",
    @ColumnInfo(name = "bendera") var bendera: String = "",
    @ColumnInfo(name = "negara_asal") var negaraAsal: String = "",
    @ColumnInfo(name = "tujuan") var tujuan: String = "",
    @ColumnInfo(name = "dokumen_kapan") var dokumenKapal: String = "",
    @ColumnInfo(name = "lokasi_pemeriksaan") var lokasiPemeriksaan: String = "",
    @ColumnInfo(name = "jumlah_abk") var jumlahABK: Int = 0,
    @ColumnInfo(name = "deteksi_demam") var deteksiDemam: Int = 0,
    @ColumnInfo(name = "jumlah_penumpang") var jumlahPenumpang: Int = 0,
    @ColumnInfo(name = "jumlah_sehat") var jumlahSehat: Int = 0,
    @ColumnInfo(name = "jumlah_sakit") var jumlahSakit: Int = 0,
    @ColumnInfo(name = "jumlah_meninggal") var jumlahMeninggal: Int = 0,
    @ColumnInfo(name = "jumlah_dirujuk") var jumlahDirujuk: Int = 0,
    @ColumnInfo(name = "status_sanitasi") var statusSanitasi: String = "",
    @ColumnInfo(name = "kesimpulan") var kesimpulan: String = "",
    @ColumnInfo(name = "petugas_pelaksana") var petugasPelaksana: String = "",
    @ColumnInfo(name = "signature") var signature: String = ""
) : Parcelable
