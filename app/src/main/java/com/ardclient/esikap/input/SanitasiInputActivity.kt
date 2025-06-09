package com.ardclient.esikap.input

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.ardclient.esikap.R
import com.ardclient.esikap.input.cop.CopInputActivity
import com.ardclient.esikap.databinding.ActivitySanitasiInputBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.model.reusable.SanitasiModel
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.InputValidation
import com.ardclient.esikap.utils.LocaleHelper
import com.squareup.picasso.Picasso

class SanitasiInputActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var binding: ActivitySanitasiInputBinding
    private lateinit var copSanitasi: SanitasiModel

    private var selectedDocType: String = ""
    private var masalahDoc: String? = null
    private var hasMasalah: Boolean = false
    private var hasilDoc: String? = null

    private var isUpdate = false
    private var isUploaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySanitasiInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // existing data
        val existingData = intent.getParcelableExtra<SanitasiModel>("EXISTING_DATA")
        if (existingData != null){
            copSanitasi = existingData
            isUpdate = true
            initExistingData()
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

        // on health issue checked
        binding.radioHealth.setOnCheckedChangeListener{_, checkedId ->
            if (checkedId == R.id.radio_health_true){
                hasMasalah = true
                binding.healthFileLayout.visibility = View.VISIBLE
                binding.etMasalahNote.visibility = View.VISIBLE
                binding.etCatatanKesehatan.visibility = View.GONE
            }else{
                hasMasalah = false
                masalahDoc = null
                binding.healthFileLayout.visibility = View.GONE
                binding.etMasalahNote.visibility = View.GONE
                binding.etCatatanKesehatan.visibility = View.VISIBLE

            }
        }

        binding.btnSelectMasalah.setOnClickListener {
            selectedDocType = "MASALAH"
            pickDocument()
        }

        binding.btnSelectHasil.setOnClickListener {
            selectedDocType = "HASIL"
            pickDocument()
        }
    }

    private fun onUploadedUI() {
        with(binding){
            InputValidation.disabledAllRadio(
                radioDapur,
                radioDapurVec,
                radioPantry,
                radioPantryVec,
                radioGudang,
                radioGudangVec,
                radioPalka,
                radioPalkaVec,
                radioRuangTidur,
                radioRuangTidurVec,
                radioABK,
                radioABKVec,
                radioPerwira,
                radioPerwiraVec,
                radioPenumpang,
                radioPenumpangVec,
                radioGeladak,
                radioGeladakVec,
                radioWater,
                radioWaterVec,
                radioLimbaCair,
                radioLimbaCairVec,
                radioGenangan,
                radioAirTergenangVec,
                radioEngine,
                radioMesinVec,
                radioMedik,
                radioMedikVec,
                radioOtherArea,
                radioOtherAreaVec,
                radioRekomendasi,
                radioResiko,
                radioHealth,
                radioVektor
            )

            etMasalahNote.editText?.isEnabled = false
            etCatatanKesehatan.editText?.isEnabled = false

            btnSelectHasil.visibility = View.GONE
            btnSelectMasalah.visibility = View.GONE
            saveButton.visibility = View.GONE
        }
    }

    fun getRadioButtonId(value: Int, trueId: Int, falseId: Int, neutralId: Int? = null): Int {
        return when (value) {
            1 -> trueId
            2 -> falseId
            else -> neutralId ?: falseId // Default ke falseId jika neutralId tidak ada
        }
    }

    private fun initExistingData() {

        // Dapur
        val dapurVal = getRadioButtonId(getCheckedIdByString(copSanitasi.sanDapur), R.id.radio_dapur_true, R.id.radio_dapur_false, R.id.radio_dapur_neutral)
        val dapurVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecDapur), R.id.radio_dapurVec_true, R.id.radio_dapurVec_false, R.id.radio_dapurVec_neutral)

        // Pantry
        val pantryValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanRuangRakit), R.id.radio_pantry_true, R.id.radio_pantry_false, R.id.radio_pantry_neutral)
        val pantryVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecRuangRakit), R.id.radio_pantryVec_true, R.id.radio_pantryVec_false, R.id.radio_pantryVec_neutral)

        // Gudang
        val gudangValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanGudang), R.id.radio_gudang_true, R.id.radio_gudang_false, R.id.radio_gudang_neutral)
        val gudangVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecGudang), R.id.radio_gudangVec_true, R.id.radio_gudangVec_false, R.id.radio_gudangVec_neutral)

        // Palka
        val palkaValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanPalka), R.id.radio_palka_true, R.id.radio_palka_false, R.id.radio_palka_neutral)
        val palkaVecValue =  getRadioButtonId(getCheckedIdByString(copSanitasi.vecPalka), R.id.radio_palkaVec_true, R.id.radio_palkaVec_false, R.id.radio_palkaVec_neutral)

        // Ruang tidur
        val ruangTidurValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanRuangTidur), R.id.radio_quarter_true, R.id.radio_quarter_false, R.id.radio_quarter_neutral)
        val ruangTidurVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecRuangTidur), R.id.radio_quarterVec_true, R.id.radio_quarterVec_false, R.id.radio_quarterVec_neutral)

        // Abk
        val abkValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanABKReq), R.id.radio_abk_true, R.id.radio_abk_false, R.id.radio_abk_neutral)
        val abkVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecABKReq), R.id.radio_abkVec_true, R.id.radio_abkVec_false, R.id.radio_abkVec_neutral)

        // Perwira
        val perwiraValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanPerwira), R.id.radio_perwira_true, R.id.radio_perwira_false, R.id.radio_perwira_neutral)
        val perwiraVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecPerwira), R.id.radio_perwiraVec_true, R.id.radio_perwiraVec_false, R.id.radio_perwiraVec_neutral)

        // Penumpang
        val penumpangValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanPenumpang), R.id.radio_penumpang_true, R.id.radio_penumpang_false, R.id.radio_penumpang_neutral)
        val penumpangVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecPenumpang), R.id.radio_penumpangVec_true, R.id.radio_penumpangVec_false, R.id.radio_penumpangVec_neutral)

        // Geladak
        val geladakValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanGeladak), R.id.radio_deck_true, R.id.radio_deck_false, R.id.radio_deck_neutral)
        val geladakVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecGeladak), R.id.radio_deckVec_true, R.id.radio_deckVec_false, R.id.radio_deckVec_neutral)

        // Water
        val waterValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanAirMinum), R.id.radio_water_true, R.id.radio_water_false, R.id.radio_water_neutral)
        val waterVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecAirMinum), R.id.radio_waterVec_true, R.id.radio_waterVec_false, R.id.radio_waterVec_neutral)

        // Limba
        val limbaCairValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanLimbaCair), R.id.radio_limba_true, R.id.radio_limba_false, R.id.radio_limba_neutral)
        val limbaCairVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecLimbaCair), R.id.radio_limbaVec_true, R.id.radio_limbaVec_false, R.id.radio_limbaVec_neutral)


        // Genangan
        val genanganValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanAirTergenang), R.id.radio_genangan_true, R.id.radio_genangan_false, R.id.radio_genangan_neutral)
        val genanganVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecAirTergenang), R.id.radio_airTergenangVec_true, R.id.radio_airTergenangVec_false, R.id.radio_airTergenangVec_neutral)


        // Engine
        val engineValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanRuangMesin), R.id.radio_engine_true, R.id.radio_engine_false, R.id.radio_engine_neutral)
        val engineVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecRuangMesin), R.id.radio_mesinVec_true, R.id.radio_mesinVec_false, R.id.radio_mesinVec_neutral)


         // Medik
        val medikValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanFasilitasMedik), R.id.radio_medik_true, R.id.radio_medik_false, R.id.radio_medik_neutral)
        val medikVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecFasilitasMedik), R.id.radio_medikVec_true, R.id.radio_medikVec_false, R.id.radio_medikVec_neutral)


        // Other
        val otherAreaValue = getRadioButtonId(getCheckedIdByString(copSanitasi.sanAreaLainnya), R.id.radio_otherArea_true, R.id.radio_otherArea_false, R.id.radio_otherArea_neutral)
        val otherAreaVecValue = getRadioButtonId(getCheckedIdByString(copSanitasi.vecAreaLainnya), R.id.radio_otherAreaVec_true, R.id.radio_otherAreaVec_false, R.id.radio_otherVec_neutral)

        val rekomendasiValue = if (getCheckedIdByString(copSanitasi.rekomendasi) == 1) R.id.radio_disinseksi else if (getCheckedIdByString(copSanitasi.rekomendasi) == 2) R.id.radio_fumigasi else R.id.radio_no_problem
        val resikoValue = if (getCheckedIdByString(copSanitasi.resikoSanitasi) == 1) R.id.radio_resiko_tinggi else if (getCheckedIdByString(copSanitasi.resikoSanitasi) == 2) R.id.radio_resiko_rendah else R.id.radio_resiko_no
        val healthValue = if (getCheckedIdByString(copSanitasi.masalahKesehatan) == 1) R.id.radio_health_true else R.id.radio__health_false

        val vektorValue = if (getCheckedIdByString(copSanitasi.tandatandaVektor) == 1) R.id.radio_vektor_true else R.id.radio_vektor_false


        // asigning
        binding.radioDapur.check(dapurVal)
        binding.radioDapurVec.check(dapurVecValue)
        binding.radioPantry.check(pantryValue)
        binding.radioPantryVec.check(pantryVecValue)
        binding.radioGudang.check(gudangValue)
        binding.radioGudangVec.check(gudangVecValue)
        binding.radioPalka.check(palkaValue)
        binding.radioPalkaVec.check(palkaVecValue)
        binding.radioRuangTidur.check(ruangTidurValue)
        binding.radioRuangTidurVec.check(ruangTidurVecValue)
        binding.radioABK.check(abkValue)
        binding.radioABKVec.check(abkVecValue)
        binding.radioPerwira.check(perwiraValue)
        binding.radioPerwiraVec.check(perwiraVecValue)
        binding.radioPenumpang.check(penumpangValue)
        binding.radioPenumpangVec.check(penumpangVecValue)
        binding.radioGeladak.check(geladakValue)
        binding.radioGeladakVec.check(geladakVecValue)
        binding.radioWater.check(waterValue)
        binding.radioWaterVec.check(waterVecValue)
        binding.radioLimbaCair.check(limbaCairValue)
        binding.radioLimbaCairVec.check(limbaCairVecValue)
        binding.radioGenangan.check(genanganValue)
        binding.radioAirTergenangVec.check(genanganVecValue)
        binding.radioEngine.check(engineValue)
        binding.radioMesinVec.check(engineVecValue)
        binding.radioMedik.check(medikValue)
        binding.radioMedikVec.check(medikVecValue)
        binding.radioOtherArea.check(otherAreaValue)
        binding.radioOtherAreaVec.check(otherAreaVecValue)
        binding.radioRekomendasi.check(rekomendasiValue)
        binding.radioResiko.check(resikoValue)
        binding.radioHealth.check(healthValue)
        binding.radioVektor.check(vektorValue)

        // Doc
        hasilDoc = copSanitasi.pemeriksanDoc
        binding.btnSelectHasil.text = getString(R.string.update_dokumen_title)
        binding.prevHasil.visibility = View.VISIBLE
        if (hasilDoc != null){
            Picasso.get().load(copSanitasi.pemeriksanDoc).fit().into(binding.prevHasil)
        }



        // Has masalah kesehatan
        if (getCheckedIdByString(copSanitasi.masalahKesehatan) == 1){
            hasMasalah = true
            binding.healthFileLayout.visibility = View.VISIBLE
            binding.etMasalahNote.visibility = View.VISIBLE

            masalahDoc = copSanitasi.masalahKesehatanFile
            binding.btnSelectMasalah.text = getString(R.string.update_dokumen_title)
            binding.prevMasalah.visibility = View.VISIBLE
            Picasso.get().load(masalahDoc).fit().into(binding.prevMasalah)

            //binding.etMasalahNote.editText?.setText(copSanitasi.masalahKesehatanCatatan)
            binding.dropdownNote.setText(copSanitasi.masalahKesehatanCatatan, false)
            binding.etCatatanKesehatan.visibility = View.GONE
        }else{
            hasMasalah = false
            binding.healthFileLayout.visibility = View.GONE
            binding.etMasalahNote.visibility = View.GONE
            binding.etCatatanKesehatan.visibility = View.VISIBLE
            binding.etCatatanKesehatan.editText?.setText(copSanitasi.catatanKesehatan)
        }
    }


    private fun getCheckedIdByString(selected: String): Int {
        // Buat set untuk nilai yang memberikan ID 1
        val group1 = setOf(
            "memenuhi syarat",
            "ada vektor dan tanda - tandanya",
            "disinseksi",
            "resiko tinggi",
            "ada"
        )

        // Buat set untuk nilai yang memberikan ID 2
        val group2 = setOf(
            "tidak memenuhi syarat",
            "Tidak ada vektor",
            "fumigasi",
            "resiko rendah",
            "tidak ada"
        )

        // Uji apakah 'selected' ada dalam set
        return when (selected.lowercase()) {
            in group1 -> 1
            in group2 -> 2
            else -> 3
        }
    }

    private fun onSaveButton() {
        val isAllChecked = checkIsAllChecked()


        if (isAllChecked && hasilDoc != null) {

            // SSCEC Masalah
            val masalahCatatanVal = binding.etMasalahNote.editText?.text.toString()
            val catatanKesehatanVal = binding.etCatatanKesehatan.editText?.text.toString()

            InputValidation.isAllFieldComplete(
                binding.etMasalahNote
            )

            val requireCatatanKesehatan = !hasMasalah && catatanKesehatanVal.isEmpty()
            val requireMasalahDoc = hasMasalah && masalahDoc.isNullOrEmpty()
            val requireMasalahCatatan = hasMasalah && masalahCatatanVal.isEmpty()

            val errorMessage = when {
                binding.radioResiko.checkedRadioButtonId == -1 || binding.radioHealth.checkedRadioButtonId == -1 || requireMasalahDoc || requireCatatanKesehatan || requireMasalahCatatan -> getString(R.string.data_not_completed)
                binding.radioRekomendasi.checkedRadioButtonId == -1 -> getString(R.string.data_not_completed)
                else -> null
            }

            if (errorMessage != null) {
                Toast.makeText(this@SanitasiInputActivity, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                onAllChecked()
            }
        } else {
            Toast.makeText(this@SanitasiInputActivity, getString(R.string.data_not_completed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onAllChecked() {
        val dapurValue = getSelectedRadioGroupValue(binding.radioDapur)
        val dapurVecValue = getSelectedRadioGroupValue(binding.radioDapurVec)
        val pantryValue = getSelectedRadioGroupValue(binding.radioPantry)
        val pantryVecValue = getSelectedRadioGroupValue(binding.radioPantryVec)
        val gudangValue = getSelectedRadioGroupValue(binding.radioGudang)
        val gudangVecValue = getSelectedRadioGroupValue(binding.radioGudangVec)
        val palkaValue = getSelectedRadioGroupValue(binding.radioPalka)
        val palkaVecValue = getSelectedRadioGroupValue(binding.radioPalkaVec)
        val ruangTidurValue = getSelectedRadioGroupValue(binding.radioRuangTidur)
        val ruangTidurVecValue = getSelectedRadioGroupValue(binding.radioRuangTidurVec)
        val abkValue = getSelectedRadioGroupValue(binding.radioABK)
        val abkVecValue = getSelectedRadioGroupValue(binding.radioABKVec)
        val perwiraValue = getSelectedRadioGroupValue(binding.radioPerwira)
        val perwiraVecValue = getSelectedRadioGroupValue(binding.radioPerwiraVec)
        val penumpangValue = getSelectedRadioGroupValue(binding.radioPenumpang)
        val penumpangVecValue = getSelectedRadioGroupValue(binding.radioPenumpangVec)
        val geladakValue = getSelectedRadioGroupValue(binding.radioGeladak)
        val geladakVecValue = getSelectedRadioGroupValue(binding.radioGeladakVec)
        val waterValue = getSelectedRadioGroupValue(binding.radioWater)
        val waterVecValue = getSelectedRadioGroupValue(binding.radioWaterVec)
        val limbaCairValue = getSelectedRadioGroupValue(binding.radioLimbaCair)
        val limbaCairVecValue = getSelectedRadioGroupValue(binding.radioLimbaCairVec)
        val genanganValue = getSelectedRadioGroupValue(binding.radioGenangan)
        val genanganVecValue = getSelectedRadioGroupValue(binding.radioAirTergenangVec)
        val engineValue = getSelectedRadioGroupValue(binding.radioEngine)
        val engineVecVal = getSelectedRadioGroupValue(binding.radioMesinVec)
        val medikValue = getSelectedRadioGroupValue(binding.radioMedik)
        val medikVecVal = getSelectedRadioGroupValue(binding.radioMedikVec)
        val otherAreaValue = getSelectedRadioGroupValue(binding.radioOtherArea)
        val otherAreaVecVal = getSelectedRadioGroupValue(binding.radioOtherAreaVec)
        val rekomendasiValue = getSelectedRadioGroupValue(binding.radioRekomendasi)
        val resikoValue = getSelectedRadioGroupValue(binding.radioResiko)
        val healthValue = getSelectedRadioGroupValue(binding.radioHealth)
        val vektorValue = getSelectedRadioGroupValue(binding.radioVektor)
        val masalahCatatanVal = binding.etMasalahNote.editText?.text.toString()
        val catatanKesehatan = binding.etCatatanKesehatan.editText?.text.toString()

        val sanitasiData = SanitasiModel(
            sanDapur = dapurValue,
            sanRuangRakit = pantryValue,
            sanGudang = gudangValue,
            sanPalka = palkaValue,
            sanRuangTidur = ruangTidurValue,
            sanABKReq = abkValue,
            sanPerwira = perwiraValue,
            sanPenumpang = penumpangValue,
            sanGeladak = geladakValue,
            sanAirMinum = waterValue,
            sanLimbaCair = limbaCairValue,
            sanAirTergenang = genanganValue,
            sanRuangMesin = engineValue,
            sanFasilitasMedik = medikValue,
            sanAreaLainnya = otherAreaValue,
            vecDapur = dapurVecValue,
            vecRuangRakit = pantryVecValue,
            vecGudang = gudangVecValue,
            vecPalka = palkaVecValue,
            vecRuangTidur = ruangTidurVecValue,
            vecABKReq = abkVecValue,
            vecPerwira = perwiraVecValue,
            vecPenumpang = penumpangVecValue,
            vecGeladak = geladakVecValue,
            vecAirMinum = waterVecValue,
            vecLimbaCair = limbaCairVecValue,
            rekomendasi = rekomendasiValue,
            resikoSanitasi = resikoValue,
            masalahKesehatan = healthValue,
            masalahKesehatanFile = masalahDoc ?: "",
            masalahKesehatanCatatan = masalahCatatanVal,
            pemeriksanDoc = hasilDoc!!,
            hasilFile = "TESTING",
            vecAirTergenang = genanganVecValue,
            vecRuangMesin = engineVecVal,
            vecFasilitasMedik = medikVecVal,
            vecAreaLainnya = otherAreaVecVal,
            tandatandaVektor = vektorValue,
            catatanKesehatan = catatanKesehatan
        )

        val intent = Intent(this@SanitasiInputActivity, CopInputActivity::class.java)
        intent.putExtra("SANITASI", sanitasiData)
        if (isUpdate){
            intent.putExtra("HAS_UPDATE", true)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun checkIsAllChecked(): Boolean {
        val radioGroups = listOf(
            binding.radioDapur,
            binding.radioDapurVec,
            binding.radioPantry,
            binding.radioPantryVec,
            binding.radioGudang,
            binding.radioGudangVec,
            binding.radioPalka,
            binding.radioPalkaVec,
            binding.radioRuangTidur,
            binding.radioRuangTidurVec,
            binding.radioABK,
            binding.radioABKVec,
            binding.radioPerwira,
            binding.radioPerwiraVec,
            binding.radioPenumpang,
            binding.radioPenumpangVec,
            binding.radioGeladak,
            binding.radioGeladakVec,
            binding.radioWater,
            binding.radioWaterVec,
            binding.radioLimbaCair,
            binding.radioLimbaCairVec,
            binding.radioGenangan,
            binding.radioAirTergenangVec,
            binding.radioEngine,
            binding.radioMesinVec,
            binding.radioMedik,
            binding.radioMedikVec,
            binding.radioOtherArea,
            binding.radioOtherAreaVec,
            binding.radioVektor
        )

        return radioGroups.all {
            it.checkedRadioButtonId != -1
        }
    }

    fun getSelectedRadioGroupValue(radioGroup: RadioGroup): String {
        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        val checkedRadioButton = findViewById<RadioButton>(checkedRadioButtonId)


        // sanitasi en
        if (checkedRadioButton.text == "Meets Requirements"){
            return "Memenuhi Syarat"
        }

        if (checkedRadioButton.text == "Does Not Meet Requirements"){
            return "Tidak Memenuhi Syarat"
        }

        if (checkedRadioButton.text == "No Room / Not Checked"){
            return "Tidak Ada Ruang / Tidak Diperiksa"
        }

        // vector en
        if (checkedRadioButton.text == "Vectors and signs are present"){
            return "Ada vektor dan tanda - tandanya"
        }

        if (checkedRadioButton.text == "No Vectors"){
            return "Tidak ada vektor"
        }

        // extras en
        if (checkedRadioButton.text == "Disinsection"){
            return "Disinseksi"
        }

        if (checkedRadioButton.text == "Fumigation"){
            return "Fumigasi"
        }

        // risk
        if (checkedRadioButton.text == "High risk"){
            return "Resiko tinggi"
        }

        if (checkedRadioButton.text == "Low risk"){
            return "Resiko rendah"
        }

        // avail
        if (checkedRadioButton.text == "Available"){
            return "ada"
        }

        if (checkedRadioButton.text == "Not available"){
            return "tidak ada"
        }


        return checkedRadioButton.text.toString()
    }

    private fun pickDocument() {
        // dialog
        val imageSelectorDialog = ImageSelectorModal()
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }

    override fun onImageSelected(imageUri: Uri) {
        val uriString = imageUri.toString()
        when(selectedDocType){
            "MASALAH" -> {
                masalahDoc = uriString
                binding.btnSelectMasalah.text = getString(R.string.update_dokumen_title)
                binding.prevMasalah.visibility = View.VISIBLE
                Picasso.get().load(uriString).fit().into(binding.prevMasalah)
            }
            "HASIL" -> {
                hasilDoc = uriString
                binding.btnSelectHasil.text = getString(R.string.update_dokumen_title)
                binding.prevHasil.visibility = View.VISIBLE
                Picasso.get().load(hasilDoc).fit().into(binding.prevHasil)
            }
        }
    }

    override fun attachBaseContext(base: Context?) {
        LocaleHelper().setLocale(base!!, LocaleHelper().getLanguage(base))
        super.attachBaseContext(LocaleHelper().onAttach(base))
    }
}