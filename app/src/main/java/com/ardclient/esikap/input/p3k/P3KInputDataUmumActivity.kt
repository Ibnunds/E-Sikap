package com.ardclient.esikap.input.p3k

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.ActivityP3kInputDataUmumBinding
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.input.sscec.SSCECInputActivity
import com.ardclient.esikap.utils.DateTimeUtils
import com.ardclient.esikap.utils.InputValidation
import com.google.android.material.datepicker.MaterialDatePicker

class P3KInputDataUmumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityP3kInputDataUmumBinding
    private lateinit var P3KDataUmum: P3KModel

    // Radio
    private val radioMap = mutableMapOf<String, String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityP3kInputDataUmumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // exiting data
        val existingData = intent.getParcelableExtra<P3KModel>("EXISTING_DATA")
        if (existingData != null){
            P3KDataUmum = existingData
            initExistingData()
        }else{
            P3KDataUmum = P3KModel()
        }

        binding.saveButton.setOnClickListener {
            onSaveButton()
        }

        // Date picker
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih tanggal")
                .build()

        binding.etTanggalDiperiksa.editText?.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATEPICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = DateTimeUtils.formatDate(it)
            binding.etTanggalDiperiksa.editText?.setText(selectedDate)
        }

        binding.radioJenisLayanan.setOnCheckedChangeListener{ _, checkedId ->
            if (checkedId == R.id.radio_layanan_kedatangan){
                radioMap["LAYANAN"] = "Kedatangan"
            }else{
                radioMap["LAYANAN"] = "Keberangkatan"
            }
        }

        binding.radioJenisPelayaran.setOnCheckedChangeListener{ _, checkedId ->
            if (checkedId == R.id.radio_pelayaran_domestik){
                radioMap["PELAYARAN"] = "Domestik"
            }else{
                radioMap["PELAYARAN"] = "Internasional"
            }
        }
    }

    private fun initExistingData() {
        with(binding) {
            etLokasiPemeriksaan.editText?.setText(P3KDataUmum.lokasiPemeriksaan)
            etTanggalDiperiksa.editText?.setText(P3KDataUmum.tglDiperiksa)
            etJmlABK.editText?.setText(P3KDataUmum.jmlABK.toString())
            etJmlSehat.editText?.setText(P3KDataUmum.abkSehat.toString())
            etJmlSakit.editText?.setText(P3KDataUmum.abkSakit.toString())


            // radio
            radioMap["LAYANAN"] = P3KDataUmum.jenisLayanan
            if (P3KDataUmum.jenisLayanan == "Kedatangan"){
                radioJenisLayanan.check(R.id.radio_layanan_kedatangan)
            }else{
                radioJenisLayanan.check(R.id.radio_layanan_keberangkatan)
            }

            radioMap["PELAYARAN"] = P3KDataUmum.jenisPelayanan
            if (P3KDataUmum.jenisPelayanan == "Domestik"){
                radioJenisPelayaran.check(R.id.radio_pelayaran_domestik)
            }else{
                radioJenisPelayaran.check(R.id.radio_pelayaran_inter)
            }
        }
    }

    private fun onSaveButton() {
        with(binding){
            val lokasiPemeriksaan = etLokasiPemeriksaan.editText?.text.toString()
            val tanggalDiperiksa = etTanggalDiperiksa.editText?.text.toString()

            val jumlahABK = etJmlABK.editText?.text.toString()
            val abkSehat = etJmlSehat.editText?.text.toString()
            val abkSakit = etJmlSakit.editText?.text.toString()

            val isFormCompleted = InputValidation.isAllFieldComplete(
                etLokasiPemeriksaan,
                etTanggalDiperiksa,
                etJmlABK,
                etJmlSehat,
                etJmlSakit
            )

            val isRadioComplete = InputValidation.isAllRadioFilled(
                radioJenisPelayaran,
                radioJenisLayanan
            )

            if (isFormCompleted && isRadioComplete){
                val dataUmum = P3KModel(
                    jenisLayanan = radioMap["LAYANAN"]!!,
                    jenisPelayanan = radioMap["PELAYARAN"]!!,
                    lokasiPemeriksaan = lokasiPemeriksaan,
                    tglDiperiksa = tanggalDiperiksa,
                    jmlABK = jumlahABK.toInt(),
                    abkSehat = abkSehat.toInt(),
                    abkSakit = abkSakit.toInt()
                )

                val intent = Intent(this@P3KInputDataUmumActivity, P3KInputActivity::class.java)
                intent.putExtra("BASIC", dataUmum)
                setResult(RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this@P3KInputDataUmumActivity, "Mohon lengkapi semua input", Toast.LENGTH_SHORT).show()
            }
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