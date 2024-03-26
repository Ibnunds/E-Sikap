package com.ardclient.esikap.p3k

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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ardclient.esikap.R
import com.ardclient.esikap.SanitasiInputActivity
import com.ardclient.esikap.database.p3k.P3KDao
import com.ardclient.esikap.database.p3k.P3KRoomDatabase
import com.ardclient.esikap.databinding.ActivityP3kInputBinding
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.model.reusable.PemeriksaanKapalModel
import com.ardclient.esikap.model.reusable.SanitasiModel
import com.ardclient.esikap.sscec.SSCECInputDataUmumActivity
import com.ardclient.esikap.utils.DialogUtils

class P3KInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityP3kInputBinding
    private lateinit var kapal: KapalModel
    private var isUpdate: Boolean = false
    private var existingID: Int = 0

    private var launcher: ActivityResultLauncher<Intent>? = null

    // model
    private lateinit var P3KDataUmum: P3KModel
    private lateinit var P3KPemeriksaan: PemeriksaanKapalModel
    private lateinit var P3KRekomendasi: P3KModel

    // DB
    private lateinit var db: P3KRoomDatabase
    private lateinit var dao: P3KDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityP3kInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // existing data
        val existingData = intent.getParcelableExtra<P3KModel>("P3K")
        if (existingData != null){
            isUpdate = true
            existingID = existingData.id
            P3KDataUmum = existingData
            P3KRekomendasi = existingData
            P3KPemeriksaan = existingData.pemeriksaan

            // button
            binding.bottomContainerEdit.visibility = View.VISIBLE
            binding.bottomContainerSave.visibility = View.GONE

            // header
            binding.topAppBar.title = "Dokumen P3K"

            // chip
            binding.chipSanitasi.isChecked = true
            binding.chipSanitasi.text = "Lengkap"
            binding.chipDataUmum.isChecked = true
            binding.chipDataUmum.text = "Lengkap"
            binding.chipDokumenKapal.isChecked = true
            binding.chipDokumenKapal.text = "Lengkap"
        }else{
            // init mode
            P3KDataUmum = P3KModel()
            P3KRekomendasi = P3KModel()
            P3KPemeriksaan = PemeriksaanKapalModel()
        }



        // db
        db = P3KRoomDatabase.getDatabase(this)
        dao = db.getP3KDAO()

        // existing kapal data
        kapal = intent.getParcelableExtra("KAPAL") ?: KapalModel()

        // handle input result
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                // Receive Data

                // == Basic Data
                val basicData = data?.getParcelableExtra<P3KModel>("BASIC")
                if (basicData != null) {
                    P3KDataUmum = basicData
                    binding.chipDataUmum.isChecked = true
                    binding.chipDataUmum.text = "Lengkap"
                }

                // == Doc Data
                val signData = data?.getParcelableExtra<P3KModel>("SIGNATURE")
                if (signData != null) {
                    P3KRekomendasi = signData
                    binding.chipDokumenKapal.isChecked = true
                    binding.chipDokumenKapal.text = "Lengkap"
                }

                // == Sanitasi Data
                val pemeriksaan = data?.getParcelableExtra<PemeriksaanKapalModel>("PEMERIKSAAN")
                if (pemeriksaan != null) {
                    P3KPemeriksaan = pemeriksaan
                    binding.chipSanitasi.isChecked = true
                    binding.chipSanitasi.text = "Lengkap"
                }

            }
        }

        binding.cardDataUmum.setOnClickListener {
            val intent = Intent(this, P3KInputDataUmumActivity::class.java)
            if (P3KDataUmum.jenisLayanan.isNotEmpty()) {
                intent.putExtra("EXISTING_DATA", P3KDataUmum)
            }
            launcher?.launch(intent)
        }

        binding.cardSanitasi.setOnClickListener {
            val intent = Intent(this, P3KInputPemeriksaanActivity::class.java)
            if (P3KPemeriksaan.peralatanP3K.isNotEmpty()) {
                intent.putExtra("EXISTING_DATA", P3KPemeriksaan)
            }
            launcher?.launch(intent)
        }

        binding.cardDokumenKapal.setOnClickListener {
            val intent = Intent(this, P3KInputRekomendasiActivity::class.java)
            if (P3KRekomendasi.recP3K.isNotEmpty()) {
                intent.putExtra("EXISTING_DATA", P3KRekomendasi)
            }
            launcher?.launch(intent)
        }

        binding.submitButton.setOnClickListener {
            onSaveButton()
        }

        binding.updateButton.setOnClickListener {
            onSaveButton()
        }

        binding.deleteButton.setOnClickListener {
            DialogUtils.showDeleteDialog(this, object: DialogUtils.OnDeleteConfirmListener {
                override fun onDeleteConfirmed() {
                    onDeleteData()
                }
            })
        }
    }

    private fun onSaveButton() {
        if (binding.chipDataUmum.isChecked && binding.chipDokumenKapal.isChecked && binding.chipSanitasi.isChecked){
            val data = if (isUpdate) {
                P3KModel(
                    id = existingID,
                    kapal = kapal,
                    kapalId = kapal.id,
                    pemeriksaan = P3KPemeriksaan,
                    jenisLayanan = P3KDataUmum.jenisLayanan,
                    jenisPelayanan = P3KDataUmum.jenisPelayanan,
                    lokasiPemeriksaan = P3KDataUmum.lokasiPemeriksaan,
                    tglDiperiksa = P3KDataUmum.tglDiperiksa,
                    jmlABK = P3KDataUmum.jmlABK,
                    abkSehat = P3KDataUmum.abkSehat,
                    abkSakit = P3KDataUmum.abkSakit,
                    recP3K = P3KRekomendasi.recP3K,
                    recTanggal = P3KRekomendasi.recTanggal,
                    recJam = P3KRekomendasi.recJam,
                    signKapten = P3KRekomendasi.signKapten,
                    signPetugas = P3KRekomendasi.signPetugas,
                    signNamaKapten = P3KRekomendasi.signNamaKapten,
                    signNamaPetugas = P3KRekomendasi.signNamaPetugas
                )
            } else {
                P3KModel(
                    kapal = kapal,
                    kapalId = kapal.id,
                    pemeriksaan = P3KPemeriksaan,
                    jenisLayanan = P3KDataUmum.jenisLayanan,
                    jenisPelayanan = P3KDataUmum.jenisPelayanan,
                    lokasiPemeriksaan = P3KDataUmum.lokasiPemeriksaan,
                    tglDiperiksa = P3KDataUmum.tglDiperiksa,
                    jmlABK = P3KDataUmum.jmlABK,
                    abkSehat = P3KDataUmum.abkSehat,
                    abkSakit = P3KDataUmum.abkSakit,
                    recP3K = P3KRekomendasi.recP3K,
                    recTanggal = P3KRekomendasi.recTanggal,
                    recJam = P3KRekomendasi.recJam,
                    signKapten = P3KRekomendasi.signKapten,
                    signPetugas = P3KRekomendasi.signPetugas,
                    signNamaKapten = P3KRekomendasi.signNamaKapten,
                    signNamaPetugas = P3KRekomendasi.signNamaPetugas
                )
            }

            onSubmitData(data)
        }else{
            Toast.makeText(this@P3KInputActivity, "Data belum lengkap!", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun onSubmitData(data: P3KModel) {
        if (dao.getP3KById(data.id).isEmpty()){
            dao.createP3K(data)
            Toast.makeText(this, "Dokumen berhasil dibuat!", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            dao.updateP3K(data)
            Toast.makeText(this, "Dokumen berhasil diupdate!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onDeleteData() {
        dao.deleteP3K(P3KDataUmum)
        Toast.makeText(this, "Dokumen berhasil dihapus!", Toast.LENGTH_SHORT).show()
        finish()
    }
}