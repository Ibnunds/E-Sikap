package com.ardclient.esikap.cop

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.ardclient.esikap.databinding.ActivityCopInputDataUmumBinding
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.utils.InputValidation

class CopInputDataUmumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCopInputDataUmumBinding
    private lateinit var copBasicData: COPModel
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
            initExistingData()
        }else{
            copBasicData = COPModel()
        }

        // button
        binding.saveButton.setOnClickListener {
            onSaveData()
        }
    }

    private fun initExistingData() {
        binding.etTujuan.editText?.setText(copBasicData.tujuan)
        binding.etTiba.editText?.setText(copBasicData.tglTiba)
        binding.etLokasiSandar.editText?.setText(copBasicData.lokasiSandar)
        binding.etJmlABKAsing.editText?.setText(copBasicData.jumlahABKAsing.toString())
        binding.etJmlSehatABKAsing.editText?.setText(copBasicData.asingSehat.toString())
        binding.etJmlSakitABKAsing.editText?.setText(copBasicData.asingSakit.toString())
        binding.etJmlABKWNI.editText?.setText(copBasicData.jumlahABKWNI.toString())
        binding.etJmlSehatABKWNI.editText?.setText(copBasicData.wniSehat.toString())
        binding.etJmlSakitABKWNI.editText?.setText(copBasicData.wniSakit.toString())
        binding.etJmlPenumpangAsing.editText?.setText(copBasicData.jumlahPenumpangAsing.toString())
        binding.etJmlSehatPenumpangAsing.editText?.setText(copBasicData.penumpangAsingSehat.toString())
        binding.etJmlSakitPenumpangAsing.editText?.setText(copBasicData.penumpangAsingSakit.toString())
        binding.etJmlPenumpangWNI.editText?.setText(copBasicData.jumlahPenumpangWNI.toString())
        binding.etJmlSehatPenumpangWNI.editText?.setText(copBasicData.penumpangSehat.toString())
        binding.etJmlSakitPenumpangWNI.editText?.setText(copBasicData.penumpangSakit.toString())
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
        val etJmlPenumpangAsing = binding.etJmlPenumpangAsing.editText?.text.toString()
        val etJmlSehatPenumpangAsing = binding.etJmlSehatPenumpangAsing.editText?.text.toString()
        val etJmlSakitPenumpangAsing = binding.etJmlSakitPenumpangAsing.editText?.text.toString()
        val etJmlPenumpangWNI = binding.etJmlPenumpangWNI.editText?.text.toString()
        val etJmlSehatPenumpangWNI = binding.etJmlSehatPenumpangWNI.editText?.text.toString()
        val etJmlSakitPenumpangWNI = binding.etJmlSakitPenumpangWNI.editText?.text.toString()

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
            binding.etJmlSakitABKWNI,
            binding.etJmlPenumpangAsing,
            binding.etJmlSehatPenumpangAsing,
            binding.etJmlSakitPenumpangAsing,
            binding.etJmlPenumpangWNI,
            binding.etJmlSehatPenumpangWNI,
            binding.etJmlSakitPenumpangWNI
        )

        if (isAllFilled){
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
                penumpangSakit = etJmlSakitPenumpangWNI.toInt()
            )

            val intent = Intent(this, CopInputActivity::class.java)
            intent.putExtra("COP_BASIC", copBasicData)
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