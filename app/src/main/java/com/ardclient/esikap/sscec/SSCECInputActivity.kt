package com.ardclient.esikap.sscec

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ardclient.esikap.R
import com.ardclient.esikap.SanitasiInputActivity
import com.ardclient.esikap.databinding.ActivitySscecInputBinding
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.model.reusable.SanitasiModel

class SSCECInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySscecInputBinding
    private var launcher: ActivityResultLauncher<Intent>? = null

    private lateinit var SSCECSanitasi: SanitasiModel
    private lateinit var SSCECDataUmum: SSCECModel
    private lateinit var SSCECSignature: SSCECModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySscecInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // init model
        SSCECSanitasi = SanitasiModel()
        SSCECDataUmum = SSCECModel()
        SSCECSignature = SSCECModel()

        // handle input result
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                // Receive Data

                // == Basic Data
                val basicData = data?.getParcelableExtra<SSCECModel>("BASIC")
                if (basicData !=null){
                    SSCECDataUmum = basicData
                    binding.chipDataUmum.isChecked = true
                    binding.chipDataUmum.text = "Lengkap"
                }

                // == Doc Data
                val signData = data?.getParcelableExtra<SSCECModel>("SIGNATURE")
                if (signData != null){
                    SSCECSignature = signData
                    binding.chipDokumenKapal.isChecked = true
                    binding.chipDokumenKapal.text = "Lengkap"
                }

                // == Sanitasi Data
                val sanitasi = data?.getParcelableExtra<SanitasiModel>("SANITASI")
                if (sanitasi !=null){
                    SSCECSanitasi = sanitasi
                    binding.chipSanitasi.isChecked = true
                    binding.chipSanitasi.text = "Lengkap"
                }

            }
        }

        binding.cardSanitasi.setOnClickListener {
            val intent = Intent(this, SanitasiInputActivity::class.java)
            intent.putExtra("SENDER", "SSCEC")
            if (SSCECSanitasi.sanDapur.isNotEmpty()){
                intent.putExtra("EXISTING_DATA", SSCECSanitasi)
            }
            launcher?.launch(intent)
        }

        binding.cardDataUmum.setOnClickListener {
            val intent = Intent(this, SSCECInputDataUmumActivity::class.java)
            if (SSCECDataUmum.pelabuhanTujuan.isNotEmpty()){
                intent.putExtra("EXISTING_DATA", SSCECDataUmum)
            }
            launcher?.launch(intent)
        }
    }
}