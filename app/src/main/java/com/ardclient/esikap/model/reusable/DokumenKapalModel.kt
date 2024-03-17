package com.ardclient.esikap.model.reusable

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class DokumenKapalModel(
    @ColumnInfo(name = "isyarat_karantina") var isyaratKarantina: String = "",
    @ColumnInfo(name = "aktifitasKapal") var aktifitasKapal: String = "",
    @ColumnInfo(name = "mdh") var mdh: String = "",
    @ColumnInfo(name = "sscec") var sscec: String = "",
    @ColumnInfo(name = "daftar_vaksinasi") var daftarVaksinasi: String = "",
    @ColumnInfo(name = "daftar_abk") var daftarABK: String = "",
    @ColumnInfo(name = "buku_kuning") var bukuKuning: String = "",
    @ColumnInfo(name = "certP3K") var certP3K: String = "",
    @ColumnInfo(name = "buku_kesehatan") var bukuKesehatan: String = "",
    @ColumnInfo(name = "catatan_perjalanan") var catatanPerjalanan: String = "",
    @ColumnInfo(name = "ship_particular") var shipParticular: String = "",
    @ColumnInfo(name = "izinBerlayar") var izinBerlayar: String = "",
    @ColumnInfo(name = "daftar_narkotik") var daftarNarkotik: String = "",
    @ColumnInfo(name = "daftar_obat") var daftarObat: String = "",
    @ColumnInfo(name = "daftar_alkes") var daftarAlkes: String = "",
) : Parcelable
