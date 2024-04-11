package com.ardclient.esikap.input.phqc

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.input.SignatureActivity
import com.ardclient.esikap.database.phqc.PHQCDao
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.databinding.ActivityPhqcInputBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.PHQCModel
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DateTimeUtils
import com.ardclient.esikap.utils.DialogUtils
import com.ardclient.esikap.utils.InputValidation
import com.ardclient.esikap.utils.SessionUtils
import com.google.android.material.datepicker.MaterialDatePicker
import com.squareup.picasso.Picasso


class PHQCInputActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var kapal: KapalModel
    private lateinit var phqc: PHQCModel
    private var isUpdate: Boolean = false
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var nmPetugas: String? = null
    private var base64Sign: String? = null

    private lateinit var binding: ActivityPhqcInputBinding

    // database
    private lateinit var database: PHQCRoomDatabase
    private lateinit var dao: PHQCDao

    // doc
    private var pemeriksaanDoc: String? = null

    // Radio
    private val radioMap = mutableMapOf<String, String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhqcInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // on back
        onBackPressedDispatcher.addCallback(this) {
            DialogUtils.showNotSavedDialog(this@PHQCInputActivity, object: DialogUtils.DialogListener {
                override fun onConfirmed() {
                    finish()
                }
            })
        }

        // init database
        database = PHQCRoomDatabase.getDatabase(this)
        dao = database.getPHQCDao()

        // handle sign result
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val namaPetugas = data?.getStringExtra("NAMA")
                val decodedSign = data?.getByteArrayExtra("SIGNATURE")

                val encodedSign = BitmapFactory.decodeByteArray(decodedSign, 0, decodedSign!!.size)
                base64Sign = Base64Utils.convertBitmapToBase64(encodedSign)

                if (!namaPetugas.isNullOrEmpty()) {
                    binding.signLayout.visibility = View.VISIBLE
                    binding.addSignButton.visibility = View.GONE
                    nmPetugas = namaPetugas
                    binding.ivSign.setImageBitmap(encodedSign)
                    binding.tvPetugas.text = namaPetugas
                }
            }
        }

        // check is from update
        val updateData = intent.getParcelableExtra<PHQCModel>("PHQC")
        if (updateData!=null){
            phqc = updateData
            isUpdate = true
            initExistingData()
        }else{
            phqc = PHQCModel()
        }

        // existing kapal data
        val kapalData = intent.getParcelableExtra<KapalModel>("KAPAL")

        if (kapalData != null){
            kapal = kapalData
        }else{
            kapal = KapalModel()
        }

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        val session = SessionUtils.getUserSession(this)

        binding.addSignButton.setOnClickListener {
            val intent = Intent(this@PHQCInputActivity, SignatureActivity::class.java)
            intent.putExtra("NAMA", session.name)
            intent.putExtra("TYPE", "PETUGAS")
            launcher!!.launch(intent)
        }

        binding.saveButton.setOnClickListener {
            onSaveButtonPressed()
        }

        binding.signLayout.setOnClickListener {
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

        binding.btnSelectHasil.setOnClickListener {
            pickDocument()
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

    private fun pickDocument() {
        // dialog
        val imageSelectorDialog = ImageSelectorModal()
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }

    override fun onImageSelected(imageUri: Uri) {
        val uriString = imageUri.toString()
        pemeriksaanDoc = uriString
        binding.btnSelectHasil.text = "Update Dokumen"
        binding.prevHasil.visibility = View.VISIBLE
        Picasso.get().load(uriString).fit().into(binding.prevHasil)
    }

    private fun initExistingData() {
        binding.etTujuan.editText?.setText(phqc.tujuan)
        binding.etDokumen.editText?.setText(phqc.dokumenKapal)
        binding.etPemeriksaan.editText?.setText(phqc.lokasiPemeriksaan)
        binding.etJmlABK.editText?.setText(phqc.jumlahABK.toString())
        binding.etDemam.editText?.setText(phqc.deteksiDemam.toString())
        binding.etJmlSakit.editText?.setText(phqc.jumlahSakit.toString())
        binding.etJmlSehat.editText?.setText(phqc.jumlahSehat.toString())
        binding.etJmlMeninggal.editText?.setText(phqc.jumlahMeninggal.toString())
        binding.etJmlDirujuk.editText?.setText(phqc.jumlahDirujuk.toString())
        binding.etJmlPenumpang.editText?.setText(phqc.jumlahPenumpang.toString())
        binding.etCustDemam.editText?.setText(phqc.custDeteksiDemam.toString())
        binding.etCustJmlSakit.editText?.setText(phqc.custJumlahSakit.toString())
        binding.etCustJmlSehat.editText?.setText(phqc.custJumlahSehat.toString())
        binding.etCustJmlMeninggal.editText?.setText(phqc.custJumlahMeninggal.toString())
        binding.etCustJmlDirujuk.editText?.setText(phqc.custJumlahDirujuk.toString())
        binding.etSanitasi.editText?.setText(phqc.statusSanitasi)
        binding.etKesimpulan.editText?.setText(phqc.kesimpulan)
        binding.etTanggal.editText?.setText(phqc.tanggalDiperiksa)
        nmPetugas = phqc.petugasPelaksana

        // signature
        val bitmapSign = Base64Utils.convertBase64ToBitmap(phqc.signature)
        binding.ivSign.setImageBitmap(bitmapSign)
        binding.tvPetugas.text = phqc.petugasPelaksana
        base64Sign = phqc.signature

        // doc
        pemeriksaanDoc = phqc.pemeriksaanFile
        binding.btnSelectHasil.text = "Update Dokumen"
        binding.prevHasil.visibility = View.VISIBLE
        Picasso.get().load(phqc.pemeriksaanFile).fit().into(binding.prevHasil)


        binding.signLayout.visibility = View.VISIBLE
        binding.addSignButton.visibility = View.GONE

        // radio
        radioMap["LAYANAN"] = phqc.jenisLayanan
        if (phqc.jenisLayanan == "Kedatangan"){
            binding.radioJenisLayanan.check(R.id.radio_layanan_kedatangan)
        }else{
            binding.radioJenisLayanan.check(R.id.radio_layanan_keberangkatan)
        }

        radioMap["PELAYARAN"] = phqc.jenisPelayaran
        if (phqc.jenisPelayaran == "Domestik"){
            binding.radioJenisPelayaran.check(R.id.radio_pelayaran_domestik)
        }else{
            binding.radioJenisPelayaran.check(R.id.radio_pelayaran_inter)
        }
    }

    private fun onSaveButtonPressed() {
        // Mengakses input menggunakan binding
        val tujuan = binding.etTujuan.editText?.text.toString()
        val dokumen = binding.etDokumen.editText?.text.toString()
        val pemeriksaan = binding.etPemeriksaan.editText?.text.toString()
        val jmlABK = binding.etJmlABK.editText?.text.toString()
        val demam = binding.etDemam.editText?.text.toString()
        val jmlSakit = binding.etJmlSakit.editText?.text.toString()
        val jmlSehat = binding.etJmlSehat.editText?.text.toString()
        val jmlMeninggal = binding.etJmlMeninggal.editText?.text.toString()
        val jmlDirujuk = binding.etJmlDirujuk.editText?.text.toString()
        val jmlPenumpang = binding.etJmlPenumpang.editText?.text.toString()
        val custDemam = binding.etCustDemam.editText?.text.toString()
        val custJmlSakit = binding.etCustJmlSakit.editText?.text.toString()
        val custJmlSehat = binding.etCustJmlSehat.editText?.text.toString()
        val custjmlMeninggal = binding.etCustJmlMeninggal.editText?.text.toString()
        val custJmlDirujuk = binding.etCustJmlDirujuk.editText?.text.toString()
        val sanitasi = binding.etSanitasi.editText?.text.toString()
        val kesimpulan = binding.etKesimpulan.editText?.text.toString()
        val tanggal = binding.etTanggal.editText?.text.toString()

        // cek radio
        val isAllRadio = InputValidation.isAllRadioFilled(
            binding.radioJenisLayanan,
            binding.radioJenisPelayaran
        )

        // Mengecek apakah semua input terisi
        val isAllFilled = InputValidation.isAllFieldComplete(
            binding.etTujuan,
            binding.etDokumen,
            binding.etPemeriksaan,
            binding.etJmlABK,
            binding.etDemam,
            binding.etJmlSakit,
            binding.etJmlSehat,
            binding.etJmlMeninggal,
            binding.etJmlDirujuk,
            binding.etJmlPenumpang,
            binding.etCustJmlSakit,
            binding.etCustDemam,
            binding.etCustJmlSehat,
            binding.etCustJmlMeninggal,
            binding.etCustJmlDirujuk,
            binding.etSanitasi,
            binding.etKesimpulan,
            binding.etTanggal
        )

        if (isAllFilled && pemeriksaanDoc != null && nmPetugas != null && isAllRadio){
            if (isUpdate){
                onSaveData(PHQCModel(
                    id = phqc.id,
                    kapalId = phqc.kapalId,
                    kapal = phqc.kapal,
                    tujuan = tujuan,
                    dokumenKapal = dokumen,
                    lokasiPemeriksaan = pemeriksaan,
                    jumlahABK = jmlABK.toInt(),
                    deteksiDemam = demam.toInt(),
                    jumlahSehat = jmlSehat.toInt(),
                    jumlahSakit =  jmlSakit.toInt(),
                    jumlahMeninggal = jmlMeninggal.toInt(),
                    jumlahDirujuk = jmlDirujuk.toInt(),
                    jumlahPenumpang=jmlPenumpang.toInt(),
                    custDeteksiDemam = custDemam.toInt(),
                    custJumlahSehat = custJmlSehat.toInt(),
                    custJumlahSakit = custJmlSakit.toInt(),
                    custJumlahMeninggal = custjmlMeninggal.toInt(),
                    custJumlahDirujuk = custJmlDirujuk.toInt(),
                    statusSanitasi = sanitasi,
                    kesimpulan = kesimpulan,
                    petugasPelaksana = nmPetugas!!,
                    signature = base64Sign!!,
                    pemeriksaanFile = pemeriksaanDoc!!,
                    tanggalDiperiksa = tanggal,
                    jenisLayanan = radioMap["LAYANAN"]!!,
                    jenisPelayaran = radioMap["PELAYARAN"]!!
                ))
            }else{
                onSaveData(PHQCModel(
                    kapalId = kapal.id,
                    kapal = kapal,
                    tujuan = tujuan,
                    dokumenKapal = dokumen,
                    lokasiPemeriksaan = pemeriksaan,
                    jumlahABK = jmlABK.toInt(),
                    deteksiDemam = demam.toInt(),
                    jumlahSehat = jmlSehat.toInt(),
                    jumlahSakit =  jmlSakit.toInt(),
                    jumlahMeninggal = jmlMeninggal.toInt(),
                    jumlahDirujuk = jmlDirujuk.toInt(),
                    jumlahPenumpang=jmlPenumpang.toInt(),
                    custDeteksiDemam = custDemam.toInt(),
                    custJumlahSehat = custJmlSehat.toInt(),
                    custJumlahSakit = custJmlSakit.toInt(),
                    custJumlahMeninggal = custjmlMeninggal.toInt(),
                    custJumlahDirujuk = custJmlDirujuk.toInt(),
                    statusSanitasi = sanitasi,
                    kesimpulan = kesimpulan,
                    petugasPelaksana = nmPetugas!!,
                    signature = base64Sign!!,
                    pemeriksaanFile = pemeriksaanDoc!!,
                    tanggalDiperiksa = tanggal,
                    jenisLayanan = radioMap["LAYANAN"]!!,
                    jenisPelayaran = radioMap["PELAYARAN"]!!
                ))
            }
        }else{
            Toast.makeText(this@PHQCInputActivity, "Mohon lengkapi semua input", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSaveData(phqc: PHQCModel) {
        if (dao.getPHQCById(phqc.id).isEmpty()){
            dao.createPHQC(phqc)
        }else{
            dao.updatePHQC(phqc)
        }


        Toast.makeText(this@PHQCInputActivity, "Dokumen berhasil dibuat!", Toast.LENGTH_SHORT).show()
        finish()
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