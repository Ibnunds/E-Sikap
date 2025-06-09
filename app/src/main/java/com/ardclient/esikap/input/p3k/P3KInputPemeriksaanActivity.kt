package com.ardclient.esikap.input.p3k

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.ActivityP3kInputPemeriksaanBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.model.reusable.PemeriksaanKapalModel
import com.ardclient.esikap.utils.InputValidation
import com.ardclient.esikap.utils.LocaleHelper
import com.squareup.picasso.Picasso

class P3KInputPemeriksaanActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var binding: ActivityP3kInputPemeriksaanBinding
    private lateinit var pemeriksaanKapal: PemeriksaanKapalModel

    private var masalahDoc: String = ""
    private var needExtra: Boolean = false

    private var isUpdate = false
    private var isUploaded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityP3kInputPemeriksaanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // existing data
        val existingData = intent.getParcelableExtra<PemeriksaanKapalModel>("EXISTING_DATA")
        if (existingData != null){
            pemeriksaanKapal = existingData
            initExistingData()
            isUpdate = true
        }else{
            pemeriksaanKapal = PemeriksaanKapalModel()
        }

        // check is upload
        isUploaded = intent.getBooleanExtra("IS_UPLOAD", false)
        if (isUploaded){
            onUploadedUI()
        }

        // button
        binding.saveButton.setOnClickListener {
            onSaveButton()
        }

        // on issue checked
        binding.radioMasalah.setOnCheckedChangeListener{_, checkedId ->
            if (checkedId == R.id.radio_masalah_true){
                needExtra = true
                binding.masalahFileLayout.visibility = View.VISIBLE
                binding.etMasalahNote.visibility = View.GONE
            }else{
                needExtra = false
                binding.masalahFileLayout.visibility = View.GONE
                binding.etMasalahNote.visibility = View.VISIBLE
            }
        }

        binding.btnSelectMasalah.setOnClickListener {
            pickDocument()
        }
    }

    private fun onUploadedUI() {
        with(binding){
            InputValidation.disabledAllRadio(
                radioPeralatanP3K,
                radioOxygen,
                radioFasilitasMedis,
                radioAnalgesik,
                radioAntibiotik,
                radioObatLainnya,
                radioNarkotik,
                radioResiko,
                radioMasalah
            )

            btnSelectMasalah.visibility = View.GONE
            etMasalahNote.editText?.isEnabled = false

            saveButton.visibility = View.GONE
        }

    }

    private fun initExistingData() {
        val peralatanP3KId = when (getCheckedIntByString(pemeriksaanKapal.peralatanP3K)) {
            1 -> R.id.radio_peralatanp3k_true
            2 -> R.id.radio_peralatanp3k_false
            else -> R.id.radio_peralatanp3k_failed
        }

        val oksigenId = when(getCheckedIntByString(pemeriksaanKapal.oxygenEmergency)) {
            1 -> R.id.radio_oxygen_true
            2 -> R.id.radio_oxygen_false
            else -> R.id.radio_oxygen_failed
        }

        val faskesId = when(getCheckedIntByString(pemeriksaanKapal.fasilitasMedis)) {
            1 -> R.id.radio_faskes_true
            2 -> R.id.radio_faskes_false
            else -> R.id.radio_faskes_failed
        }

        val antibiotikId = when(getCheckedIntByString(pemeriksaanKapal.obatAntibiotik)) {
            1 -> R.id.radio_antibiotik_true
            2 -> R.id.radio_antibiotik_false
            else -> R.id.radio_antibiotik_failed
        }

        val analgesikId = when(getCheckedIntByString(pemeriksaanKapal.obatAnalgesik)) {
            1 -> R.id.radio_analgesik_true
            2 -> R.id.radio_analgesik_false
            else -> R.id.radio_analgesik_failed
        }

        val obatLainnyaId = when(getCheckedIntByString(pemeriksaanKapal.obatLainnya)) {
            1 -> R.id.radio_obatlainnya_true
            2 -> R.id.radio_obatlainnya_false
            else -> R.id.radio_obatlainnya_failed
        }

        val narkotikId = when(getCheckedIntByString(pemeriksaanKapal.obatNarkotik)){
            1 -> R.id.radio_narkotik_true
            2 -> R.id.radio_narkotik_false
            else -> R.id.radio_narkotik_failed
        }

        val resikoId = when(getCheckedIntByString(pemeriksaanKapal.resiko)) {
            1 -> R.id.radio_resiko_tinggi
            2 -> R.id.radio_resiko_rendah
            else -> R.id.radio_resiko_no
        }

        val masalahId = when(getCheckedIntByString(pemeriksaanKapal.masalah)) {
            1 -> R.id.radio_masalah_true
            else -> R.id.radio_masalah_false
        }

        with(binding) {
            radioPeralatanP3K.check(peralatanP3KId)
            radioOxygen.check(oksigenId)
            radioFasilitasMedis.check(faskesId)
            radioAntibiotik.check(antibiotikId)
            radioAnalgesik.check(analgesikId)
            radioObatLainnya.check(obatLainnyaId)
            radioNarkotik.check(narkotikId)
            radioResiko.check(resikoId)
            radioMasalah.check(masalahId)

            if (getCheckedIntByString(pemeriksaanKapal.masalah) == 1){
                needExtra = true
                binding.masalahFileLayout.visibility = View.VISIBLE
                binding.etMasalahNote.visibility = View.GONE
            }else{
                binding.etMasalahNote.visibility = View.VISIBLE
                binding.etMasalahNote.editText?.setText(pemeriksaanKapal.masalahCatatan)
            }
        }

        Log.d("FILE", pemeriksaanKapal.masalahFile)

        // document
        if (getCheckedIntByString(pemeriksaanKapal.masalah) == 1){
            binding.etMasalahNote.visibility = View.GONE
            binding.btnSelectMasalah.text = getString(R.string.update_dokumen_title)
            binding.prevMasalah.visibility  =View.VISIBLE
            Picasso.get().load(pemeriksaanKapal.masalahFile).fit().into(binding.prevMasalah)

            masalahDoc = pemeriksaanKapal.masalahFile
        }else{
            binding.etMasalahNote.editText?.setText(pemeriksaanKapal.masalahCatatan)
        }
    }

    private fun getCheckedIntByString(title: String): Int {
        return when (title) {
            "Lengkap", "Resiko tinggi", "Ada" -> {
                1
            }
            "Tidak lengkap", "Resiko rendah", "Tidak ada" -> {
                2
            }
            else -> {
                3
            }
        }
    }

    private fun onSaveButton() {
        with(binding) {
            val isAllFilled = InputValidation.isAllRadioFilled(
                radioPeralatanP3K,
                radioOxygen,
                radioFasilitasMedis,
                radioAntibiotik,
                radioAnalgesik,
                radioObatLainnya,
                radioNarkotik,
                radioResiko,
                radioMasalah
            )

            val isInputFilled = InputValidation.isAllFieldComplete(
                etMasalahNote
            )

            if (isAllFilled) {
                val isValid = if (needExtra) !masalahDoc.isNullOrBlank() else isInputFilled
                if (isValid) {
                    onValidInput()
                } else {
                    Toast.makeText(this@P3KInputPemeriksaanActivity, getString(R.string.data_not_completed), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@P3KInputPemeriksaanActivity, getString(R.string.data_not_completed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onValidInput() {
       with(binding) {
           val peralatanP3KValue = InputValidation.getSelectedRadioGroupValue(this, radioPeralatanP3K)
           val oksigenVal = InputValidation.getSelectedRadioGroupValue(this, radioOxygen)
           val faskesVal = InputValidation.getSelectedRadioGroupValue(this, radioFasilitasMedis)
           val antibiotikVal = InputValidation.getSelectedRadioGroupValue(this, radioAntibiotik)
           val analgesikVal = InputValidation.getSelectedRadioGroupValue(this, radioAnalgesik)
           val narkotikVal = InputValidation.getSelectedRadioGroupValue(this, radioNarkotik)
           val obatLainnyaVal = InputValidation.getSelectedRadioGroupValue(this, radioObatLainnya)
           val resikoVal = InputValidation.getSelectedRadioGroupValue(this, radioResiko)
           val masalahVal = InputValidation.getSelectedRadioGroupValue(this, radioMasalah)
           val masalahNote = if (needExtra) etMasalahNote.editText?.text.toString() else "-"

           // trans

           val pemeriksaanData = PemeriksaanKapalModel(
               peralatanP3K = transValue(peralatanP3KValue),
               oxygenEmergency = transValue(oksigenVal),
               fasilitasMedis = transValue(faskesVal),
               obatAntibiotik = transValue(antibiotikVal),
               obatAnalgesik = transValue(analgesikVal),
               obatNarkotik = transValue(narkotikVal),
               obatLainnya = transValue(obatLainnyaVal),
               resiko = transValue(resikoVal),
               masalah = transValue(masalahVal, true),
               masalahCatatan = masalahNote,
               masalahFile = masalahDoc
           )

           val intent = Intent(this@P3KInputPemeriksaanActivity, P3KInputActivity::class.java)
           intent.putExtra("PEMERIKSAAN", pemeriksaanData)
           if (isUpdate){
               intent.putExtra("HAS_UPDATE", true)
           }
           setResult(RESULT_OK, intent)
           finish()
       }
    }

    private fun transValue(selected: String, masalah: Boolean? = false): String {
        return when(selected) {
            "Complete" -> "Lengkap"
            "Incomplete" -> "Tidak lengkap"
            "Not available" -> if (masalah == true) "Tidak ada" else "Tidak tersedia"
            "High risk" -> "Resiko tinggi"
            "Low risk" -> "Resiko rendah"
            "No risk" -> "Tidak ada resiko"
            "Available" -> "Ada"
            else -> selected
        }
    }

    private fun pickDocument() {
        // dialog
        val imageSelectorDialog = ImageSelectorModal()
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }

    override fun onImageSelected(imageUri: Uri) {
        val uriString = imageUri.toString()
        masalahDoc = uriString
        binding.btnSelectMasalah.text = getString(R.string.update_dokumen_title)
        binding.prevMasalah.visibility  =View.VISIBLE
        Picasso.get().load(uriString).fit().into(binding.prevMasalah)
    }

    override fun attachBaseContext(base: Context?) {
        LocaleHelper().setLocale(base!!, LocaleHelper().getLanguage(base))
        super.attachBaseContext(LocaleHelper().onAttach(base))
    }
}