package com.ardclient.esikap.input

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.ardclient.esikap.R
import com.ardclient.esikap.input.cop.CopInputActivity
import com.ardclient.esikap.databinding.ActivitySanitasiInputBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.model.reusable.SanitasiModel
import com.ardclient.esikap.utils.InputValidation
import com.squareup.picasso.Picasso

class SanitasiInputActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var binding: ActivitySanitasiInputBinding
    private lateinit var copSanitasi: SanitasiModel

    private lateinit var senderActivity: String
    private var masalahDoc: String = ""
    private var hasMasalah: Boolean = false
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
            initExistingData()
        }

        // handle sender string
        val senderStr = intent.getStringExtra("SENDER")
        if (senderStr != null){
            senderActivity = senderStr
        }

        if (senderActivity == "SSCEC"){
            binding.radioRekomendasiLayout.visibility = View.GONE
        }else{
            binding.radioResikoLayout.visibility = View.GONE
            binding.radioHealthLayout.visibility = View.GONE
            binding.healthFileLayout.visibility = View.GONE
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
            }else{
                hasMasalah = false
                binding.healthFileLayout.visibility = View.GONE
                binding.etMasalahNote.visibility = View.GONE
            }
        }

        binding.btnSelectMasalah.setOnClickListener {
            pickDocument()
        }
    }

    private fun initExistingData() {
        val dapurVal = if (getCheckedIdByString(copSanitasi.sanDapur) == 1) R.id.radio_dapur_true else R.id.radio_dapur_false
        val dapurVecValue = if (getCheckedIdByString(copSanitasi.vecDapur) == 1) R.id.radio_dapurVec_true else R.id.radio_dapurVec_false
        val pantryValue = if (getCheckedIdByString(copSanitasi.sanRuangRakit) == 1) R.id.radio_pantry_true else R.id.radio_pantry_false
        val pantryVecValue = if (getCheckedIdByString(copSanitasi.vecRuangRakit) == 1) R.id.radio_pantryVec_true else R.id.radio_pantryVec_false
        val gudangValue = if (getCheckedIdByString(copSanitasi.sanGudang) == 1) R.id.radio_gudang_true else R.id.radio_gudang_false
        val gudangVecValue = if (getCheckedIdByString(copSanitasi.vecGudang) == 1) R.id.radio_gudangVec_true else R.id.radio_gudangVec_false
        val palkaValue = if (getCheckedIdByString(copSanitasi.sanPalka) == 1) R.id.radio_palka_true else R.id.radio_palka_false
        val palkaVecValue = if (getCheckedIdByString(copSanitasi.vecPalka) == 1) R.id.radio_palkaVec_true else R.id.radio_palkaVec_false
        val ruangTidurValue = if (getCheckedIdByString(copSanitasi.sanRuangTidur) == 1) R.id.radio_quarter_true else R.id.radio_quarter_false
        val ruangTidurVecValue = if (getCheckedIdByString(copSanitasi.vecRuangTidur) == 1) R.id.radio_quarterVec_true else R.id.radio_quarterVec_false
        val abkValue = if (getCheckedIdByString(copSanitasi.sanABKReq) == 1) R.id.radio_abk_true else R.id.radio_abk_false
        val abkVecValue = if (getCheckedIdByString(copSanitasi.vecABKReq) == 1) R.id.radio_abkVec_true else R.id.radio_abkVec_false
        val perwiraValue = if (getCheckedIdByString(copSanitasi.sanPerwira) == 1) R.id.radio_perwira_true else R.id.radio_perwira_false
        val perwiraVecValue = if (getCheckedIdByString(copSanitasi.vecPerwira) == 1) R.id.radio_perwiraVec_true else R.id.radio_perwiraVec_false
        val penumpangValue = if (getCheckedIdByString(copSanitasi.sanPenumpang) == 1) R.id.radio_penumpang_true else R.id.radio_penumpang_false
        val penumpangVecValue = if (getCheckedIdByString(copSanitasi.vecPenumpang) == 1) R.id.radio_penumpangVec_true else R.id.radio_penumpangVec_false
        val geladakValue = if (getCheckedIdByString(copSanitasi.sanGeladak) == 1) R.id.radio_deck_true else R.id.radio_deck_false
        val geladakVecValue = if (getCheckedIdByString(copSanitasi.vecGeladak) == 1) R.id.radio_deckVec_true else R.id.radio_deckVec_false
        val waterValue = if (getCheckedIdByString(copSanitasi.sanAirMinum) == 1) R.id.radio_water_true else R.id.radio_water_false
        val waterVecValue = if (getCheckedIdByString(copSanitasi.vecAirMinum) == 1) R.id.radio_waterVec_true else R.id.radio_waterVec_false
        val limbaCairValue = if (getCheckedIdByString(copSanitasi.sanLimbaCair) == 1) R.id.radio_limba_true else R.id.radio_limba_false
        val limbaCairVecValue = if (getCheckedIdByString(copSanitasi.vecLimbaCair) == 1) R.id.radio_limbaVec_true else R.id.radio_limbaVec_false
        val genanganValue = if (getCheckedIdByString(copSanitasi.sanAirTergenang) == 1) R.id.radio_genangan_true else R.id.radio_genangan_false
        val engineValue = if (getCheckedIdByString(copSanitasi.sanRuangMesin) == 1) R.id.radio_engine_true else R.id.radio_engine_false
        val medikValue = if (getCheckedIdByString(copSanitasi.sanFasilitasMedik) == 1) R.id.radio_medik_true else R.id.radio_medik_false
        val otherAreaValue = if (getCheckedIdByString(copSanitasi.sanAreaLainnya) == 1) R.id.radio_otherArea_true else R.id.radio_otherArea_false
        val rekomendasiValue = if (getCheckedIdByString(copSanitasi.rekomendasi) == 1) R.id.radio_disinseksi else if (getCheckedIdByString(copSanitasi.rekomendasi) == 2) R.id.radio_fumigasi else R.id.radio_no_problem
        val resikoValue = if (getCheckedIdByString(copSanitasi.resikoSanitasi) == 1) R.id.radio_resiko_tinggi else if (getCheckedIdByString(copSanitasi.resikoSanitasi) == 2) R.id.radio_resiko_rendah else R.id.radio_resiko_no
        val healthValue = if (getCheckedIdByString(copSanitasi.masalahKesehatan) == 1) R.id.radio_health_true else R.id.radio__health_false


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
        binding.radioEngine.check(engineValue)
        binding.radioMedik.check(medikValue)
        binding.radioOtherArea.check(otherAreaValue)
        binding.radioRekomendasi.check(rekomendasiValue)
        binding.radioResiko.check(resikoValue)
        binding.radioHealth.check(healthValue)

        if (getCheckedIdByString(copSanitasi.masalahKesehatan) == 1){
            hasMasalah = true
            binding.healthFileLayout.visibility = View.VISIBLE
            binding.etMasalahNote.visibility = View.VISIBLE

            masalahDoc = copSanitasi.masalahKesehatanFile
            binding.btnSelectMasalah.text = "Update Dokumen"
            binding.prevMasalah.visibility = View.VISIBLE
            Picasso.get().load(masalahDoc).fit().into(binding.prevMasalah)

            binding.etMasalahNote.editText?.setText(copSanitasi.masalahKesehatanCatatan)
        }else{
            hasMasalah = false
            binding.healthFileLayout.visibility = View.GONE
            binding.etMasalahNote.visibility = View.GONE
        }
    }


    private fun getCheckedIdByString(selected: String): Int {
        return if (selected.lowercase() == "memenuhi syarat" || selected.lowercase() == "ada vektor dan tanda - tandanya" || selected.lowercase() == "disinseksi" || selected.lowercase() == "resiko tinggi" || selected.lowercase() == "ada"){
            1
        }else if (selected.lowercase() == "tidak memenuhi syarat" || selected.lowercase() == "tidak ada vektor" || selected.lowercase() == "fumigasi" || selected.lowercase() == "resiko rendah" || selected.lowercase() == "tidak ada"){
            2
        }else{
            3
        }
    }

    private fun onSaveButton() {
        val isAllChecked = checkIsAllChecked()


        if (isAllChecked) {

            // SSCEC Masalah
            val masalahCatatanVal = binding.etMasalahNote.editText?.text.toString()

            val isMasalahFilled = InputValidation.isAllFieldComplete(
                binding.etMasalahNote
            )

            val requireMasalahDoc = hasMasalah && masalahDoc.isEmpty()
            val requireMasalahCatatan = hasMasalah && masalahCatatanVal.isEmpty()

            val errorMessage = when {
                senderActivity == "SSCEC" && (binding.radioResiko.checkedRadioButtonId == -1 || binding.radioHealth.checkedRadioButtonId == -1 || requireMasalahDoc || requireMasalahCatatan) -> "Data belum lengkap!"
                senderActivity != "SSCEC" && binding.radioRekomendasi.checkedRadioButtonId == -1 -> "Data belum lengkap!"
                else -> null
            }

            if (errorMessage != null) {
                Toast.makeText(this@SanitasiInputActivity, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                onAllChecked()
            }
        } else {
            Toast.makeText(this@SanitasiInputActivity, "Data belum lengkap!", Toast.LENGTH_SHORT).show()
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
        val engineValue = getSelectedRadioGroupValue(binding.radioEngine)
        val medikValue = getSelectedRadioGroupValue(binding.radioMedik)
        val otherAreaValue = getSelectedRadioGroupValue(binding.radioOtherArea)
        val rekomendasiValue = if (senderActivity != "SSCEC") getSelectedRadioGroupValue(binding.radioRekomendasi) else "-"
        val resikoValue = if (senderActivity == "SSCEC") getSelectedRadioGroupValue(binding.radioResiko) else "-"
        val healthValue = if (senderActivity == "SSCEC") getSelectedRadioGroupValue(binding.radioHealth) else "-"
        val masalahCatatanVal = binding.etMasalahNote.editText?.text.toString()

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
            masalahKesehatanFile = masalahDoc,
            masalahKesehatanCatatan = masalahCatatanVal
        )

        val intent = Intent(this@SanitasiInputActivity, CopInputActivity::class.java)
        intent.putExtra("SANITASI", sanitasiData)
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
            binding.radioEngine,
            binding.radioMedik,
            binding.radioOtherArea,
        )

        return radioGroups.all {
            it.checkedRadioButtonId != -1
        }
    }

    fun getSelectedRadioGroupValue(radioGroup: RadioGroup): String {
        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        val checkedRadioButton = findViewById<RadioButton>(checkedRadioButtonId)
        return checkedRadioButton.text.toString()
    }

    private fun pickDocument() {
        // dialog
        val imageSelectorDialog = ImageSelectorModal()
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }

    override fun onImageSelected(imageUri: Uri) {
        val uriString = imageUri.toString()
        masalahDoc = uriString
        binding.btnSelectMasalah.text = "Update Dokumen"
        binding.prevMasalah.visibility = View.VISIBLE
        Picasso.get().load(uriString).fit().into(binding.prevMasalah)
    }
}