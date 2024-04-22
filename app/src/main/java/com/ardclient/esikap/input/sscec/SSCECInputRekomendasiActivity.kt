package com.ardclient.esikap.input.sscec

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.input.SignatureActivity
import com.ardclient.esikap.databinding.ActivitySscecInputRekomendasiBinding
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DateTimeUtils
import com.ardclient.esikap.utils.InputValidation
import com.ardclient.esikap.utils.SessionUtils
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class SSCECInputRekomendasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySscecInputRekomendasiBinding
    private var launcher: ActivityResultLauncher<Intent>? = null

    private var signKaptenData: String? = null
    private var signPetugasData: String? = null
    private var nmPetugas2: String = ""
    private var base64SignPetugas2: String = ""
    private var nipPetugas2: String = ""
    private var nmPetugas3: String = ""
    private var base64SignPetugas3: String = ""
    private var nipPetugas3: String = ""

    private lateinit var basicData: SSCECModel
    private var isUpdate = false
    private var isUploaded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySscecInputRekomendasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // handle sign result
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val type = data?.getStringExtra("TYPE")

                val nama = data?.getStringExtra("NAMA")
                val nip = data?.getStringExtra("NIP")
                val decodedSign = data?.getByteArrayExtra("SIGNATURE")

                val encodedSign = BitmapFactory.decodeByteArray(decodedSign, 0, decodedSign!!.size)
                val sign = Base64Utils.convertBitmapToBase64(encodedSign)

                onSignedResult(type, nama, sign, encodedSign, nip)
            }
        }

        val existingData = intent.getParcelableExtra<SSCECModel>("EXISTING_DATA")
        if (existingData !=null){
            basicData = existingData
            initExistingData()
            isUpdate = true
        }else{
            basicData = SSCECModel()
        }

        // check is upload
        isUploaded = intent.getBooleanExtra("IS_UPLOAD", false)
        if (isUploaded){
            updateUIonUploaded()
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
            if (!isUploaded){
                val namaKapten = binding.tvKapten.text.toString()

                val intent = Intent(this, SignatureActivity::class.java)
                intent.putExtra("NAMA", namaKapten)
                intent.putExtra("TYPE", "KAPTEN")
                launcher!!.launch(intent)
            }
        }

        binding.ivSignOfficer.setOnClickListener{
            if (!isUploaded){
                val namaOfficer = binding.tvPetugas.text.toString()

                val intent = Intent(this, SignatureActivity::class.java)
                intent.putExtra("NAMA", namaOfficer)
                intent.putExtra("TYPE", "PETUGAS")
                launcher!!.launch(intent)
            }
        }

        // Date picker
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
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
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText(getString(R.string.select_time))
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

        // Add Sign Petugas 2
        binding.addSignPT2Button.setOnClickListener {
            val intent = Intent(this, SignatureActivity::class.java)
            intent.putExtra("NAMA", "")
            intent.putExtra("NIP", "")
            intent.putExtra("TYPE", "PETUGAS_2")
            launcher!!.launch(intent)
        }

        // Add Sign Petugas 3
        binding.addSignPT3Button.setOnClickListener {
            val intent = Intent(this, SignatureActivity::class.java)
            intent.putExtra("NAMA", "")
            intent.putExtra("NIP", "")
            intent.putExtra("TYPE", "PETUGAS_3")
            launcher!!.launch(intent)
        }

        // Layout Petugas 2
        binding.signPT2Layout.setOnClickListener {
            if (!isUploaded){
                val nama = binding.tvPetugas2.text.toString()

                val intent = Intent(this, SignatureActivity::class.java)
                intent.putExtra("NAMA", nama)
                intent.putExtra("NIP", nipPetugas2)
                intent.putExtra("TYPE", "PETUGAS_2")
                launcher!!.launch(intent)
            }

        }

        // Layout Petugas 3
        binding.signPT3Layout.setOnClickListener {
            if (!isUploaded){
                val nama = binding.tvPetugas3.text.toString()

                val intent = Intent(this, SignatureActivity::class.java)
                intent.putExtra("NAMA", nama)
                intent.putExtra("NIP", nipPetugas3)
                intent.putExtra("TYPE", "PETUGAS_3")
                launcher!!.launch(intent)
            }
        }

        // Delete sign 2
        binding.btnDeleteSign2.setOnClickListener {
            nmPetugas2 = ""
            base64SignPetugas2 = ""
            nipPetugas2 = ""

            binding.addSignPT2Button.visibility = View.VISIBLE
            binding.signPT2Layout.visibility = View.GONE
        }

        // Delete sign 3
        binding.btnDeleteSign3.setOnClickListener {
            nmPetugas3 = ""
            base64SignPetugas3 = ""
            nipPetugas3 = ""

            binding.addSignPT3Button.visibility = View.VISIBLE
            binding.signPT3Layout.visibility = View.GONE
        }
    }

    private fun updateUIonUploaded() {
        with(binding){
            etSSCEC.editText?.isEnabled = false
            etTanggal.editText?.isEnabled = false
            etJam.editText?.isEnabled = false

            tvSignHelpKapten.visibility = View.GONE
            tvSignHelpPetugas.visibility = View.GONE

            saveButton.visibility = View.GONE
        }
    }

    private fun initExistingData() {
        binding.etSSCEC.editText?.setText(basicData.recSSCEC)
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

        if (basicData.nipPetugas2.isNotEmpty()){
            val sign = Base64Utils.convertBase64ToBitmap(basicData.signPetugas2)
            binding.ivSignPT2.setImageBitmap(sign)
            binding.tvPetugas2.text = basicData.namaPetugas2
            base64SignPetugas2 = basicData.signPetugas2
            nipPetugas2 = basicData.nipPetugas2
            nmPetugas2 = basicData.namaPetugas2
            binding.signPT2Layout.visibility = View.VISIBLE
            binding.addSignPT2Button.visibility = View.GONE
        }

        if (basicData.nipPetugas3.isNotEmpty()){
            val sign = Base64Utils.convertBase64ToBitmap(basicData.signPetugas3)
            binding.ivSignPT3.setImageBitmap(sign)
            binding.tvPetugas3.text = basicData.namaPetugas3
            base64SignPetugas3 = basicData.signPetugas3
            nipPetugas3 = basicData.nipPetugas3
            nmPetugas3 = basicData.namaPetugas3
            binding.signPT3Layout.visibility = View.VISIBLE
            binding.addSignPT3Button.visibility = View.GONE
        }
    }

    private fun onSaveButtonPressed() {
        val sscecVal = binding.etSSCEC.editText?.text.toString()
        val tanggalVal = binding.etTanggal.editText?.text.toString()
        val jamVal = binding.etJam.editText?.text.toString()
        val namaKapten = binding.tvKapten.text.toString()
        val namaPetugas = binding.tvPetugas.text.toString()

        // check is all filled
        val isAllFilled = InputValidation.isAllFieldComplete(
            binding.etSSCEC,
            binding.etTanggal,
            binding.etJam
        )

        if (isAllFilled &&  signKaptenData != null && signPetugasData != null){
            val basicData = SSCECModel(
                recSSCEC = sscecVal,
                recJam = jamVal,
                recTanggal = tanggalVal,
                signKapten = signKaptenData!!,
                signPetugas = signPetugasData!!,
                signNamaKapten = namaKapten,
                signNamaPetugas = namaPetugas,
                namaPetugas2 = nmPetugas2,
                namaPetugas3 = nmPetugas3,
                signPetugas2 = base64SignPetugas2,
                signPetugas3 = base64SignPetugas3,
                nipPetugas2 = nipPetugas2,
                nipPetugas3 = nipPetugas3
            )

            val intent = Intent(this, SSCECInputActivity::class.java)
            intent.putExtra("SIGNATURE", basicData)
            if (isUpdate){
                intent.putExtra("HAS_UPDATE", true)
            }
            setResult(RESULT_OK, intent)
            finish()
        }else{
            Toast.makeText(this, getString(R.string.data_not_completed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSignedResult(
        type: String?,
        nama: String?,
        sign: String?,
        signBitmap: Bitmap?,
        nip: String?
    ) {
        when(type){
            "KAPTEN" -> {
                binding.signKaptenButton.visibility = View.GONE
                binding.signedKapten.visibility = View.VISIBLE
                binding.tvKapten.text = nama
                binding.ivSignKapten.setImageBitmap(signBitmap)
                signKaptenData = sign
            }

            "PETUGAS" -> {
                binding.signOfficerButton.visibility = View.GONE
                binding.signedOfficer.visibility = View.VISIBLE
                binding.tvPetugas.text = nama
                binding.ivSignOfficer.setImageBitmap(signBitmap)
                signPetugasData = sign
            }

            "PETUGAS_2" -> {
                binding.addSignPT2Button.visibility = View.GONE
                binding.signPT2Layout.visibility = View.VISIBLE
                binding.tvPetugas2.text = nama
                binding.ivSignPT2.setImageBitmap(signBitmap)
                binding.tvPetugas2NIP.text = nip
                nmPetugas2 = nama!!
                base64SignPetugas2 = sign!!
                nipPetugas2 = nip!!
            }

            "PETUGAS_3" -> {
                binding.addSignPT3Button.visibility = View.GONE
                binding.signPT3Layout.visibility = View.VISIBLE
                binding.tvPetugas3.text = nama
                binding.ivSignPT3.setImageBitmap(signBitmap)
                binding.tvPetugas3NIP.text = nip
                nmPetugas3 = nama!!
                base64SignPetugas3 = sign!!
                nipPetugas3 = nip!!
            }
        }
//        if (type == "KAPTEN"){
//            binding.signKaptenButton.visibility = View.GONE
//            binding.signedKapten.visibility = View.VISIBLE
//            binding.tvKapten.text = nama
//            binding.ivSignKapten.setImageBitmap(signBitmap)
//            signKaptenData = sign
//        }else{
//            binding.signOfficerButton.visibility = View.GONE
//            binding.signedOfficer.visibility = View.VISIBLE
//            binding.tvPetugas.text = nama
//            binding.ivSignOfficer.setImageBitmap(signBitmap)
//            signPetugasData = sign
//        }
    }

    private fun onUnsignButtonPress(type: String) {
        val session = SessionUtils.getUserSession(this)
        val kapten = intent.getStringExtra("KAPTEN")

        val name = if (type == "KAPTEN") kapten else session.name

        val intent = Intent(this@SSCECInputRekomendasiActivity, SignatureActivity::class.java)
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