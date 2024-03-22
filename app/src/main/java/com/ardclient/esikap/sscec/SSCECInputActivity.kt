package com.ardclient.esikap.sscec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ardclient.esikap.SanitasiInputActivity
import com.ardclient.esikap.database.sscec.SSCECDao
import com.ardclient.esikap.database.sscec.SSCECRoomDatabase
import com.ardclient.esikap.databinding.ActivitySscecInputBinding
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.model.reusable.SanitasiModel

class SSCECInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySscecInputBinding
    private var launcher: ActivityResultLauncher<Intent>? = null

    private lateinit var SSCECSanitasi: SanitasiModel
    private lateinit var SSCECDataUmum: SSCECModel
    private lateinit var SSCECSignature: SSCECModel

    private lateinit var kapal: KapalModel

    // db
    private lateinit var db: SSCECRoomDatabase
    private lateinit var dao: SSCECDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySscecInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // INIT DB
        db = SSCECRoomDatabase.getDatabase(this)
        dao = db.getSSCECDao()

        // init model
        SSCECSanitasi = SanitasiModel()
        SSCECDataUmum = SSCECModel()
        SSCECSignature = SSCECModel()

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
                val basicData = data?.getParcelableExtra<SSCECModel>("BASIC")
                if (basicData != null) {
                    SSCECDataUmum = basicData
                    binding.chipDataUmum.isChecked = true
                    binding.chipDataUmum.text = "Lengkap"
                }

                // == Doc Data
                val signData = data?.getParcelableExtra<SSCECModel>("SIGNATURE")
                if (signData != null) {
                    SSCECSignature = signData
                    binding.chipDokumenKapal.isChecked = true
                    binding.chipDokumenKapal.text = "Lengkap"
                }

                // == Sanitasi Data
                val sanitasi = data?.getParcelableExtra<SanitasiModel>("SANITASI")
                if (sanitasi != null) {
                    SSCECSanitasi = sanitasi
                    binding.chipSanitasi.isChecked = true
                    binding.chipSanitasi.text = "Lengkap"
                }

            }
        }

        binding.cardSanitasi.setOnClickListener {
            val intent = Intent(this, SanitasiInputActivity::class.java)
            intent.putExtra("SENDER", "SSCEC")
            if (SSCECSanitasi.sanDapur.isNotEmpty()) {
                intent.putExtra("EXISTING_DATA", SSCECSanitasi)
            }
            launcher?.launch(intent)
        }

        binding.cardDataUmum.setOnClickListener {
            val intent = Intent(this, SSCECInputDataUmumActivity::class.java)
            if (SSCECDataUmum.pelabuhanTujuan.isNotEmpty()) {
                intent.putExtra("EXISTING_DATA", SSCECDataUmum)
            }
            launcher?.launch(intent)
        }

        binding.cardDokumenKapal.setOnClickListener {
            val intent = Intent(this, SSCECInputRekomendasiActivity::class.java)
            if (SSCECSignature.signNamaKapten.isNotEmpty()) {
                intent.putExtra("EXISTING_DATA", SSCECSignature)
            }
            launcher?.launch(intent)
        }

        binding.submitButton.setOnClickListener {
            if (binding.chipDataUmum.isChecked && binding.chipSanitasi.isChecked && binding.chipDokumenKapal.isChecked) {
                val sscec = SSCECModel(
                    kapalId = kapal.id,
                    kapal = kapal,
                    pelabuhanTujuan = SSCECDataUmum.pelabuhanTujuan,
                    tglTiba = SSCECDataUmum.tglTiba,
                    lokasiSandar = SSCECDataUmum.lokasiSandar,
                    jumlahABKAsing = SSCECDataUmum.jumlahABKAsing,
                    asingSehat = SSCECDataUmum.asingSehat,
                    asingSakit = SSCECDataUmum.asingSakit,
                    jumlahABKWNI = SSCECDataUmum.jumlahABKWNI,
                    wniSehat = SSCECDataUmum.wniSehat,
                    wniSakit = SSCECDataUmum.wniSakit,
                    recSSCEC = SSCECSignature.recSSCEC,
                    recTanggal = SSCECSignature.recTanggal,
                    recJam = SSCECSignature.recJam,
                    signNamaPetugas = SSCECSignature.signNamaPetugas,
                    signNamaKapten = SSCECSignature.signNamaKapten,
                    signKapten = SSCECSignature.signKapten,
                    signPetugas = SSCECSignature.signPetugas,
                    sanitasi = SSCECSanitasi
                )

                onSubmitData(sscec)
            } else {
                Toast.makeText(this@SSCECInputActivity, "Data belum lengkap!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun onSubmitData(sscec: SSCECModel) {
        if (dao.getSSCECById(sscec.id).isEmpty()){
            dao.createSSCEC(sscec)
        }else{
            dao.updateSSCEC(sscec)
        }

        Toast.makeText(this, "Dokumen berhasil dibuat!", Toast.LENGTH_SHORT).show()
        finish()
    }
}