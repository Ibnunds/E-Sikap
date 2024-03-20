package com.ardclient.esikap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.ardclient.esikap.cop.CopInputActivity
import com.ardclient.esikap.databinding.ActivitySanitasiInputBinding
import com.ardclient.esikap.model.reusable.SanitasiModel

class SanitasiInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySanitasiInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySanitasiInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // button
        binding.saveButton.setOnClickListener {
            onSaveButton()
        }
    }

    private fun onSaveButton() {
        val isAllChecked = checkIsAllChecked()

        if (isAllChecked){
            onAllChecked()
        }else{
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
        val rekomendasiValue = getSelectedRadioGroupValue(binding.radioRekomendasi)

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
            rekomendasi = rekomendasiValue
        )

        val intent = Intent(this@SanitasiInputActivity, CopInputActivity::class.java)
        intent.putExtra("COP_SANITASI", sanitasiData)
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
            binding.radioRekomendasi
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
}