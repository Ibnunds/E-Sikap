package com.ardclient.esikap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.ardclient.esikap.databinding.ActivitySanitasiInputBinding

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
        val pantryValue = getSelectedRadioGroupValue(binding.radioPantry)
        val gudangValue = getSelectedRadioGroupValue(binding.radioGudang)
        val palkaValue = getSelectedRadioGroupValue(binding.radioPalka)
        val ruangTidurValue = getSelectedRadioGroupValue(binding.radioRuangTidur)
        val abkValue = getSelectedRadioGroupValue(binding.radioABK)
        val perwiraValue = getSelectedRadioGroupValue(binding.radioPerwira)
        val penumpangValue = getSelectedRadioGroupValue(binding.radioPenumpang)
        val geladakValue = getSelectedRadioGroupValue(binding.radioGeladak)
        val waterValue = getSelectedRadioGroupValue(binding.radioWater)
        val limbaCairValue = getSelectedRadioGroupValue(binding.radioLimbaCair)
        val genanganValue = getSelectedRadioGroupValue(binding.radioGenangan)
        val engineValue = getSelectedRadioGroupValue(binding.radioEngine)
        val medikValue = getSelectedRadioGroupValue(binding.radioMedik)
        val otherAreaValue = getSelectedRadioGroupValue(binding.radioOtherArea)
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
            binding.radioOtherArea
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