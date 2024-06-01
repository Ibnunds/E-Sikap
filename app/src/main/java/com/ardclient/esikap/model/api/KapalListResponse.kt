package com.ardclient.esikap.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class KapalListResponse(
    @SerializedName("id") var id: Int,
    @SerializedName("nama_kapal") var namaKapal: String? = null,
    @SerializedName("nama_agen") var namaAgen : String? = null,
    @SerializedName("kapten_kapal") var kaptenKapal: String? = null,
    @SerializedName("gross_tone") var grossTone: String? = null,
    @SerializedName("bendera") var bendera : String? = null,
    @SerializedName("imo") var imo : String? = null,
    @SerializedName("negara_asal") var negaraAsal : String? = null,
    @SerializedName("tipe_kapal") var tipeKapal : String? = null,
    @SerializedName("tanggal_permintaan") var tanggalPermintaan : String? = null,
    @SerializedName("status") var status : Int? = null,
    @SerializedName("tipe_dokumen") var tipeDokumen : String? = null,
    @SerializedName("tgl_diperiksa_phqc") var tglDiperiksaPHQC : String? = null,
    @SerializedName("tgl_diperiksa_sscec") var tglDiperiksaSSCEC : String? = null,
    @SerializedName("tgl_diperiksa_cop") var tglDiperiksaCOP : String? = null,
    @SerializedName("tgl_diperiksa_p3k") var tglDiperiksaP3K : String? = null,
    @SerializedName("dok_id_phqc") var dokIdPHQC : String? = null,
    @SerializedName("dok_id_sscec") var dokIdSSCEC : String? = null,
    @SerializedName("dok_id_cop") var dokIdCOP : String? = null,
    @SerializedName("dok_id_p3k") var dokIdP3K : String? = null,
) : Parcelable
