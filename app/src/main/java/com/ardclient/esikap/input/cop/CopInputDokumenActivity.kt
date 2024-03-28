package com.ardclient.esikap.input.cop

import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardclient.esikap.adapter.DokumenInputAdapter
import com.ardclient.esikap.databinding.ActivityCopInputDokumenBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.model.reusable.DokumenKapalListModel
import com.ardclient.esikap.model.reusable.DokumenKapalModel

class CopInputDokumenActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var binding: ActivityCopInputDokumenBinding
    private var pickedDoc: String = ""
    private lateinit var copDocData: DokumenKapalModel

    // document base64
    private var docMDH: String? = null
    private var docSSCEC: String? = null
    private var docVaksin: String? = null
    private var docABK: String? = null
    private var docBukuVaksin: String? = null
    private var docCertP3K: String? = null
    private var docBukuSehat: String? = null
    private var docLPOC: String? = null
    private var docShipParticular: String? = null
    private var docLPC: String? = null
    private var docNarkotik: String? = null
    private var docObat: String? = null

    // radio checked value
    private val radioMap = mutableMapOf<String, String>()
    private val noteMap = mutableMapOf<String, String>()
    private val docMap = mutableMapOf<String, String>()


    private var listData = ArrayList<DokumenKapalListModel>()
    private lateinit var dokumenInputAdapter: DokumenInputAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputDokumenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // button
        binding.saveButton.setOnClickListener {
            onSaveButton()
            //onSaveButtonTest()
        }

        // list input
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        // check existing data
        val existingData = intent.getParcelableExtra<DokumenKapalModel>("EXISTING_DATA")
        if (existingData != null){
            copDocData = existingData
            Log.d("EXISTING DATA", existingData.toString())
            //initExistingData()
        }else{
            copDocData = DokumenKapalModel()
        }

        // init lis data
        initListData()
    }

//    private fun onSaveButtonTest() {
//        intent.putExtra("COP_DOC", "testDoc")
//        setResult(RESULT_OK, intent)
//        finish()
//    }

    private fun initListData() {
        listData = arrayListOf(
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


        dokumenInputAdapter = DokumenInputAdapter(listData, object: DokumenInputAdapter.UploadButtonListener {
            override fun onUploadButton(key: String) {
                pickDocument(key)
            }

            override fun onRadioChangedListener(key: String, radioVal: String) {
                radioMap[key] = radioVal
                listData.find { it.key == key }?.apply {
                    docImage = if (radioVal == "Tidak ada") "" else docMap[key] ?: ""
                    checkedVal = radioVal
                    note = noteMap[key] ?: ""
                }
            }

            override fun onNoteChanged(key: String, inputText: CharSequence?) {
                noteMap[key] = inputText.toString()
                listData.find { it.key == key }?.apply {
                    docImage = docMap[key] ?: ""
                    checkedVal = radioMap[key] ?: ""
                    note = inputText.toString()
                }
            }
        })

        binding.recyclerView.adapter = dokumenInputAdapter
    }

    private fun initExistingData() {
        // text form

        // image form
        docMDH = copDocData.mdh
        docSSCEC = copDocData.sscec
        docVaksin = copDocData.daftarVaksinasi
        docABK = copDocData.daftarABK
        docBukuVaksin = copDocData.bukuVaksin
        docCertP3K = copDocData.certP3K
        docBukuSehat = copDocData.bukuKesehatan
        docLPOC = copDocData.lpoc
        docShipParticular = copDocData.shipParticular
        docLPC = copDocData.lpc
        docNarkotik = copDocData.daftarNarkotik
        docObat = copDocData.daftarObat
    }

    private fun onSaveButton() {
        var isDataComplete = true // Variabel flag untuk menandai apakah semua data lengkap

        for (item in listData) {
            val key = item.key
            val valueInRadioMap = radioMap[key]
            val noteVal = noteMap[key]

            if (valueInRadioMap.isNullOrBlank() || (valueInRadioMap == "Ada" && docMap[key].isNullOrEmpty()) || (item.needNote && noteVal.isNullOrEmpty())) {
                isDataComplete = false // Setel flag menjadi false jika ada data yang null
                break // Hentikan loop jika data tidak lengkap
            }
        }

        if (!isDataComplete) {
            Toast.makeText(this@CopInputDokumenActivity, "Data belum lengkap!", Toast.LENGTH_SHORT).show()
        } else {
            onDataCompleted()
        }
    }

    private fun onDataCompleted() {
        val copDokumen = DokumenKapalModel(
            mdh = radioMap["MDH"]!!,
            mdhDoc = docMap["MDH"]!!,
            mdhNote = noteMap["MDH"]!!,
            sscec = radioMap["SSCEC"]!!,
            sscecDoc = docMap["SSCEC"]!!,
            sscecNote = noteMap["SSCEC"]!!,
            certP3K = radioMap["P3K"]!!,
            p3kDoc = docMap["P3K"]!!,
            p3kNote = noteMap["P3K"]!!,
            bukuKesehatan = radioMap["BUKUKES"]!!,
            bukuKesehatanDoc = docMap["BUKUKES"]!!,
            bukuKesehatanNote = noteMap["BUKUKES"]!!,
            bukuVaksin = radioMap["BUKUVAKSIN"]!!,
            bukuVaksinDoc = docMap["BUKUVAKSIN"]!!,
            bukuVaksinNote = noteMap["BUKUVAKSIN"]!!,
            daftarABK = radioMap["DAFTARABK"]!!,
            daftarABKDoc = docMap["DAFTARABK"]!!,
            daftarVaksinasi = radioMap["DAFTARVAKSIN"]!!,
            daftarVaksinasiDoc = docMap["DAFTARVAKSIN"]!!,
            daftarObat = radioMap["DAFTAROBAT"]!!,
            daftarObatDoc = docMap["DAFTAROBAT"]!!,
            daftarNarkotik = radioMap["DAFTARNARKOTIK"]!!,
            daftarNarkotikDoc = docMap["DAFTARNARKOTIK"]!!,
            lpoc = radioMap["LPOC"]!!,
            lpocDoc = docMap["LPOC"]!!,
            shipParticular = radioMap["SHIPPAR"]!!,
            shipParticularDoc = docMap["SHIPPAR"]!!,
            lpc = radioMap["LPC"]!!,
            lpcDoc = docMap["LPC"]!!,
            lpcNote = noteMap["LPC"]!!
        )
        Log.d("HASIL_DATA", copDokumen.toString())

//         intent.putExtra("COP_DOC", copDokumen)
//         setResult(RESULT_OK, intent)
//         finish()
    }


    private fun pickDocument(documentType: String) {
        // dialog
        val imageSelectorDialog = ImageSelectorModal()

        pickedDoc = documentType
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }

    override fun onImageSelected(imageUri: Uri) {
        //val imageBase64 = Base64Utils.uriToBase64(this, imageUri)

        // apply to recycler view
        val uriString = imageUri.toString()
        docMap[pickedDoc] = uriString
        listData.find { it.key == pickedDoc }?.apply {
            docImage = uriString
            checkedVal = radioMap[pickedDoc] ?: ""
            note = noteMap[pickedDoc] ?: ""
        }

        dokumenInputAdapter.notifyDataSetChanged()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}