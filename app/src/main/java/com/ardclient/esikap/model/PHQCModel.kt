package com.ardclient.esikap.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "phqc")
@Parcelize
data class PHQCModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "kapalId") var kapalId: Int = 0,
    @Embedded(prefix = "kapal_") var kapal: KapalModel = KapalModel(),
    @ColumnInfo(name = "tujuan") var tujuan: String = "",
    @ColumnInfo(name = "dokumen_kapal") var dokumenKapal: String = "",
    @ColumnInfo(name = "lokasi_pemeriksaan") var lokasiPemeriksaan: String = "",
    @ColumnInfo(name = "jenis_layanan") var jenisLayanan: String = "",
    @ColumnInfo(name = "jenis_pelayaran") var jenisPelayaran: String = "",
    @ColumnInfo(name = "jumlah_abk") var jumlahABK: Int = 0,
    @ColumnInfo(name = "deteksi_demam") var deteksiDemam: Int = 0,
    @ColumnInfo(name = "jumlah_sehat") var jumlahSehat: Int = 0,
    @ColumnInfo(name = "jumlah_sakit") var jumlahSakit: Int = 0,
    @ColumnInfo(name = "jumlah_meninggal") var jumlahMeninggal: Int = 0,
    @ColumnInfo(name = "jumlah_dirujuk") var jumlahDirujuk: Int = 0,
    @ColumnInfo(name = "jumlah_cust") var jumlahPenumpang: Int = 0,
    @ColumnInfo(name = "cust_deteksi_demam") var custDeteksiDemam: Int = 0,
    @ColumnInfo(name = "cust_jumlah_penumpang") var custjumlahPenumpang: Int = 0,
    @ColumnInfo(name = "cust_jumlah_sehat") var custJumlahSehat: Int = 0,
    @ColumnInfo(name = "cust_jumlah_sakit") var custJumlahSakit: Int = 0,
    @ColumnInfo(name = "cust_jumlah_meninggal") var custJumlahMeninggal: Int = 0,
    @ColumnInfo(name = "cust_jumlah_dirujuk") var custJumlahDirujuk: Int = 0,
    @ColumnInfo(name = "pemeriksaan_file") var pemeriksaanFile: String = "",
    @ColumnInfo(name = "status_sanitasi") var statusSanitasi: String = "",
    @ColumnInfo(name = "kesimpulan") var kesimpulan: String = "",
    @ColumnInfo(name = "masalah_kesehatan") var masalahKesehatan: String = "",
    @ColumnInfo(name = "petugas_pelaksana") var petugasPelaksana: String = "",
    @ColumnInfo(name = "kapten") var kapten: String = "",
    @ColumnInfo(name = "tanggal_diperiksa") var tanggalDiperiksa: String = "",
    @ColumnInfo(name = "jam_diperiksa") var jamDiperiksa: String = "",
    @ColumnInfo(name = "signature") var signature: String = "",
    @ColumnInfo(name = "username") var username: String = "",
    @ColumnInfo(name = "signature_kapten") var signatureKapten: String = "",
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "isUpload") var isUpload: Boolean = false,
    @ColumnInfo(name = "nama_pt2") var namaPetugas2: String = "",
    @ColumnInfo(name = "sign_pt2") var signPetugas2: String = "",
    @ColumnInfo(name = "nip_pt2") var nipPetugas2: String = "",
    @ColumnInfo(name = "nama_pt3") var namaPetugas3: String = "",
    @ColumnInfo(name = "sign_pt3") var signPetugas3: String = "",
    @ColumnInfo(name = "nip_pt3") var nipPetugas3: String = "",
    @ColumnInfo(name = "flag") var flag: String = kapal.flag,
) : Parcelable

@Entity
data class PHQCStatusUpdateModel(
    @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "isUpload") var isUpload: Boolean = false,
)
