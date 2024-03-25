package com.ardclient.esikap.cop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ardclient.esikap.SanitasiInputActivity
import com.ardclient.esikap.databinding.ActivityCopInputBinding
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.model.reusable.SanitasiModel

class CopInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCopInputBinding
    private var launcher: ActivityResultLauncher<Intent>? = null

    // result data
    private lateinit var copBasicData: COPModel
    private lateinit var copDocData: DokumenKapalModel
    private lateinit var copSanitasi: SanitasiModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // init value
        copBasicData = COPModel()
        copDocData = DokumenKapalModel()
        copSanitasi = SanitasiModel()

        // handle input result
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                // Receive Data

                // == Basic Data
                val basicCOPData = data?.getParcelableExtra<COPModel>("COP_BASIC")
                if (basicCOPData !=null){
                    copBasicData = basicCOPData
                    binding.chipCOPDataUmum.isChecked = true
                    binding.chipCOPDataUmum.text = "Lengkap"
                }

                // == Doc Data
                val docCOPdata = data?.getParcelableExtra<DokumenKapalModel>("COP_DOC")
                if (docCOPdata != null){
                    copDocData = docCOPdata
                    binding.chipCOPDokumenKapal.isChecked = true
                    binding.chipCOPDokumenKapal.text = "Lengkap"
                }

                // == Sanitasi Data
                val sanitasi = data?.getParcelableExtra<SanitasiModel>("SANITASI")
                if (sanitasi !=null){
                    copSanitasi = sanitasi
                    binding.chipCOPSanitasi.isChecked = true
                    binding.chipCOPSanitasi.text = "Lengkap"
                }

            }
        }

        // card button
        binding.cardCOPDataUmum.setOnClickListener {
            val intent = Intent(this, CopInputDataUmumActivity::class.java)
            if (copBasicData.tujuan.isNotEmpty()){
                intent.putExtra("EXISTING_DATA", copBasicData)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPDokumenKapal.setOnClickListener {
            val intent = Intent(this, CopInputDokumenActivity::class.java)
            if (copDocData.aktifitasKapal.isNotEmpty()){
                intent.putExtra("EXISTING_DATA", copDocData)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPSanitasi.setOnClickListener {
            val intent = Intent(this, SanitasiInputActivity::class.java)
            intent.putExtra("SENDER", "COP")
            if (copSanitasi.sanDapur.isNotEmpty()){
                intent.putExtra("EXISTING_DATA", copSanitasi)
            }
            launcher?.launch(intent)
        }

        binding.submitButton.setOnClickListener {

        }
    }


}