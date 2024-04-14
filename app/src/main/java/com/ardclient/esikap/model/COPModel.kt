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
    @ColumnInfo(name = "jenis_layanan") var jenisLayanan: String = "",
    @ColumnInfo(name = "jenis_pelayaran") var jenisPelayaran: String = "",
    @ColumnInfo(name = "tanggal_tiba") var tglTiba: String = "",
    @ColumnInfo(name = "lokasi_sandar") var lokasiSandar: String = "",
    @ColumnInfo(name = "lokasi_pemeriksaan") var lokasiPemeriksaan: String = "",
    @ColumnInfo(name = "jumlah_abk_asing") var jumlahABKAsing: Int = 0,
    @ColumnInfo(name = "jumlah_abk_asing_md") var jumlahABKAsingMD: Int = 0,
    @ColumnInfo(name = "abk_asing_sehat") var asingSehat: Int = 0,
    @ColumnInfo(name = "abk_asing_sakit") var asingSakit : Int = 0,
    @ColumnInfo(name = "jumlah_abk_wni") var jumlahABKWNI: Int = 0,
    @ColumnInfo(name = "jumlah_abk_wni_md") var jumlahABKWNIMD: Int = 0,
    @ColumnInfo(name = "abk_wni_sehat") var wniSehat: Int = 0,
    @ColumnInfo(name = "abk_wni_sakit") var wniSakit: Int = 0,
    @ColumnInfo(name = "jumlah_penumpang_wni") var jumlahPenumpangWNI: Int = 0,
    @ColumnInfo(name = "jumlah_penumpang_wni_md") var jumlahPenumpangWNIMD: Int = 0,
    @ColumnInfo(name = "penumpang_wni_sehat") var penumpangSehat: Int = 0,
    @ColumnInfo(name = "penumpang_wni_sakit") var penumpangSakit: Int = 0,
    @ColumnInfo(name = "jumlah_penumpang_asing") var jumlahPenumpangAsing: Int = 0,
    @ColumnInfo(name = "jumlah_penumpang_asing_md") var jumlahPenumpangAsingMD: Int = 0,
    @ColumnInfo(name = "penumpang_asing_sehat") var penumpangAsingSehat: Int = 0,
    @ColumnInfo(name = "penumpang_asing_sakit") var penumpangAsingSakit : Int = 0,
    @Embedded(prefix = "doc_") var dokumenKapal: DokumenKapalModel = DokumenKapalModel(),
    @Embedded(prefix = "sanitasi_") var sanitasiKapal: SanitasiModel = SanitasiModel(),
    @ColumnInfo(name = "pemeriksaanObatP3K") var obatP3K: String = "",
    @ColumnInfo(name = "pelanggaranKarantina") var pelanggaranKarantina: String = "",
    @ColumnInfo(name = "dokumenKesehatanKapal") var dokumenKesehatanKapal: String = "",
    @ColumnInfo(name = "doc_type") var docType: String = "",
    @ColumnInfo(name = "doc_file") var docFile: String = "",
    @ColumnInfo(name = "doc_tanggal") var docTanggal: String = "",
    @ColumnInfo(name = "doc_jam") var docJam: String = "",
    @ColumnInfo(name = "sign_kapten") var signKapten: String = "",
    @ColumnInfo(name = "sign_nama_kapten") var signNamaKapten: String = "",
    @ColumnInfo(name = "sign_petugas") var signPetugas: String = "",
    @ColumnInfo(name = "username") var username: String = "",
    @ColumnInfo(name = "sign_nama_petugas") var signNamaPetugas: String = "",
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "isUpload") var isUpload: Boolean = false,
) : Parcelable


@Entity
data class COPUpdateStatusModel(
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "isUpload") var isUpload: Boolean = false,
)