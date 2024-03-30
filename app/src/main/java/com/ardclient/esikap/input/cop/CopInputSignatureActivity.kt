package com.ardclient.esikap.input.cop

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.ActivityCopInputSignatureBinding
import com.ardclient.esikap.input.SignatureActivity
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DateTimeUtils
import com.ardclient.esikap.utils.InputValidation
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.squareup.picasso.Picasso

class CopInputSignatureActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var binding: ActivityCopInputSignatureBinding

    private var launcher: ActivityResultLauncher<Intent>? = null

    private var signKaptenData: String? = null
    private var signPetugasData: String? = null

    // dok
    private var selectedDoc: String? = null

    // radio
    private val radioMap = mutableMapOf<String, String?>()

    private lateinit var copSignature: COPModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputSignatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

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

        // Existing data
        val existingData = intent.getParcelableExtra<COPModel>("EXISTING_DATA")
        if (existingData != null){
            copSignature = existingData
            initExisting()
        }else{
            copSignature = COPModel()
        }

        // Date picker
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih tanggal")
                .build()

        // time picker
        val timePicker =
            MaterialTimePicker.Builder()
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Pilih jam")
                .build()

        binding.etTanggal.editText?.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATEPICKER")
        }

        binding.etJam.editText?.setOnClickListener {
            timePicker.show(supportFragmentManager, "DATEPICKER")
        }

        // ON DATE CB
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = DateTimeUtils.formatDate(it)
            binding.etTanggal.editText?.setText(selectedDate)
        }


        // ON TIME CB
        timePicker.addOnPositiveButtonClickListener {
            val pickerHour = timePicker.hour
            val pickerMinute = timePicker.minute
            val formatted = DateTimeUtils.formatTime(pickerHour, pickerMinute)

            binding.etJam.editText?.setText(formatted)
        }

        // Button
        binding.signKaptenButton.setOnClickListener {
            onUnsignButtonPress("KAPTEN")
        }

        binding.signOfficerButton.setOnClickListener {
            onUnsignButtonPress("PETUGAS")
        }

        // === SIGNATURE
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

        // SAVE BUTTON
        binding.saveButton.setOnClickListener {
            onSaveButton()
        }

        // PICK DOC
        binding.btnSelectDoc.setOnClickListener {
            selectedDoc = "FP"
            pickDocument()
        }

        // RADIO
        binding.radioObatP3K.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radio_obatp3k_true){
                radioMap["OBATP3K"] = "Lengkap"
            }else{
                radioMap["OBATP3K"] = "Tidak lengkap"
            }
        }

        binding.radioKarantinaPinalti.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radio_karantinapinalti_true){
                radioMap["KARANTINA"] = "Ada"
            }else{
                radioMap["KARANTINA"] = "Tidak ada"
            }
        }

        binding.radioDokKes.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radio_dokkes_lengkapberlaku -> radioMap["DOKKES"] = "Lengkap berlaku"
                R.id.radio_dokkes_lengkaptidakberlaku -> radioMap["DOKKES"] = "Lengkap tidak berlaku"
                else -> radioMap["DOKKES"] = "Tidak lengkap tidak berlaku"
            }
        }

        binding.radioTipeDok.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_tipedok_fp -> {
                    radioMap["TIPEDOK"] = "FP"
                    binding.layoutDoc.visibility = View.VISIBLE
                }
                R.id.radio_tipedok_rp -> {
                    radioMap["TIPEDOK"] = "RP"
                    binding.layoutDoc.visibility = View.VISIBLE
                }
                else -> {
                    binding.layoutDoc.visibility = View.GONE
                }
            }
        }
    }

    private fun initExisting() {
        with(binding){
            // Radio P3K
            radioMap["OBATP3K"] = copSignature.obatP3K
            if (copSignature.obatP3K == "Lengkap"){
                radioObatP3K.check(R.id.radio_obatp3k_true)
            }else{
                radioObatP3K.check(R.id.radio_obatp3k_false)
            }

            // Radio Karantina
            radioMap["KARANTINA"] = copSignature.pelanggaranKarantina
            if (copSignature.pelanggaranKarantina == "Ada"){
                radioKarantinaPinalti.check(R.id.radio_karantinapinalti_true)
            }else{
                radioKarantinaPinalti.check(R.id.radio_karantinapinalti_false)
            }

            // Radio dokkes
            radioMap["DOKKES"] = copSignature.dokumenKesehatanKapal
            when(copSignature.dokumenKesehatanKapal){
                "Lengkap berlaku" -> radioDokKes.check(R.id.radio_dokkes_lengkapberlaku)
                "Lengkap tidak berlaku" -> radioDokKes.check(R.id.radio_dokkes_lengkaptidakberlaku)
                else -> radioDokKes.check(R.id.radio_dokkes_tidaklengkaptidakberlaku)
            }

            // Radio dok
            radioMap["TIPEDOK"] = copSignature.docType
            if (copSignature.docType == "FP"){
                radioTipeDok.check(R.id.radio_tipedok_fp)
            }else{
                radioTipeDok.check(R.id.radio_tipedok_rp)
            }

            etTanggal.editText?.setText(copSignature.docTanggal)
            etJam.editText?.setText(copSignature.docJam)

            selectedDoc = copSignature.docFile
            binding.btnSelectDoc.text = "Update Dokumen"
            binding.prevDoc.visibility = View.VISIBLE
            Picasso.get().load(selectedDoc).fit().into(binding.prevDoc)


            // signature
            binding.signKaptenButton.visibility = View.GONE
            binding.signOfficerButton.visibility = View.GONE
            binding.signedKapten.visibility = View.VISIBLE
            binding.signedOfficer.visibility = View.VISIBLE
            binding.tvKapten.text = copSignature.signNamaKapten
            binding.tvPetugas.text = copSignature.signNamaPetugas

            val kaptenSign = Base64Utils.convertBase64ToBitmap(copSignature.signKapten)
            val officerSign = Base64Utils.convertBase64ToBitmap(copSignature.signPetugas)

            signKaptenData = copSignature.signKapten
            signPetugasData = copSignature.signPetugas

            binding.ivSignKapten.setImageBitmap(kaptenSign)
            binding.ivSignOfficer.setImageBitmap(officerSign)
        }
    }

    private fun pickDocument() {
        // dialog
        val imageSelectorDialog = ImageSelectorModal()
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }

    private fun onSaveButton() {
        with(binding){
            // FP
            val tanggal = etTanggal.editText?.text.toString()
            val jam = etJam.editText?.text.toString()

            //sign
            val namaKapten = binding.tvKapten.text.toString()
            val namaPetugas = binding.tvPetugas.text.toString()

            // radio
            val pemeriksaanP3K = radioMap["OBATP3K"]
            val pelanggaranKarantina = radioMap["KARANTINA"]
            val dokKesKapal = radioMap["DOKKES"]

            // Tanggal form
            val isFormComplete = InputValidation.isAllFieldComplete(etTanggal, etJam)

            // Radio from
            val isRadioComplete = InputValidation.isAllRadioFilled(
                radioObatP3K,
                radioKarantinaPinalti,
                radioDokKes,
                radioTipeDok
            )

            val dokType = radioMap["TIPEDOK"]

            if (isFormComplete && isRadioComplete && selectedDoc != null && dokType != null && signPetugasData != null && signKaptenData != null){
                val signatureData = COPModel(
                    obatP3K = pemeriksaanP3K!!,
                    pelanggaranKarantina = pelanggaranKarantina!!,
                    dokumenKesehatanKapal = dokKesKapal!!,
                    signNamaPetugas = namaPetugas,
                    signNamaKapten = namaKapten,
                    signPetugas = signPetugasData!!,
                    signKapten = signKaptenData!!,
                    docFile = selectedDoc!!,
                    docJam = jam,
                    docTanggal = tanggal,
                    docType = dokType
                )

                val intent = Intent(this@CopInputSignatureActivity, CopInputActivity::class.java)
                intent.putExtra("SIGNATURE", signatureData)
                setResult(RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this@CopInputSignatureActivity, "Data belum lengkap!", Toast.LENGTH_SHORT).show()
            }

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
        val intent = Intent(this@CopInputSignatureActivity, SignatureActivity::class.java)
        intent.putExtra("NAMA", "")
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

    override fun onImageSelected(imageUri: Uri) {
        val uriString = imageUri.toString()
        selectedDoc = uriString
        binding.btnSelectDoc.text = "Update Dokumen"
        binding.prevDoc.visibility = View.VISIBLE
        Picasso.get().load(uriString).fit().into(binding.prevDoc)
    }
}