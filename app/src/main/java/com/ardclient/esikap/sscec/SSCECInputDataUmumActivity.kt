package com.ardclient.esikap.sscec

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.ardclient.esikap.databinding.ActivitySscecInputDataUmumBinding
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.utils.DateTimeUtils
import com.ardclient.esikap.utils.InputValidation
import com.google.android.material.datepicker.MaterialDatePicker

class SSCECInputDataUmumActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySscecInputDataUmumBinding
    private lateinit var basicData: SSCECModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySscecInputDataUmumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // check existing data
        val existingData = intent.getParcelableExtra<SSCECModel>("EXISTING_DATA")
        if (existingData != null){
            basicData = existingData
            initExistingData()
        }else{
            basicData = SSCECModel()
        }

        // button
        binding.saveButton.setOnClickListener {
            onSaveData()
        }

        // Date picker
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih tanggal")
                .build()

        binding.etTiba.editText?.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATEPICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = DateTimeUtils.formatDate(it)
            binding.etTiba.editText?.setText(selectedDate)
        }
    }

    private fun initExistingData() {
        binding.etTujuan.editText?.setText(basicData.pelabuhanTujuan)
        binding.etTiba.editText?.setText(basicData.tglTiba)
        binding.etLokasiSandar.editText?.setText(basicData.lokasiSandar)
        binding.etJmlABKAsing.editText?.setText(basicData.jumlahABKAsing.toString())
        binding.etJmlSehatABKAsing.editText?.setText(basicData.asingSehat.toString())
        binding.etJmlSakitABKAsing.editText?.setText(basicData.asingSakit.toString())
        binding.etJmlABKWNI.editText?.setText(basicData.jumlahABKWNI.toString())
        binding.etJmlSehatABKWNI.editText?.setText(basicData.wniSehat.toString())
        binding.etJmlSakitABKWNI.editText?.setText(basicData.wniSakit.toString())
    }

    private fun onSaveData() {
        val etTujuan = binding.etTujuan.editText?.text.toString()
        val etTiba = binding.etTiba.editText?.text.toString()
        val etLokasiSandar = binding.etLokasiSandar.editText?.text.toString()
        val etJmlABKAsing = binding.etJmlABKAsing.editText?.text.toString()
        val etJmlSehatABKAsing = binding.etJmlSehatABKAsing.editText?.text.toString()
        val etJmlSakitABKAsing = binding.etJmlSakitABKAsing.editText?.text.toString()
        val etJmlABKWNI = binding.etJmlABKWNI.editText?.text.toString()
        val etJmlSehatABKWNI = binding.etJmlSehatABKWNI.editText?.text.toString()
        val etJmlSakitABKWNI = binding.etJmlSakitABKWNI.editText?.text.toString()

        // check is all filled
        val isAllFilled = InputValidation.isAllFieldComplete(
            binding.etTujuan,
            binding.etTiba,
            binding.etLokasiSandar,
            binding.etJmlABKAsing,
            binding.etJmlSehatABKAsing,
            binding.etJmlSakitABKAsing,
            binding.etJmlABKWNI,
            binding.etJmlSehatABKWNI,
            binding.etJmlSakitABKWNI
        )

        if (isAllFilled){
            val basicData = SSCECModel(
                pelabuhanTujuan = etTujuan,
                tglTiba = etTiba,
                lokasiSandar = etLokasiSandar,
                jumlahABKAsing = etJmlABKAsing.toInt(),
                asingSehat = etJmlSehatABKAsing.toInt(),
                asingSakit = etJmlSakitABKAsing.toInt(),
                jumlahABKWNI = etJmlABKWNI.toInt(),
                wniSehat = etJmlSehatABKWNI.toInt(),
                wniSakit = etJmlSakitABKWNI.toInt(),
            )

            val intent = Intent(this, SSCECInputActivity::class.java)
            intent.putExtra("BASIC", basicData)
            setResult(RESULT_OK, intent)
            finish()
        }else{
            Toast.makeText(this, "Mohon lengkapi semua input", Toast.LENGTH_SHORT).show()
        }
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