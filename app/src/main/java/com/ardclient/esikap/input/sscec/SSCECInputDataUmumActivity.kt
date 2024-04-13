package com.ardclient.esikap.input.sscec

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.ActivitySscecInputDataUmumBinding
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.utils.DateTimeUtils
import com.ardclient.esikap.utils.InputValidation
import com.google.android.material.datepicker.MaterialDatePicker

class SSCECInputDataUmumActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySscecInputDataUmumBinding
    private lateinit var basicData: SSCECModel

    private var isUpdate = false
    private var isUploaded = false

    // Radio
    private val radioMap = mutableMapOf<String, String?>()

    // date picker
    private lateinit var datePickerType: String
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
            isUpdate = true
        }else{
            basicData = SSCECModel()
        }

        // check is upload
        isUploaded = intent.getBooleanExtra("IS_UPLOAD", false)
        if (isUploaded){
            InputValidation.disabledAllRadio(
                binding.radioJenisLayanan,
                binding.radioJenisPelayaran
            )
            InputValidation.disabledAllInput(
                binding.etTujuan,
                binding.etTiba,
                binding.etLokasiSandar,
                binding.etJmlABKAsing,
                binding.etJmlSehatABKAsing,
                binding.etJmlSakitABKAsing,
                binding.etJmlABKWNI,
                binding.etJmlSehatABKWNI,
                binding.etJmlSakitABKWNI,
            )

            binding.saveButton.visibility = View.GONE
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
            datePickerType = "TIBA"
            datePicker.show(supportFragmentManager, "DATEPICKER")
        }

        binding.etSSCEC.editText?.setOnClickListener {
            datePickerType = "SSCEC"
            datePicker.show(supportFragmentManager, "DATEPICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = DateTimeUtils.formatDate(it)
            if (datePickerType == "TIBA"){
                binding.etTiba.editText?.setText(selectedDate)
            }else{
                binding.etSSCEC.editText?.setText(selectedDate)
            }
        }

        // radio
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
        binding.etTujuan.editText?.setText(basicData.pelabuhanTujuan)
        binding.etTiba.editText?.setText(basicData.tglTiba)
        binding.etLokasiSandar.editText?.setText(basicData.lokasiSandar)
        binding.etJmlABKAsing.editText?.setText(basicData.jumlahABKAsing.toString())
        binding.etJmlSehatABKAsing.editText?.setText(basicData.asingSehat.toString())
        binding.etJmlSakitABKAsing.editText?.setText(basicData.asingSakit.toString())
        binding.etJmlABKWNI.editText?.setText(basicData.jumlahABKWNI.toString())
        binding.etJmlSehatABKWNI.editText?.setText(basicData.wniSehat.toString())
        binding.etJmlSakitABKWNI.editText?.setText(basicData.wniSakit.toString())
        binding.etSSCEC.editText?.setText(basicData.sscecLama)
        binding.etTempatTerbit.editText?.setText(basicData.tempatTerbit)

        // radio
        radioMap["LAYANAN"] = basicData.jenisLayanan
        if (basicData.jenisLayanan == "Kedatangan"){
            binding.radioJenisLayanan.check(R.id.radio_layanan_kedatangan)
        }else{
            binding.radioJenisLayanan.check(R.id.radio_layanan_keberangkatan)
        }

        radioMap["PELAYARAN"] = basicData.jenisPelayaran
        if (basicData.jenisPelayaran == "Domestik"){
            binding.radioJenisPelayaran.check(R.id.radio_pelayaran_domestik)
        }else{
            binding.radioJenisPelayaran.check(R.id.radio_pelayaran_inter)
        }
    }

    private fun onSaveData() {
        val etTujuan = binding.etTujuan.editText?.text.toString()
        val etTiba = binding.etTiba.editText?.text.toString()
        val etSSCEC = binding.etSSCEC.editText?.text.toString()
        val etTerbit = binding.etSSCEC.editText?.text.toString()
        val etLokasiSandar = binding.etLokasiSandar.editText?.text.toString()
        val etJmlABKAsing = binding.etJmlABKAsing.editText?.text.toString()
        val etJmlSehatABKAsing = binding.etJmlSehatABKAsing.editText?.text.toString()
        val etJmlSakitABKAsing = binding.etJmlSakitABKAsing.editText?.text.toString()
        val etJmlABKWNI = binding.etJmlABKWNI.editText?.text.toString()
        val etJmlSehatABKWNI = binding.etJmlSehatABKWNI.editText?.text.toString()
        val etJmlSakitABKWNI = binding.etJmlSakitABKWNI.editText?.text.toString()

        // cek radio
        val isAllRadio = InputValidation.isAllRadioFilled(
            binding.radioJenisLayanan,
            binding.radioJenisPelayaran
        )

        // check is all filled
        val isAllFilled = InputValidation.isAllFieldComplete(
            binding.etTujuan,
            binding.etTiba,
            binding.etSSCEC,
            binding.etTempatTerbit,
            binding.etLokasiSandar,
            binding.etJmlABKAsing,
            binding.etJmlSehatABKAsing,
            binding.etJmlSakitABKAsing,
            binding.etJmlABKWNI,
            binding.etJmlSehatABKWNI,
            binding.etJmlSakitABKWNI
        )

        if (isAllFilled && isAllRadio){
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
                jenisLayanan = radioMap["LAYANAN"]!!,
                jenisPelayaran = radioMap["PELAYARAN"]!!,
                sscecLama = etSSCEC,
                tempatTerbit = etTerbit
            )

            val intent = Intent(this, SSCECInputActivity::class.java)
            intent.putExtra("BASIC", basicData)
            if (isUpdate){
                intent.putExtra("HAS_UPDATE", true)
            }
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