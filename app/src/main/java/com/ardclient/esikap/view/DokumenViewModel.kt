package com.ardclient.esikap.view

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ardclient.esikap.R
import com.ardclient.esikap.model.reusable.DokumenKapalListModel
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.utils.LocaleHelper

class DokumenViewModel : ViewModel() {
    private var listDataLiveData: MutableLiveData<List<DokumenKapalListModel>>? = null

    fun getInitList(copDocData: DokumenKapalModel, context: Context): MutableLiveData<List<DokumenKapalListModel>> {
        if (listDataLiveData == null){
            listDataLiveData = MutableLiveData()
            loadData(copDocData, context)
        }

        return listDataLiveData as MutableLiveData<List<DokumenKapalListModel>>
    }

    private fun loadData(copDocData: DokumenKapalModel, context: Context) {
        // apply locale
        LocaleHelper().applyLocale(context)

        val listData = arrayListOf(
            DokumenKapalListModel(context.getString(R.string.doc_mdh_title), "MDH", copDocData.mdh, copDocData.mdhDoc, copDocData.mdhNote,
                needNote = true,
                needDoc = true,
            ),
            DokumenKapalListModel(context.getString(R.string.doc_sscec_title), "SSCEC", copDocData.sscec, copDocData.sscecDoc, copDocData.sscecNote,
                needNote = true,
                needDoc = true,
            ),
            DokumenKapalListModel(context.getString(R.string.doc_p3k_title), "P3K", copDocData.certP3K, copDocData.p3kDoc, copDocData.p3kNote,
                needNote = true,
                needDoc = true,
            ),
            DokumenKapalListModel(context.getString(R.string.doc_bukukes_title), "BUKUKES", copDocData.bukuKesehatan, copDocData.bukuKesehatanDoc, copDocData.bukuKesehatanNote, true, needDoc = true),
            DokumenKapalListModel(context.getString(R.string.doc_bukuvaksin_title), "BUKUVAKSIN", copDocData.bukuVaksin, copDocData.bukuVaksinDoc, copDocData.bukuVaksinNote,
                needNote = false,
                needDoc = false,
            ),
            DokumenKapalListModel(context.getString(R.string.doc_daftarabk_title), "DAFTARABK", copDocData.daftarABK, copDocData.daftarABKDoc, "",
                needNote = false,
                needDoc = true
            ),
            DokumenKapalListModel(context.getString(R.string.doc_daftarvaksin_title), "DAFTARVAKSIN", copDocData.daftarVaksinasi, copDocData.daftarVaksinasiDoc, "",
                needNote = false,
                needDoc = true
            ),
            DokumenKapalListModel(context.getString(R.string.doc_daftarobat_title), "DAFTAROBAT", copDocData.daftarObat, copDocData.daftarObatDoc, "",
                needNote = false,
                needDoc = true
            ),
            DokumenKapalListModel(context.getString(R.string.doc_daftarnarkotik_title), "DAFTARNARKOTIK", copDocData.daftarNarkotik, copDocData.daftarNarkotikDoc, "",
                needNote = false,
                needDoc = true
            ),
            DokumenKapalListModel(context.getString(R.string.doc_lpoc_title), "LPOC", copDocData.lpoc, copDocData.lpocDoc, "",
                needNote = false,
                needDoc = true
            ),
            DokumenKapalListModel(context.getString(R.string.doc_shippar_title), "SHIPPAR", copDocData.shipParticular, copDocData.shipParticularDoc, "",
                needNote = false,
                needDoc = true
            ),
            DokumenKapalListModel(context.getString(R.string.doc_lpc_title), "LPC", copDocData.lpc, copDocData.lpcDoc, copDocData.lpcNote,
                needNote = true,
                needDoc = true,
            ),
//            DokumenKapalListModel(context.getString(R.string.doc_icv_title), "BUKUKUNING", copDocData.bukuKuning, copDocData.bukuKuningDoc, copDocData.bukuKuningNote,
//                needNote = true,
//                needDoc = true,
//            ),
            DokumenKapalListModel(context.getString(R.string.doc_noteperjalanan_title), "CATATANPERJALANAN", copDocData.catatanPerjalanan, copDocData.catatanPerjalananDoc, copDocData.catatanPerjalananNote,
                needNote = true,
                needDoc = true
            ),
            //DokumenKapalListModel(context.getString(R.string.doc_izinberlayar_title), "IZINBERLAYAR", copDocData.izinBerlayar, copDocData.izinBerlayarDoc, copDocData.izinBerlayarNote, true),
            //DokumenKapalListModel(context.getString(R.string.doc_alkes_title), "DAFTARALKES", copDocData.daftarAlkes, copDocData.daftarAlkesDoc, copDocData.daftarAlkesNote, true),
            DokumenKapalListModel(context.getString(R.string.doc_daftarstore_title), "DAFTARSTORE", copDocData.daftarStore, copDocData.daftarStoreDoc, copDocData.daftarStoreNote,
                needNote = true,
                needDoc = true
            ),
        )

        listDataLiveData!!.postValue(listData)

    }

    fun updateDocumentData(documentKey: String, imageUri: String) {
        val currentList = listDataLiveData?.value ?: return
        val updatedList = currentList.map { item ->
            if (item.key == documentKey) {
                item.copy(docImage = imageUri)
            } else {
                item
            }
        }
        listDataLiveData?.postValue(updatedList)
    }

    fun updateRadioValue(documentKey: String, checkedString: String, context: Context) {
        val currentList = listDataLiveData?.value ?: return
        val updatedList = currentList.map { item ->
            if (item.key == documentKey) {
                val updatedItem = item.copy(checkedVal = checkedString)
                if (checkedString == "Tidak ada") {
                    updatedItem.copy(docImage = "")
                } else {
                    updatedItem
                }
            } else {
                item
            }
        }
        listDataLiveData?.postValue(updatedList)
    }

    fun updateDocumentNote(documentKey: String, note: String) {
        val currentList = listDataLiveData?.value ?: return
        val updatedList = currentList.map { item ->
            if (item.key == documentKey) {
                item.copy(note = note)
            } else {
                item
            }
        }
        listDataLiveData?.postValue(updatedList)
    }
}