package com.ardclient.esikap.view

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ardclient.esikap.R
import com.ardclient.esikap.model.reusable.SanitasiListModel
import com.ardclient.esikap.model.reusable.SanitasiModel

class SanitasiViewModel : ViewModel() {
    private var listDataLiveData: MutableLiveData<List<SanitasiListModel>>? = null

    fun getInitList(sanitasi: SanitasiModel, context: Context): MutableLiveData<List<SanitasiListModel>> {
        if (listDataLiveData == null){
            listDataLiveData = MutableLiveData()
            loadData(sanitasi, context)
        }

        return listDataLiveData as MutableLiveData<List<SanitasiListModel>>
    }

    private fun loadData(sanitasi: SanitasiModel, ctx: Context) {
        val listData = arrayListOf(
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_dapur), type = "SAN", checkedKey = sanitasi.sanDapur),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_rakit), type = "SAN", checkedKey = sanitasi.sanRuangRakit),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_gudang), type = "SAN", checkedKey = sanitasi.sanGudang),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_palka), type = "SAN", checkedKey = sanitasi.sanPalka),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_tidur), type = "SAN", checkedKey = sanitasi.sanRuangTidur),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_abk), type = "SAN", checkedKey = sanitasi.sanABKReq),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_perwira), type = "SAN", checkedKey = sanitasi.sanPerwira),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_penumpang), type = "SAN", checkedKey = sanitasi.sanPenumpang),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_geladak), type = "SAN", checkedKey = sanitasi.sanGeladak),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_air_minum), type = "SAN", checkedKey = sanitasi.sanAirMinum),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_limba_cair), type = "SAN", checkedKey = sanitasi.sanLimbaCair),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_air_tergenang), type = "SAN", checkedKey = sanitasi.sanAirTergenang),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_mesin), type = "SAN", checkedKey = sanitasi.sanRuangMesin),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_fasilitas_medis), type = "SAN", checkedKey = sanitasi.sanFasilitasMedik),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_area_lainnya), type = "SAN", checkedKey = sanitasi.sanAreaLainnya)
        )

        listDataLiveData!!.postValue(listData)

    }
}