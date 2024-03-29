package com.ardclient.esikap.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ardclient.esikap.model.reusable.DokumenKapalListModel
import com.ardclient.esikap.model.reusable.DokumenKapalModel

class DokumenViewModel : ViewModel() {
    private var listDataLiveData: MutableLiveData<List<DokumenKapalListModel>>? = null

    fun getInitList(copDocData: DokumenKapalModel): MutableLiveData<List<DokumenKapalListModel>> {
        if (listDataLiveData == null){
            listDataLiveData = MutableLiveData()
            loadData(copDocData)
        }

        return listDataLiveData as MutableLiveData<List<DokumenKapalListModel>>
    }

    private fun loadData(copDocData: DokumenKapalModel) {
        val listData = arrayListOf(
            DokumenKapalListModel("Dokumen MDH", "MDH", copDocData.mdh, copDocData.mdhDoc, copDocData.mdhNote, true),
            DokumenKapalListModel("Dokumen SSCEC", "SSCEC", copDocData.sscec, copDocData.sscecDoc, copDocData.sscecNote, true),
            DokumenKapalListModel("Dokumen P3K", "P3K", copDocData.certP3K, copDocData.p3kDoc, copDocData.p3kNote, true),
            DokumenKapalListModel("Dokumen Buku Kesehatan", "BUKUKES", copDocData.bukuKesehatan, copDocData.bukuKesehatanDoc, copDocData.bukuKesehatanNote, true),
            DokumenKapalListModel("Dokumen Buku Vaksin", "BUKUVAKSIN", copDocData.bukuVaksin, copDocData.bukuVaksinDoc, copDocData.bukuVaksinNote, true),
            DokumenKapalListModel("Dokumen Daftar ABK", "DAFTARABK", copDocData.daftarABK, copDocData.daftarABKDoc, "", false),
            DokumenKapalListModel("Dokumen Daftar Vaksin", "DAFTARVAKSIN", copDocData.daftarVaksinasi, copDocData.daftarVaksinasiDoc, "", false),
            DokumenKapalListModel("Dokumen Daftar Obat", "DAFTAROBAT", copDocData.daftarObat, copDocData.daftarObatDoc, "", false),
            DokumenKapalListModel("Dokumen Daftar Narkotik", "DAFTARNARKOTIK", copDocData.daftarNarkotik, copDocData.daftarNarkotikDoc, "", false),
            DokumenKapalListModel("Dokumen Last Port Off Call", "LPOC", copDocData.lpoc, copDocData.lpocDoc, "", false),
            DokumenKapalListModel("Dokumen Ship Particular", "SHIPPAR", copDocData.shipParticular, copDocData.shipParticularDoc, "", false),
            DokumenKapalListModel("Dokumen Last Port Clearance", "LPC", copDocData.lpc, copDocData.lpcDoc, copDocData.lpcNote, true)
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

    fun updateRadioValue(documentKey: String, checkedString: String) {
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