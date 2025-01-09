package com.ardclient.esikap.input.cop

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.ActivityCopInputDataUmumBinding
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.utils.DateTimeUtils
import com.ardclient.esikap.utils.InputValidation
import com.ardclient.esikap.utils.LocaleHelper
import com.google.android.material.datepicker.MaterialDatePicker

class CopInputDataUmumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCopInputDataUmumBinding
    private lateinit var copBasicData: COPModel

    private var isUpdate = false
    private var isUploaded = false

    // Radio
    private val radioMap = mutableMapOf<String, String?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputDataUmumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // check existing data
        val existingData = intent.getParcelableExtra<COPModel>("EXISTING_DATA")
        if (existingData != null){
            copBasicData = existingData
            isUpdate = true
            initExistingData()
        }else{
            copBasicData = COPModel()
        }

        // check is upload
        isUploaded = intent.getBooleanExtra("IS_UPLOAD", false)
        if (isUploaded){
            InputValidation.disabledAllRadio(
                binding.radioJenisPelayaran
            )
            InputValidation.disabledAllInput(
                binding.etTujuan,
                binding.etTiba,
                binding.etLokasiSandar,
                binding.etLokasiPemeriksaan,
                binding.etJmlABKAsing,
                binding.etJmlABKAsingMeninggal,
                binding.etJmlSehatABKAsing,
                binding.etJmlSakitABKAsing,
                binding.etJmlABKWNI,
                binding.etJmlABKWNIMeninggal,
                binding.etJmlSehatABKWNI,
                binding.etJmlSakitABKWNI,
                binding.etJmlPenumpangAsing,
                binding.etJmlPenumpangAsingMeninggal,
                binding.etJmlSehatPenumpangAsing,
                binding.etJmlSakitPenumpangAsing,
                binding.etJmlPenumpangWNI,
                binding.etJmlPenumpangWNIMeninggal,
                binding.etJmlSehatPenumpangWNI,
                binding.etJmlSakitPenumpangWNI,
            )

            binding.saveButton.visibility = View.GONE
        }

        // title
        binding.serviceTitle.text = "${binding.serviceTitle.text} : ${getString(R.string.kedatangan_subtitle)}"

        // button
        binding.saveButton.setOnClickListener {
            onSaveData()
        }

        // Date picker
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .build()

        binding.etTiba.editText?.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATEPICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = DateTimeUtils.formatDate(it)
            binding.etTiba.editText?.setText(selectedDate)
        }

        // radio
//        binding.radioJenisLayanan.setOnCheckedChangeListener{ _, checkedId ->
//            if (checkedId == R.id.radio_layanan_kedatangan){
//                radioMap["LAYANAN"] = "Kedatangan"
//            }else{
//                radioMap["LAYANAN"] = "Keberangkatan"
//            }
//        }
        // set default to kedatangan only
        radioMap["LAYANAN"] = "Kedatangan"

        binding.radioJenisPelayaran.setOnCheckedChangeListener{ _, checkedId ->
            if (checkedId == R.id.radio_pelayaran_domestik){
                radioMap["PELAYARAN"] = "Domestik"
            }else{
                radioMap["PELAYARAN"] = "Internasional"
            }
        }
    }

    private fun initExistingData() {
        //binding.etTujuan.editText?.setText(copBasicData.tujuan)
        binding.dropdownTujuan.setText(copBasicData.tujuan, false)
        binding.etTiba.editText?.setText(copBasicData.tglTiba)
        binding.etLokasiSandar.editText?.setText(copBasicData.lokasiSandar)
        binding.etLokasiPemeriksaan.editText?.setText(copBasicData.lokasiPemeriksaan)
        binding.etJmlABKAsing.editText?.setText(copBasicData.jumlahABKAsing.toString())
        binding.etJmlABKAsingMeninggal.editText?.setText(copBasicData.jumlahABKAsingMD.toString())
        binding.etJmlSehatABKAsing.editText?.setText(copBasicData.asingSehat.toString())
        binding.etJmlSakitABKAsing.editText?.setText(copBasicData.asingSakit.toString())
        binding.etJmlABKWNI.editText?.setText(copBasicData.jumlahABKWNI.toString())
        binding.etJmlABKWNIMeninggal.editText?.setText(copBasicData.jumlahABKWNIMD.toString())
        binding.etJmlSehatABKWNI.editText?.setText(copBasicData.wniSehat.toString())
        binding.etJmlSakitABKWNI.editText?.setText(copBasicData.wniSakit.toString())
        binding.etJmlPenumpangAsing.editText?.setText(copBasicData.jumlahPenumpangAsing.toString())
        binding.etJmlPenumpangAsingMeninggal.editText?.setText(copBasicData.jumlahPenumpangAsingMD.toString())
        binding.etJmlSehatPenumpangAsing.editText?.setText(copBasicData.penumpangAsingSehat.toString())
        binding.etJmlSakitPenumpangAsing.editText?.setText(copBasicData.penumpangAsingSakit.toString())
        binding.etJmlPenumpangWNI.editText?.setText(copBasicData.jumlahPenumpangWNI.toString())
        binding.etJmlPenumpangWNIMeninggal.editText?.setText(copBasicData.jumlahPenumpangWNIMD.toString())
        binding.etJmlSehatPenumpangWNI.editText?.setText(copBasicData.penumpangSehat.toString())
        binding.etJmlSakitPenumpangWNI.editText?.setText(copBasicData.penumpangSakit.toString())

        // radio
        radioMap["LAYANAN"] = copBasicData.jenisLayanan
//        if (copBasicData.jenisLayanan == "Kedatangan"){
//            binding.radioJenisLayanan.check(R.id.radio_layanan_kedatangan)
//        }else{
//            binding.radioJenisLayanan.check(R.id.radio_layanan_keberangkatan)
//        }
        radioMap["LAYANAN"] = "Kedatangan"

        radioMap["PELAYARAN"] = copBasicData.jenisPelayaran
        if (copBasicData.jenisPelayaran == "Domestik"){
            binding.radioJenisPelayaran.check(R.id.radio_pelayaran_domestik)
        }else{
            binding.radioJenisPelayaran.check(R.id.radio_pelayaran_inter)
        }
    }

    private fun onSaveData() {
        val etTujuan = binding.etTujuan.editText?.text.toString()
        val etTiba = binding.etTiba.editText?.text.toString()
        val etLokasiSandar = binding.etLokasiSandar.editText?.text.toString()
        val etLokasiPemeriksaan = binding.etLokasiPemeriksaan.editText?.text.toString()
        val etJmlABKAsing = binding.etJmlABKAsing.editText?.text.toString()
        val etJmlABKAsingMD = binding.etJmlABKAsingMeninggal.editText?.text.toString()
        val etJmlSehatABKAsing = binding.etJmlSehatABKAsing.editText?.text.toString()
        val etJmlSakitABKAsing = binding.etJmlSakitABKAsing.editText?.text.toString()
        val etJmlABKWNI = binding.etJmlABKWNI.editText?.text.toString()
        val etJmlABKWNIMD = binding.etJmlABKWNIMeninggal.editText?.text.toString()
        val etJmlSehatABKWNI = binding.etJmlSehatABKWNI.editText?.text.toString()
        val etJmlSakitABKWNI = binding.etJmlSakitABKWNI.editText?.text.toString()
        val etJmlPenumpangAsing = binding.etJmlPenumpangAsing.editText?.text.toString()
        val etJmlPenumpangAsingMD = binding.etJmlPenumpangAsingMeninggal.editText?.text.toString()
        val etJmlSehatPenumpangAsing = binding.etJmlSehatPenumpangAsing.editText?.text.toString()
        val etJmlSakitPenumpangAsing = binding.etJmlSakitPenumpangAsing.editText?.text.toString()
        val etJmlPenumpangWNI = binding.etJmlPenumpangWNI.editText?.text.toString()
        val etJmlPenumpangWNIMD = binding.etJmlPenumpangWNIMeninggal.editText?.text.toString()
        val etJmlSehatPenumpangWNI = binding.etJmlSehatPenumpangWNI.editText?.text.toString()
        val etJmlSakitPenumpangWNI = binding.etJmlSakitPenumpangWNI.editText?.text.toString()

        // cek radio
        val isAllRadio = InputValidation.isAllRadioFilled(
            binding.radioJenisPelayaran
        )

        // check is all filled
        val isAllFilled = InputValidation.isAllFieldComplete(
            binding.etTujuan,
            binding.etTiba,
            binding.etLokasiSandar,
            binding.etLokasiPemeriksaan,
            binding.etJmlABKAsing,
            binding.etJmlABKAsingMeninggal,
            binding.etJmlSehatABKAsing,
            binding.etJmlSakitABKAsing,
            binding.etJmlABKWNI,
            binding.etJmlABKWNIMeninggal,
            binding.etJmlSehatABKWNI,
            binding.etJmlSakitABKWNI,
            binding.etJmlPenumpangAsing,
            binding.etJmlPenumpangAsingMeninggal,
            binding.etJmlSehatPenumpangAsing,
            binding.etJmlSakitPenumpangAsing,
            binding.etJmlPenumpangWNI,
            binding.etJmlPenumpangWNIMeninggal,
            binding.etJmlSehatPenumpangWNI,
            binding.etJmlSakitPenumpangWNI
        )

        if (isAllFilled && isAllRadio){
            val copBasicData = COPModel(
                tujuan = etTujuan,
                tglTiba = etTiba,
                lokasiSandar = etLokasiSandar,
                jumlahABKAsing = etJmlABKAsing.toInt(),
                asingSehat = etJmlSehatABKAsing.toInt(),
                asingSakit = etJmlSakitABKAsing.toInt(),
                jumlahABKWNI = etJmlABKWNI.toInt(),
                wniSehat = etJmlSehatABKWNI.toInt(),
                wniSakit = etJmlSakitABKWNI.toInt(),
                jumlahPenumpangAsing = etJmlPenumpangAsing.toInt(),
                penumpangAsingSehat = etJmlSehatPenumpangAsing.toInt(),
                penumpangAsingSakit = etJmlSakitPenumpangAsing.toInt(),
                jumlahPenumpangWNI = etJmlPenumpangWNI.toInt(),
                penumpangSehat = etJmlSehatPenumpangWNI.toInt(),
                penumpangSakit = etJmlSakitPenumpangWNI.toInt(),
                jenisLayanan = radioMap["LAYANAN"]!!,
                jenisPelayaran = radioMap["PELAYARAN"]!!,
                lokasiPemeriksaan = etLokasiPemeriksaan,
                jumlahABKAsingMD = etJmlABKAsingMD.toInt(),
                jumlahABKWNIMD = etJmlABKWNIMD.toInt(),
                jumlahPenumpangAsingMD = etJmlPenumpangAsingMD.toInt(),
                jumlahPenumpangWNIMD = etJmlPenumpangWNIMD.toInt()
            )

            val intent = Intent(this, CopInputActivity::class.java)
            intent.putExtra("COP_BASIC", copBasicData)
            if (isUpdate){
                intent.putExtra("HAS_UPDATE", true)
            }
            setResult(RESULT_OK, intent)
            finish()
        }else{
            Toast.makeText(this, getString(R.string.data_not_completed), Toast.LENGTH_SHORT).show()
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

    override fun attachBaseContext(base: Context?) {
        LocaleHelper().setLocale(base!!, LocaleHelper().getLanguage(base))
        super.attachBaseContext(LocaleHelper().onAttach(base))
    }
}