package com.ardclient.esikap.input.phqc

import android.content.Intent
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
import com.ardclient.esikap.input.SignatureActivity
import com.ardclient.esikap.database.phqc.PHQCDao
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.databinding.ActivityPhqcInputBinding
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.PHQCModel
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.InputValidation


class PHQCInputActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhqcInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.addSignButton.setOnClickListener {
            val intent = Intent(this@PHQCInputActivity, SignatureActivity::class.java)
            intent.putExtra("NAMA", "")
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
        binding.etSanitasi.editText?.setText(phqc.statusSanitasi)
        binding.etKesimpulan.editText?.setText(phqc.kesimpulan)
        nmPetugas = phqc.petugasPelaksana

        // signature
        val bitmapSign = Base64Utils.convertBase64ToBitmap(phqc.signature)
        binding.ivSign.setImageBitmap(bitmapSign)
        binding.tvPetugas.text = phqc.petugasPelaksana
        base64Sign = phqc.signature

        binding.signLayout.visibility = View.VISIBLE
        binding.addSignButton.visibility = View.GONE
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
        val sanitasi = binding.etSanitasi.editText?.text.toString()
        val kesimpulan = binding.etKesimpulan.editText?.text.toString()

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
            binding.etSanitasi,
            binding.etKesimpulan
        )

        if (isAllFilled){
            if (nmPetugas != null){
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
                        statusSanitasi = sanitasi,
                        kesimpulan = kesimpulan,
                        petugasPelaksana = nmPetugas!!,
                        signature = base64Sign!!
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
                        statusSanitasi = sanitasi,
                        kesimpulan = kesimpulan,
                        petugasPelaksana = nmPetugas!!,
                        signature = base64Sign!!
                    ))
                }

            }else{
                Toast.makeText(this@PHQCInputActivity, "Belum ada tanda tangan!", Toast.LENGTH_SHORT).show()
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