package com.ardclient.esikap.input.p3k

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ardclient.esikap.input.SignatureActivity
import com.ardclient.esikap.databinding.ActivityP3kInputRekomendasiBinding
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DateTimeUtils
import com.ardclient.esikap.utils.InputValidation
import com.ardclient.esikap.utils.SessionUtils
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat

class P3KInputRekomendasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityP3kInputRekomendasiBinding
    private var launcher: ActivityResultLauncher<Intent>? = null

    private var signKaptenData: String? = null
    private var signPetugasData: String? = null

    private lateinit var basicData: P3KModel

    private var isUpdate = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityP3kInputRekomendasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // handle sign result
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val type = data?.getStringExtra("TYPE")

                val nama = data?.getStringExtra("NAMA")
                val decodedSign = data?.getByteArrayExtra("SIGNATURE")

                val encodedSign = BitmapFactory.decodeByteArray(decodedSign, 0, decodedSign!!.size)
                val sign = Base64Utils.convertBitmapToBase64(encodedSign)

                onSignedResult(type, nama, sign, encodedSign)
            }
        }

        val existingData = intent.getParcelableExtra<P3KModel>("EXISTING_DATA")
        if (existingData !=null){
            basicData = existingData
            initExistingData()
            Log.d("EXISTING_DATAS", existingData.toString())
            isUpdate = true
        }else{
            basicData = P3KModel()
            Log.d("EXISTING_DATAS", "NO DATA")
        }

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // Button
        binding.signKaptenButton.setOnClickListener {
            onUnsignButtonPress("KAPTEN")
        }

        binding.signOfficerButton.setOnClickListener {
            onUnsignButtonPress("PETUGAS")
        }

        binding.saveButton.setOnClickListener {
            onSaveButtonPressed()
        }

        binding.ivSignKapten.setOnClickListener{
            val namaKapten = binding.tvKapten.text.toString()

            val intent = Intent(this, SignatureActivity::class.java)
            intent.putExtra("NAMA", namaKapten)
            intent.putExtra("TYPE", "KAPTEN")
            launcher!!.launch(intent)
        }

        binding.ivSignOfficer.setOnClickListener{
            val namaOfficer = binding.tvPetugas.text.toString()

            val intent = Intent(this, SignatureActivity::class.java)
            intent.putExtra("NAMA", namaOfficer)
            intent.putExtra("TYPE", "PETUGAS")
            launcher!!.launch(intent)
        }

        // Date picker
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih tanggal")
                .build()

        binding.etTanggal.editText?.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATEPICKER")
        }

        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = DateTimeUtils.formatDate(it)
            binding.etTanggal.editText?.setText(selectedDate)
        }


        // time picker
        val timePicker =
            MaterialTimePicker.Builder()
                .setInputMode(INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Pilih jam")
                .build()

        binding.etJam.editText?.setOnClickListener {
            timePicker.show(supportFragmentManager, "TIMEPICKER")
        }

        timePicker.addOnPositiveButtonClickListener {
            val pickerHour = timePicker.hour
            val pickerMinute = timePicker.minute
            val formatted = DateTimeUtils.formatTime(pickerHour, pickerMinute)

            binding.etJam.editText?.setText(formatted)
        }

    }

    private fun initExistingData() {
        binding.etDoc.editText?.setText(basicData.recP3K)
        binding.etTanggal.editText?.setText(basicData.recTanggal)
        binding.etJam.editText?.setText(basicData.recJam)

        // signature
        binding.signKaptenButton.visibility = View.GONE
        binding.signOfficerButton.visibility = View.GONE
        binding.signedKapten.visibility = View.VISIBLE
        binding.signedOfficer.visibility = View.VISIBLE
        binding.tvKapten.text = basicData.signNamaKapten
        binding.tvPetugas.text = basicData.signNamaPetugas

        val kaptenSign = Base64Utils.convertBase64ToBitmap(basicData.signKapten)
        val officerSign = Base64Utils.convertBase64ToBitmap(basicData.signPetugas)

        signKaptenData = basicData.signKapten
        signPetugasData = basicData.signPetugas

        binding.ivSignKapten.setImageBitmap(kaptenSign)
        binding.ivSignOfficer.setImageBitmap(officerSign)
    }

    private fun onSaveButtonPressed() {
        val docVal = binding.etDoc.editText?.text.toString()
        val tanggalVal = binding.etTanggal.editText?.text.toString()
        val jamVal = binding.etJam.editText?.text.toString()
        val namaKapten = binding.tvKapten.text.toString()
        val namaPetugas = binding.tvPetugas.text.toString()

        // check is all filled
        val isAllFilled = InputValidation.isAllFieldComplete(
            binding.etDoc,
            binding.etTanggal,
            binding.etJam
        )

        if (isAllFilled && namaKapten.isNotEmpty() && namaPetugas.isNotEmpty() && signKaptenData != null && signPetugasData != null){
            val basicData = P3KModel(
                recP3K = docVal,
                recJam = jamVal,
                recTanggal = tanggalVal,
                signKapten = signKaptenData!!,
                signPetugas = signPetugasData!!,
                signNamaKapten = namaKapten,
                signNamaPetugas = namaPetugas
            )

            val intent = Intent(this, P3KInputActivity::class.java)
            intent.putExtra("SIGNATURE", basicData)
            if (isUpdate){
                intent.putExtra("HAS_UPDATE", true)
            }
            setResult(RESULT_OK, intent)
            finish()
        }else{
            Toast.makeText(this, "Mohon lengkapi semua input", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSignedResult(type: String?, nama: String?, sign: String?, signBitmap: Bitmap?) {
        if (type == "KAPTEN"){
            binding.signKaptenButton.visibility = View.GONE
            binding.signedKapten.visibility = View.VISIBLE
            binding.tvKapten.text = nama
            binding.ivSignKapten.setImageBitmap(signBitmap)
            signKaptenData = sign
        }else{
            binding.signOfficerButton.visibility = View.GONE
            binding.signedOfficer.visibility = View.VISIBLE
            binding.tvPetugas.text = nama
            binding.ivSignOfficer.setImageBitmap(signBitmap)
            signPetugasData = sign
        }
    }

    private fun onUnsignButtonPress(type: String) {
        val session = SessionUtils.getUserSession(this)
        val kapten = intent.getStringExtra("KAPTEN")

        val name = if (type == "KAPTEN") kapten else session.name

        val intent = Intent(this@P3KInputRekomendasiActivity, SignatureActivity::class.java)
        intent.putExtra("NAMA", name)
        intent.putExtra("TYPE", type)
        launcher!!.launch(intent)
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