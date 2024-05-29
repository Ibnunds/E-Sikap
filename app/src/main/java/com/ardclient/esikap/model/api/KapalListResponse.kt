package com.ardclient.esikap.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class KapalListResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("nama_kapal") var namaKapal: String? = null,
    @SerializedName("nama_agen") var namaAgen : String? = null,
    @SerializedName("kapten_kapal") var kaptenKapal: String? = null,
    @SerializedName("gross_tone") var grossTone: String? = null,
    @SerializedName("bendera") var bendera : String? = null,
    @SerializedName("imo") var imo : String? = null,
    @SerializedName("negara_asal") var negaraAsal : String? = null,
    @SerializedName("tipe_kapal") var tipeKapal : String? = null,
    @SerializedName("tanggal_permintaan") var tanggalPermintaan : String? = null,
    @SerializedName("tanggal_diperiksa") var tanggalDiperiksa : String? = null,
    @SerializedName("tanggal_disetujui") var tanggalDisetujui : String? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("tipe_dokumen") var tipeDokumen : String? = null
) : Parcelable
