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
import com.ardclient.esikap.databinding.ActivityCopInputSignatureBinding
import com.ardclient.esikap.input.SignatureActivity
import com.ardclient.esikap.input.p3k.P3KInputActivity
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
    private var selectedTanggal = ""
    private var selectedJam = ""

    private var launcher: ActivityResultLauncher<Intent>? = null

    private var signKaptenData: String? = null
    private var signPetugasData: String? = null

    // dok
    private var selectedDoc = ""
    private var dokFP: String? = null
    private var dokRP: String? = null
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

        binding.etTanggalFP.editText?.setOnClickListener {
            selectedTanggal = "FP"
            datePicker.show(supportFragmentManager, "DATEPICKER")
        }

        binding.etJamFP.editText?.setOnClickListener {
            selectedJam = "FP"
            timePicker.show(supportFragmentManager, "DATEPICKER")
        }

        binding.etTanggalRP.editText?.setOnClickListener {
            selectedTanggal = "RP"
            datePicker.show(supportFragmentManager, "DATEPICKER")
        }

        binding.etJamRP.editText?.setOnClickListener {
            selectedJam = "RP"
            timePicker.show(supportFragmentManager, "DATEPICKER")
        }

        // ON DATE CB
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = DateTimeUtils.formatDate(it)
            if (selectedTanggal == "FP"){
                binding.etTanggalFP.editText?.setText(selectedDate)
            }else{
                binding.etTanggalRP.editText?.setText(selectedDate)
            }
        }


        // ON TIME CB
        timePicker.addOnPositiveButtonClickListener {
            val pickerHour = timePicker.hour
            val pickerMinute = timePicker.minute
            val formatted = DateTimeUtils.formatTime(pickerHour, pickerMinute)

            if (selectedJam == "FP"){
                binding.etJamFP.editText?.setText(formatted)
            }else{
                binding.etJamRP.editText?.setText(formatted)
            }
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
        binding.btnSelectDocFP.setOnClickListener {
            selectedDoc = "FP"
            pickDocument()
        }

        binding.btnSelectDocRP.setOnClickListener {
            selectedDoc = "RP"
            pickDocument()
        }
    }

    private fun pickDocument() {
        // dialog
        val imageSelectorDialog = ImageSelectorModal()
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }

    private fun onSaveButton() {
        with(binding){
            val hasilP3K = etHasilP3K.editText?.text.toString()
            val pelanggaranKarantina = etPelanggaranKarantina.editText?.text.toString()
            val dokKesKapal = etDokKesehatanKapal.editText?.text.toString()
            // FP
            val fpTanggal = etTanggalFP.editText?.text.toString()
            val fpJam = etJamFP.editText?.text.toString()
            val rpTanggal = etTanggalRP.editText?.text.toString()
            val rpJam = etJamRP.editText?.text.toString()

            //sign
            val namaKapten = binding.tvKapten.text.toString()
            val namaPetugas = binding.tvPetugas.text.toString()

            val isFormComplete = InputValidation.isAllFieldComplete(
                etHasilP3K,
                etPelanggaranKarantina,
                etDokKesehatanKapal,
                etTanggalFP,
                etJamFP,
                etTanggalRP,
                etJamRP
            )

            if (isFormComplete && namaKapten.isNotEmpty() && namaPetugas.isNotEmpty() && dokFP != null && dokRP != null && signPetugasData != null && signKaptenData != null){
                val signatureData = COPModel(
                    obatP3K = hasilP3K,
                    pelanggaranKarantina = pelanggaranKarantina,
                    dokumenKesehatanKapal = dokKesKapal,
                    docFreePratique = dokFP!!,
                    docRestresedPratique = dokRP!!,
                    docFreePratiqueTanggal = fpTanggal,
                    docFreePratiqueJam = fpJam,
                    docRestresedPratiqueTanggal = rpTanggal,
                    docRestresedPratiqueJam = rpJam,
                    signNamaPetugas = namaPetugas,
                    signNamaKapten = namaKapten,
                    signPetugas = signPetugasData!!,
                    signKapten = signKaptenData!!
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
        if (selectedDoc == "FP"){
            dokFP = uriString
            binding.btnSelectDocFP.text = "Update Dokumen"
            binding.prevDocFP.visibility = View.VISIBLE
            Picasso.get().load(uriString).fit().into(binding.prevDocFP)
        }else{
            dokRP = uriString
            binding.btnSelectDocRP.text = "Update Dokumen"
            binding.prevDocRP.visibility = View.VISIBLE
            Picasso.get().load(uriString).fit().into(binding.prevDocRP)
        }
    }
}