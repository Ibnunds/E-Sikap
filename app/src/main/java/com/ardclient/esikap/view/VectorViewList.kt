package com.ardclient.esikap.view

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ardclient.esikap.R
import com.ardclient.esikap.model.reusable.SanitasiListModel
import com.ardclient.esikap.model.reusable.SanitasiModel

class VectorViewList : ViewModel() {
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
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_dapur), type = "VEC", checkedKey = sanitasi.vecDapur),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_rakit), type = "VEC", checkedKey = sanitasi.vecRuangRakit),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_gudang), type = "VEC", checkedKey = sanitasi.vecGudang),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_palka), type = "VEC", checkedKey = sanitasi.vecPalka),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_tidur), type = "VEC", checkedKey = sanitasi.vecRuangTidur),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_abk), type = "VEC", checkedKey = sanitasi.vecABKReq),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_perwira), type = "VEC", checkedKey = sanitasi.vecPerwira),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_penumpang), type = "VEC", checkedKey = sanitasi.vecPenumpang),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_geladak), type = "VEC", checkedKey = sanitasi.vecGeladak),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_air_minum), type = "VEC", checkedKey = sanitasi.vecAirMinum),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_limba_cair), type = "VEC", checkedKey = sanitasi.vecLimbaCair),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_air_tergenang), type = "VEC", checkedKey = sanitasi.vecAirTergenang),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_ruang_mesin), type = "VEC", checkedKey = sanitasi.vecRuangMesin),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_fasilitas_medis), type = "VEC", checkedKey = sanitasi.vecFasilitasMedik),
            SanitasiListModel(title = ctx.getString(R.string.sanitasi_area_lainnya), type = "VEC", checkedKey = sanitasi.vecAreaLainnya)
        )

        listDataLiveData!!.postValue(listData)
    }
}