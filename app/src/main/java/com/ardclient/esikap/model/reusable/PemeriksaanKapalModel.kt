package com.ardclient.esikap.model.reusable

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class PemeriksaanKapalModel(
    @ColumnInfo(name = "peralatan_p3k") var peralatanP3K: String = "",
    @ColumnInfo(name = "oxygen_emergency") var oxygenEmergency: String = "",
    @ColumnInfo(name = "fasilitas_medis") var fasilitasMedis: String = "",
    @ColumnInfo(name = "obat_antibiotik") var obatAntibiotik: String = "",
    @ColumnInfo(name = "obat_analgesik") var obatAnalgesik: String = "",
    @ColumnInfo(name = "obat_lainnya") var obatLainnya: String = "",
    @ColumnInfo(name = "obat_narkotik") var obatNarkotik: String = "",
) : Parcelable
