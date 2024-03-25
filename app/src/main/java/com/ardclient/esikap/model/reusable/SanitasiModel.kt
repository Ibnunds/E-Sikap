package com.ardclient.esikap.model.reusable

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class SanitasiModel(
    @ColumnInfo(name = "san_dapur") var sanDapur: String = "",
    @ColumnInfo(name = "san_ruang_rakit") var sanRuangRakit: String = "",
    @ColumnInfo(name = "san_gudang") var sanGudang: String = "",
    @ColumnInfo(name = "san_palka") var sanPalka: String = "",
    @ColumnInfo(name = "san_ruang_tidur") var sanRuangTidur: String = "",
    @ColumnInfo(name = "san_abk_req") var sanABKReq: String = "",
    @ColumnInfo(name = "san_perwira") var sanPerwira: String = "",
    @ColumnInfo(name = "san_penumpang") var sanPenumpang: String = "",
    @ColumnInfo(name = "san_geladak") var sanGeladak: String = "",
    @ColumnInfo(name = "san_air_minum") var sanAirMinum: String = "",
    @ColumnInfo(name = "san_limba_cair") var sanLimbaCair: String = "",
    @ColumnInfo(name = "san_air_tergenang") var sanAirTergenang: String = "",
    @ColumnInfo(name = "san_ruang_mesin") var sanRuangMesin: String = "",
    @ColumnInfo(name = "san_fasilitas_medik") var sanFasilitasMedik: String = "",
    @ColumnInfo(name = "san_area_lainnya") var sanAreaLainnya: String = "",
    @ColumnInfo(name = "vec_dapur") var vecDapur: String = "",
    @ColumnInfo(name = "vec_ruang_rakit") var vecRuangRakit: String = "",
    @ColumnInfo(name = "vec_gudang") var vecGudang: String = "",
    @ColumnInfo(name = "vec_palka") var vecPalka: String = "",
    @ColumnInfo(name = "vec_ruang_tidur") var vecRuangTidur: String = "",
    @ColumnInfo(name = "vec_abk_req") var vecABKReq: String = "",
    @ColumnInfo(name = "vec_perwira") var vecPerwira: String = "",
    @ColumnInfo(name = "vec_penumpang") var vecPenumpang: String = "",
    @ColumnInfo(name = "vec_geladak") var vecGeladak: String = "",
    @ColumnInfo(name = "vec_air_minum") var vecAirMinum: String = "",
    @ColumnInfo(name = "vec_limba_cair") var vecLimbaCair: String = "",
    @ColumnInfo(name = "resiko_sanitasi") var resikoSanitasi: String = "",
    @ColumnInfo(name = "masalah_kesehatan") var masalahKesehatan: String = "",
    @ColumnInfo(name = "masalah_kesehatan_catatan") var masalahKesehatanCatatan: String = "",
    @ColumnInfo(name = "masalah_kesehatan_file") var masalahKesehatanFile: String = "",
    @ColumnInfo(name = "rekomendasi") var rekomendasi: String = "",
) : Parcelable
