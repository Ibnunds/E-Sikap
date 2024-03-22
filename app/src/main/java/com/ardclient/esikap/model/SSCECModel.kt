package com.ardclient.esikap.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ardclient.esikap.model.reusable.SanitasiModel
import kotlinx.parcelize.Parcelize

@Entity(tableName = "sscec")
@Parcelize
data class SSCECModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "kapalId") var kapalId: Int = 0,
    @Embedded(prefix = "kapal_") var kapal: KapalModel = KapalModel(),
    @Embedded(prefix = "sanitasi_") var sanitasi: SanitasiModel = SanitasiModel(),
    @ColumnInfo(name = "pelabuhanTujuan") var pelabuhanTujuan: String = "",
    @ColumnInfo(name = "tgl_tiba") var tglTiba: String = "",
    @ColumnInfo(name = "lokasi_sandar") var lokasiSandar: String = "",
    @ColumnInfo(name = "jumlah_abk_asing") var jumlahABKAsing: Int = 0,
    @ColumnInfo(name = "abk_asing_sehat") var asingSehat: Int = 0,
    @ColumnInfo(name = "abk_asing_sakit") var asingSakit : Int = 0,
    @ColumnInfo(name = "jumlah_abk_wni") var jumlahABKWNI: Int = 0,
    @ColumnInfo(name = "abk_wni_sehat") var wniSehat: Int = 0,
    @ColumnInfo(name = "abk_wni_sakit") var wniSakit: Int = 0,
    @ColumnInfo(name = "rec_sscec") var recSSCEC: String = "",
    @ColumnInfo(name = "rec_tanggal") var recTanggal: String = "",
    @ColumnInfo(name = "rec_jam") var recJam: String = "",
    @ColumnInfo(name = "sign_kapten") var signKapten: String = "",
    @ColumnInfo(name = "sign_nama_kapten") var signNamaKapten: String = "",
    @ColumnInfo(name = "sign_petugas") var signPetugas: String = "",
    @ColumnInfo(name = "sign_nama_petugas") var signNamaPetugas: String = "",
):Parcelable