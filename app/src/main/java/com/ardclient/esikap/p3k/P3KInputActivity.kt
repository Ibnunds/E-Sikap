package com.ardclient.esikap.p3k

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ardclient.esikap.R
import com.ardclient.esikap.SanitasiInputActivity
import com.ardclient.esikap.databinding.ActivityP3kInputBinding
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.model.reusable.PemeriksaanKapalModel
import com.ardclient.esikap.model.reusable.SanitasiModel
import com.ardclient.esikap.sscec.SSCECInputDataUmumActivity

class P3KInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityP3kInputBinding
    private lateinit var kapal: KapalModel

    private var launcher: ActivityResultLauncher<Intent>? = null

    // model
    private lateinit var P3KDataUmum: P3KModel
    private lateinit var P3KPemeriksaan: PemeriksaanKapalModel
    private lateinit var P3KRekomendasi: P3KModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityP3kInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // init mode
        P3KDataUmum = P3KModel()
        P3KRekomendasi = P3KModel()
        P3KPemeriksaan = PemeriksaanKapalModel()

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
    }
}