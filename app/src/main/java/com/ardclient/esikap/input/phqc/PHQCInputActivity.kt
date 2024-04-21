package com.ardclient.esikap.input.phqc

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.squareup.picasso.Picasso


class PHQCInputActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var kapal: KapalModel
    private lateinit var phqc: PHQCModel
    private var isUpdate: Boolean = false
    private var launcher: ActivityResultLauncher<Intent>? = null

    // sign
    private var nmPetugas: String? = null
    private var base64Sign: String? = null
    private var nmKapten: String? = null
    private var base64SignKapten: String? = null
    private var nmPetugas2: String = ""
    private var base64SignPetugas2: String = ""
    private var nipPetugas2: String = ""
    private var nmPetugas3: String = ""
    private var base64SignPetugas3: String = ""
    private var nipPetugas3: String = ""

    private lateinit var binding: ActivityPhqcInputBinding

    // database
    private lateinit var database: PHQCRoomDatabase
    private lateinit var dao: PHQCDao

    // doc
    private var pemeriksaanDoc: String? = null

    // Radio
    private val radioMap = mutableMapOf<String, String?>()

    private var username = ""

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
                val type = data?.getStringExtra("TYPE")

                val nama = data?.getStringExtra("NAMA")
                val nip = data?.getStringExtra("NIP")
                val decodedSign = data?.getByteArrayExtra("SIGNATURE")

                val encodedSign = BitmapFactory.decodeByteArray(decodedSign, 0, decodedSign!!.size)
                val sign = Base64Utils.convertBitmapToBase64(encodedSign)

                onSignedResult(type, nama, sign, encodedSign, nip)
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

        username = session.userName!!


        // Add Sign Petugas
        binding.addSignButton.setOnClickListener {
            val intent = Intent(this@PHQCInputActivity, SignatureActivity::class.java)
            intent.putExtra("NAMA", session.name)
            intent.putExtra("TYPE", "PETUGAS")
            launcher!!.launch(intent)
        }


        // Add Sign Kapten
        binding.addSignKaptenButton.setOnClickListener {
            val intent = Intent(this@PHQCInputActivity, SignatureActivity::class.java)
            intent.putExtra("NAMA", kapal.kaptenKapal)
            intent.putExtra("TYPE", "KAPTEN")
            launcher!!.launch(intent)
        }


        // Add Sign Petugas 2
        binding.addSignPT2Button.setOnClickListener {
            val intent = Intent(this@PHQCInputActivity, SignatureActivity::class.java)
            intent.putExtra("NAMA", "")
            intent.putExtra("NIP", "")
            intent.putExtra("TYPE", "PETUGAS_2")
            launcher!!.launch(intent)
        }

        // Add Sign Petugas 3
        binding.addSignPT3Button.setOnClickListener {
            val intent = Intent(this@PHQCInputActivity, SignatureActivity::class.java)
            intent.putExtra("NAMA", "")
            intent.putExtra("NIP", "")
            intent.putExtra("TYPE", "PETUGAS_3")
            launcher!!.launch(intent)
        }


        // Layout Petugas
        binding.signLayout.setOnClickListener {
            val namaOfficer = binding.tvPetugas.text.toString()

            val intent = Intent(this, SignatureActivity::class.java)
            intent.putExtra("NAMA", namaOfficer)
            intent.putExtra("TYPE", "PETUGAS")
            launcher!!.launch(intent)
        }


        // Layout Kapten
        binding.signKaptenLayout.setOnClickListener {
            val kapten = binding.tvKapten.text.toString()

            val intent = Intent(this@PHQCInputActivity, SignatureActivity::class.java)
            intent.putExtra("NAMA", kapten)
            intent.putExtra("TYPE", "KAPTEN")
            launcher!!.launch(intent)
        }

        // Layout Petugas 2
        binding.signPT2Layout.setOnClickListener {
            val nama = binding.tvPetugas2.text.toString()

            val intent = Intent(this@PHQCInputActivity, SignatureActivity::class.java)
            intent.putExtra("NAMA", nama)
            intent.putExtra("NIP", nipPetugas2)
            intent.putExtra("TYPE", "PETUGAS_2")
            launcher!!.launch(intent)
        }

        // Layout Petugas 3
        binding.signPT3Layout.setOnClickListener {
            val nama = binding.tvPetugas3.text.toString()

            val intent = Intent(this@PHQCInputActivity, SignatureActivity::class.java)
            intent.putExtra("NAMA", nama)
            intent.putExtra("NIP", nipPetugas3)
            intent.putExtra("TYPE", "PETUGAS_3")
            launcher!!.launch(intent)
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

        // time picker
        val timePicker =
            MaterialTimePicker.Builder()
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
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

        binding.saveButton.setOnClickListener {
            onSaveButtonPressed()
        }
    }

    private fun onSignedResult(
        type: String?,
        nama: String?,
        sign: String,
        encodedSign: Bitmap?,
        nip: String?
    ) {
        when(type){
            "KAPTEN" -> {
                binding.addSignKaptenButton.visibility = View.GONE
                binding.signKaptenLayout.visibility = View.VISIBLE
                binding.tvKapten.text = nama
                binding.ivSignKapten.setImageBitmap(encodedSign)
                nmKapten = nama
                base64SignKapten = sign
            }

            "PETUGAS" -> {
                binding.addSignButton.visibility = View.GONE
                binding.signLayout.visibility = View.VISIBLE
                binding.tvPetugas.text = nama
                binding.ivSign.setImageBitmap(encodedSign)
                nmPetugas = nama
                base64Sign = sign
            }

            "PETUGAS_2" -> {
                binding.addSignPT2Button.visibility = View.GONE
                binding.signPT2Layout.visibility = View.VISIBLE
                binding.tvPetugas2.text = nama
                binding.ivSignPT2.setImageBitmap(encodedSign)
                binding.tvPetugas2NIP.text = nip
                nmPetugas2 = nama!!
                base64SignPetugas2 = sign
                nipPetugas2 = nip!!
            }

            "PETUGAS_3" -> {
                binding.addSignPT3Button.visibility = View.GONE
                binding.signPT3Layout.visibility = View.VISIBLE
                binding.tvPetugas3.text = nama
                binding.ivSignPT3.setImageBitmap(encodedSign)
                binding.tvPetugas3NIP.text = nip
                nmPetugas3 = nama!!
                base64SignPetugas3 = sign
                nipPetugas3 = nip!!
            }
        }
//        if (type == "KAPTEN"){
//            binding.addSignKaptenButton.visibility = View.GONE
//            binding.signKaptenLayout.visibility = View.VISIBLE
//            binding.tvKapten.text = nama
//            binding.ivSignKapten.setImageBitmap(encodedSign)
//            nmKapten = nama
//            base64SignKapten = sign
//        }else{
//            binding.addSignButton.visibility = View.GONE
//            binding.signLayout.visibility = View.VISIBLE
//            binding.tvPetugas.text = nama
//            binding.ivSign.setImageBitmap(encodedSign)
//            nmPetugas = nama
//            base64Sign = sign
//        }
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
        binding.etMasalahKesehatan.editText?.setText(phqc.masalahKesehatan)
        binding.etJam.editText?.setText(phqc.jamDiperiksa)
        nmPetugas = phqc.petugasPelaksana
        nmKapten = phqc.kapten

        // signature
        val bitmapSign = Base64Utils.convertBase64ToBitmap(phqc.signature)
        binding.ivSign.setImageBitmap(bitmapSign)
        binding.tvPetugas.text = phqc.petugasPelaksana
        base64Sign = phqc.signature

        val kaptenSign = Base64Utils.convertBase64ToBitmap(phqc.signatureKapten)
        binding.ivSignKapten.setImageBitmap(kaptenSign)
        binding.tvKapten.text = phqc.kapten
        base64SignKapten = phqc.signatureKapten

        if (phqc.nipPetugas2.isNotEmpty()){
            val sign = Base64Utils.convertBase64ToBitmap(phqc.signPetugas2)
            binding.ivSignPT2.setImageBitmap(sign)
            binding.tvPetugas2.text = phqc.namaPetugas2
            base64SignPetugas2 = phqc.signPetugas2
            nipPetugas2 = phqc.nipPetugas2
            nmPetugas2 = phqc.namaPetugas2
            binding.signPT2Layout.visibility = View.VISIBLE
            binding.addSignPT2Button.visibility = View.GONE
        }

        if (phqc.nipPetugas3.isNotEmpty()){
            val sign = Base64Utils.convertBase64ToBitmap(phqc.signPetugas3)
            binding.ivSignPT3.setImageBitmap(sign)
            binding.tvPetugas3.text = phqc.namaPetugas3
            base64SignPetugas3 = phqc.signPetugas3
            nipPetugas3 = phqc.nipPetugas3
            nmPetugas3 = phqc.namaPetugas3
            binding.signPT3Layout.visibility = View.VISIBLE
            binding.addSignPT3Button.visibility = View.GONE
        }

        // doc
        pemeriksaanDoc = phqc.pemeriksaanFile
        binding.btnSelectHasil.text = "Update Dokumen"
        binding.prevHasil.visibility = View.VISIBLE
        Picasso.get().load(phqc.pemeriksaanFile).fit().into(binding.prevHasil)


        binding.signLayout.visibility = View.VISIBLE
        binding.addSignButton.visibility = View.GONE

        binding.signKaptenLayout.visibility = View.VISIBLE
        binding.addSignKaptenButton.visibility = View.GONE

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
        val masalahKesehatan = binding.etMasalahKesehatan.editText?.text.toString()
        val jam = binding.etJam.editText?.text.toString()

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
            binding.etTanggal,
            binding.etMasalahKesehatan,
            binding.etJam
        )

        if (isAllFilled && pemeriksaanDoc != null && nmPetugas != null && nmKapten != null && isAllRadio){
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
                    jenisPelayaran = radioMap["PELAYARAN"]!!,
                    masalahKesehatan = masalahKesehatan,
                    kapten = nmKapten!!,
                    signatureKapten = base64SignKapten!!,
                    jamDiperiksa = jam,
                    username = username,
                    namaPetugas2 = nmPetugas2,
                    namaPetugas3 = nmPetugas3,
                    signPetugas2 = base64SignPetugas2,
                    signPetugas3 = base64SignPetugas3,
                    nipPetugas2 = nipPetugas2,
                    nipPetugas3 = nipPetugas3
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
                    jenisPelayaran = radioMap["PELAYARAN"]!!,
                    masalahKesehatan = masalahKesehatan,
                    kapten = nmKapten!!,
                    signatureKapten = base64SignKapten!!,
                    jamDiperiksa = jam,
                    username = username,
                    namaPetugas2 = nmPetugas2,
                    namaPetugas3 = nmPetugas3,
                    signPetugas2 = base64SignPetugas2,
                    signPetugas3 = base64SignPetugas3,
                    nipPetugas2 = nipPetugas2,
                    nipPetugas3 = nipPetugas3
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