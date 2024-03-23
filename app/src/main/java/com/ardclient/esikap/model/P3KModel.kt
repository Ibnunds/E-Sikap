package com.ardclient.esikap.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ardclient.esikap.model.reusable.PemeriksaanKapalModel
import com.ardclient.esikap.model.reusable.SanitasiModel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "p3k")
@Parcelize
data class P3KModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "kapalId") var kapalId: Int = 0,
    @Embedded(prefix = "kapal_") var kapal: KapalModel = KapalModel(),
    @Embedded(prefix = "pemeriksaan_") var pemeriksaan: PemeriksaanKapalModel = PemeriksaanKapalModel(),
    @ColumnInfo(name = "jenis_layanan") var jenisLayanan: String = "",
    @ColumnInfo(name = "jenis_pelayanan") var jenisPelayanan: String = "",
    @ColumnInfo(name = "lokasi_pemeriksaan") var lokasiPemeriksaan: String = "",
    @ColumnInfo(name = "tanggal_diperiksa") var tglDiperiksa: String = "",
    @ColumnInfo(name = "jumlah_abk") var jmlABK: Int = 0,
    @ColumnInfo(name = "abk_sehat") var abkSehat: Int = 0,
    @ColumnInfo(name = "abk_sakit") var abkSakit: Int = 0,
    @ColumnInfo(name = "rec_p3k") var recP3K: String = "",
    @ColumnInfo(name = "rec_tanggal") var recTanggal: String = "",
    @ColumnInfo(name = "rec_jam") var recJam: String = "",
    @ColumnInfo(name = "sign_kapten") var signKapten: String = "",
    @ColumnInfo(name = "sign_nama_kapten") var signNamaKapten: String = "",
    @ColumnInfo(name = "sign_petugas") var signPetugas: String = "",
    @ColumnInfo(name = "sign_nama_petugas") var signNamaPetugas: String = "",
):Parcelable