package com.ardclient.esikap.model.reusable

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class DokumenKapalModel(
    @ColumnInfo(name = "isyarat_karantina") var isyaratKarantina: String = "",
    @ColumnInfo(name = "aktifitasKapal") var aktifitasKapal: String = "",
    @ColumnInfo(name = "mdh") var mdh: String = "",
    @ColumnInfo(name = "mdh_doc") var mdhDoc: String = "",
    @ColumnInfo(name = "mdh_note") var mdhNote: String = "",
    @ColumnInfo(name = "sscec") var sscec: String = "",
    @ColumnInfo(name = "sscec_doc") var sscecDoc: String = "",
    @ColumnInfo(name = "sscec_note") var sscecNote: String = "",
    @ColumnInfo(name = "certP3K") var certP3K: String = "",
    @ColumnInfo(name = "certP3K_doc") var p3kDoc: String = "",
    @ColumnInfo(name = "p3k_note") var p3kNote: String = "",
    @ColumnInfo(name = "buku_kesehatan") var bukuKesehatan: String = "",
    @ColumnInfo(name = "buku_kesehatan_doc") var bukuKesehatanDoc: String = "",
    @ColumnInfo(name = "buku_kesehatan_note") var bukuKesehatanNote: String = "",
    @ColumnInfo(name = "buku_vaksin") var bukuVaksin: String = "",
    @ColumnInfo(name = "buku_vaksin_doc") var bukuVaksinDoc: String = "",
    @ColumnInfo(name = "buku_vaksin_note") var bukuVaksinNote: String = "",
    @ColumnInfo(name = "daftar_abk") var daftarABK: String = "",
    @ColumnInfo(name = "daftar_abk_doc") var daftarABKDoc: String = "",
    @ColumnInfo(name = "daftar_vaksinasi") var daftarVaksinasi: String = "",
    @ColumnInfo(name = "daftar_vaksinasi_doc") var daftarVaksinasiDoc: String = "",
    @ColumnInfo(name = "daftar_obat") var daftarObat: String = "",
    @ColumnInfo(name = "daftar_obat_doc") var daftarObatDoc: String = "",
    @ColumnInfo(name = "daftar_narkotik") var daftarNarkotik: String = "",
    @ColumnInfo(name = "daftar_narkotik_doc") var daftarNarkotikDoc: String = "",
    @ColumnInfo(name = "lastPortOffCall") var lpoc: String = "",
    @ColumnInfo(name = "lastPortOffCallDoc") var lpocDoc: String = "",
    @ColumnInfo(name = "lastPortClearance") var lpc: String = "",
    @ColumnInfo(name = "lastPortClearanceDoc") var lpcDoc: String = "",
    @ColumnInfo(name = "lastPortClearanceNote") var lpcNote: String = "",
    @ColumnInfo(name = "ship_particular") var shipParticular: String = "",
    @ColumnInfo(name = "ship_particular_doc") var shipParticularDoc: String = "",
) : Parcelable
