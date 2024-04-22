package com.ardclient.esikap.input.cop

import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardclient.esikap.R
import com.ardclient.esikap.adapter.DokumenInputAdapter
import com.ardclient.esikap.databinding.ActivityCopInputDokumenBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.model.reusable.DokumenKapalListModel
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.utils.InputValidation
import com.ardclient.esikap.view.DokumenViewModel

class CopInputDokumenActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var binding: ActivityCopInputDokumenBinding
    private var pickedDoc: String = ""
    private lateinit var copDocData: DokumenKapalModel

    // radio checked value
    private val radioMap = mutableMapOf<String, String?>()
    private val noteMap = mutableMapOf<String, String?>()
    private val docMap = mutableMapOf<String, String?>()

    private val listDataLiveData = MutableLiveData<List<DokumenKapalListModel>>()
    private lateinit var dokumenInputAdapter: DokumenInputAdapter

    private lateinit var viewModel: DokumenViewModel
    private lateinit var bindingListData: List<DokumenKapalListModel>

    private var isUpdate = false
    private var isUploaded = false

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
        }

        // check existing data
        val existingData = intent.getParcelableExtra<DokumenKapalModel>("EXISTING_DATA")
        if (existingData != null){
            copDocData = existingData
            isUpdate = true
            initExistingData()
        }else{
            copDocData = DokumenKapalModel()
        }

        // check is upload
        isUploaded = intent.getBooleanExtra("IS_UPLOAD", false)
        if (isUploaded){
            onUploadedUI()
        }

        // list input
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager


        // VM
        viewModel = ViewModelProvider(this)[DokumenViewModel::class.java]

        viewModel.getInitList(copDocData, applicationContext).observe(this, Observer { listData ->
            listDataLiveData.value = listData
            bindingListData = listData
            initListAdapter(listDataLiveData)
        })

        // Prefix radio
        binding.radioKarantina.setOnCheckedChangeListener {_, checkedId ->
            if (checkedId == R.id.radio_karantina_true){
                radioMap["KARANTINA"] = getString(R.string.quarantine_use)
            }else{
                radioMap["KARANTINA"] = getString(R.string.quarantine_not_use)
            }
        }

        binding.radioActivity.setOnCheckedChangeListener {_, checkedId ->
            if (checkedId == R.id.radio_activity_true){
                radioMap["ACTIVITY"] = getString(R.string.radio_istrue)
            }else{
                radioMap["ACTIVITY"] = getString(R.string.radio_isfalse)
            }
        }
    }

    private fun onUploadedUI() {
        with(binding){
            InputValidation.disableRadioGroup(radioKarantina)
            InputValidation.disableRadioGroup(radioActivity)
            saveButton.visibility = View.GONE
        }
    }

    private fun initExistingData() {
        // radio
        radioMap["MDH"] = copDocData.mdh
        radioMap["SSCEC"] = copDocData.sscec
        radioMap["P3K"] = copDocData.certP3K
        radioMap["BUKUKES"] = copDocData.bukuKesehatan
        radioMap["BUKUVAKSIN"] = copDocData.bukuVaksin
        radioMap["DAFTARABK"] = copDocData.daftarABK
        radioMap["DAFTARVAKSIN"] = copDocData.daftarVaksinasi
        radioMap["DAFTAROBAT"] = copDocData.daftarObat
        radioMap["DAFTARNARKOTIK"] = copDocData.daftarNarkotik
        radioMap["LPOC"] = copDocData.lpoc
        radioMap["SHIPPAR"] = copDocData.shipParticular
        radioMap["LPC"] = copDocData.lpc
        radioMap["BUKUKUNING"] = copDocData.bukuKuning
        radioMap["CATATANPERJALANAN"] = copDocData.catatanPerjalanan
        radioMap["IZINBERLAYAR"] = copDocData.izinBerlayar
        radioMap["DAFTARALKES"] = copDocData.daftarAlkes
        radioMap["DAFTARSTORE"] = copDocData.daftarStore

        // doc
        docMap["MDH"] = copDocData.mdhDoc
        docMap["SSCEC"] = copDocData.sscecDoc
        docMap["P3K"] = copDocData.p3kDoc
        docMap["BUKUKES"] = copDocData.bukuKesehatanDoc
        docMap["BUKUVAKSIN"] = copDocData.bukuVaksinDoc
        docMap["DAFTARABK"] = copDocData.daftarABKDoc
        docMap["DAFTARVAKSIN"] = copDocData.daftarVaksinasiDoc
        docMap["DAFTAROBAT"] = copDocData.daftarObatDoc
        docMap["DAFTARNARKOTIK"] = copDocData.daftarNarkotikDoc
        docMap["LPOC"] = copDocData.lpocDoc
        docMap["SHIPPAR"] = copDocData.shipParticularDoc
        docMap["LPC"] = copDocData.lpcDoc
        radioMap["BUKUKUNING"] = copDocData.bukuKuningDoc
        radioMap["CATATANPERJALANAN"] = copDocData.catatanPerjalananDoc
        radioMap["IZINBERLAYAR"] = copDocData.izinBerlayarDoc
        radioMap["DAFTARALKES"] = copDocData.daftarAlkesDoc
        radioMap["DAFTARSTORE"] = copDocData.daftarStoreDoc

        // doc
        noteMap["MDH"] = copDocData.mdhNote
        noteMap["SSCEC"] = copDocData.sscecNote
        noteMap["P3K"] = copDocData.p3kNote
        noteMap["BUKUKES"] = copDocData.bukuKesehatanNote
        noteMap["BUKUVAKSIN"] = copDocData.bukuVaksinNote
        noteMap["LPC"] = copDocData.lpcNote
        radioMap["BUKUKUNING"] = copDocData.bukuKuningNote
        radioMap["CATATANPERJALANAN"] = copDocData.catatanPerjalananNote
        radioMap["IZINBERLAYAR"] = copDocData.izinBerlayarNote
        radioMap["DAFTARALKES"] = copDocData.daftarAlkesNote
        radioMap["DAFTARSTORE"] = copDocData.daftarStoreNote


        // Prefix radio
        binding.radioKarantina.check(if (copDocData.isyaratKarantina == getString(R.string.quarantine_use)) R.id.radio_karantina_true else R.id.radio_karantina_false)
        binding.radioActivity.check(if (copDocData.aktifitasKapal == getString(R.string.radio_istrue)) R.id.radio_activity_true else R.id.radio_activity_false)
        radioMap["KARANTINA"] = copDocData.isyaratKarantina
        radioMap["ACTIVITY"] = copDocData.aktifitasKapal

        // input
        binding.etRekomendasi.editText?.setText(copDocData.rekomendasi)
    }

    private fun initListAdapter(listDataLiveData: MutableLiveData<List<DokumenKapalListModel>>) {
        // Inisialisasi adapter
        dokumenInputAdapter = DokumenInputAdapter(listDataLiveData, isUploaded, object : DokumenInputAdapter.UploadButtonListener {
            override fun onUploadButton(key: String) {
                pickDocument(key)
            }

            override fun onRadioChangedListener(key: String, radioVal: String) {
                radioMap[key] = radioVal
                if (radioVal == getString(R.string.radio_isfalse)){
                    docMap[key] = null
                }
                viewModel.updateRadioValue(key, radioVal, applicationContext)
            }

            override fun onNoteChanged(key: String, inputText: String?) {
                noteMap[key] = inputText.toString()
                viewModel.updateDocumentNote(key, inputText!!)
            }
        })

        binding.recyclerView.adapter = dokumenInputAdapter
    }

    private fun onSaveButton() {
        val isInputFilled = InputValidation.isAllFieldComplete(
            binding.etRekomendasi
        )

        if (radioMap["KARANTINA"] != null && radioMap["ACTIVITY"] != null && isInputFilled){
            var isDataComplete = true // Variabel flag untuk menandai apakah semua data lengkap

            for (item in bindingListData) {
                val key = item.key
                val valueInRadioMap = radioMap[key]
                val noteVal = noteMap[key]
                val docVal = docMap[key]

                if (valueInRadioMap.isNullOrBlank() || (valueInRadioMap == getString(R.string.radio_istrue) && docVal.isNullOrEmpty()) || (item.needNote && noteVal.isNullOrEmpty())) {
                    isDataComplete = false // Setel flag menjadi false jika ada data yang null
                    break // Hentikan loop jika data tidak lengkap
                }
            }

            if (!isDataComplete) {
                Toast.makeText(this@CopInputDokumenActivity, getString(R.string.data_not_completed), Toast.LENGTH_SHORT).show()
            } else {
                onDataCompleted()
            }
        }else{
            Toast.makeText(this@CopInputDokumenActivity, getString(R.string.data_not_completed), Toast.LENGTH_SHORT).show()
        }

    }

    private fun onDataCompleted() {
        val rekomendasi = binding.etRekomendasi.editText?.text.toString()

        val copDokumen = DokumenKapalModel(
            isyaratKarantina = radioMap["KARANTINA"] ?: "",
            aktifitasKapal = radioMap["ACTIVITY"] ?: "",
            mdh = radioMap["MDH"] ?: "",
            mdhDoc = docMap["MDH"] ?: "",
            mdhNote = noteMap["MDH"] ?: "",
            sscec = radioMap["SSCEC"] ?: "",
            sscecDoc = docMap["SSCEC"] ?: "",
            sscecNote = noteMap["SSCEC"] ?: "",
            certP3K = radioMap["P3K"] ?: "",
            p3kDoc = docMap["P3K"] ?: "",
            p3kNote = noteMap["P3K"] ?: "",
            bukuKesehatan = radioMap["BUKUKES"] ?: "",
            bukuKesehatanDoc = docMap["BUKUKES"] ?: "",
            bukuKesehatanNote = noteMap["BUKUKES"] ?: "",
            bukuVaksin = radioMap["BUKUVAKSIN"] ?: "",
            bukuVaksinDoc = docMap["BUKUVAKSIN"] ?: "",
            bukuVaksinNote = noteMap["BUKUVAKSIN"] ?: "",
            daftarABK = radioMap["DAFTARABK"] ?: "",
            daftarABKDoc = docMap["DAFTARABK"] ?: "",
            daftarVaksinasi = radioMap["DAFTARVAKSIN"] ?: "",
            daftarVaksinasiDoc = docMap["DAFTARVAKSIN"] ?: "",
            daftarObat = radioMap["DAFTAROBAT"] ?: "",
            daftarObatDoc = docMap["DAFTAROBAT"] ?: "",
            daftarNarkotik = radioMap["DAFTARNARKOTIK"] ?: "",
            daftarNarkotikDoc = docMap["DAFTARNARKOTIK"] ?: "",
            lpoc = radioMap["LPOC"] ?: "",
            lpocDoc = docMap["LPOC"] ?: "",
            shipParticular = radioMap["SHIPPAR"] ?: "",
            shipParticularDoc = docMap["SHIPPAR"] ?: "",
            lpc = radioMap["LPC"] ?: "",
            lpcDoc = docMap["LPC"] ?: "",
            lpcNote = noteMap["LPC"] ?: "",

            bukuKuning = radioMap["BUKUKUNING"] ?: "",
            bukuKuningDoc = docMap["BUKUKUNING"] ?: "",
            bukuKuningNote = noteMap["BUKUKUNING"] ?: "",

            catatanPerjalanan = radioMap["CATATANPERJALANAN"] ?: "",
            catatanPerjalananDoc = docMap["CATATANPERJALANAN"] ?: "",
            catatanPerjalananNote = noteMap["CATATANPERJALANAN"] ?: "",

            izinBerlayar = radioMap["IZINBERLAYAR"] ?: "",
            izinBerlayarDoc = docMap["IZINBERLAYAR"] ?: "",
            izinBerlayarNote = noteMap["IZINBERLAYAR"] ?: "",

            daftarAlkes = radioMap["DAFTARALKES"] ?: "",
            daftarAlkesDoc = docMap["DAFTARALKES"] ?: "",
            daftarAlkesNote = noteMap["DAFTARALKES"] ?: "",

            daftarStore = radioMap["DAFTARSTORE"] ?: "",
            daftarStoreDoc = docMap["DAFTARSTORE"] ?: "",
            daftarStoreNote = noteMap["DAFTARSTORE"] ?: "",
            rekomendasi = rekomendasi
        )

         intent.putExtra("COP_DOC", copDokumen)
        if (isUpdate){
            intent.putExtra("HAS_UPDATE", true)
        }
         setResult(RESULT_OK, intent)
         finish()
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
        viewModel.updateDocumentData(pickedDoc, uriString)
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