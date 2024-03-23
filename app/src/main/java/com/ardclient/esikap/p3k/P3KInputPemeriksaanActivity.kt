package com.ardclient.esikap.p3k

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.ActivityP3kInputPemeriksaanBinding
import com.ardclient.esikap.model.reusable.PemeriksaanKapalModel
import com.ardclient.esikap.utils.InputValidation

class P3KInputPemeriksaanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityP3kInputPemeriksaanBinding
    private lateinit var pemeriksaanKapal: PemeriksaanKapalModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityP3kInputPemeriksaanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // existing data
        val existingData = intent.getParcelableExtra<PemeriksaanKapalModel>("EXISTING_DATA")
        if (existingData != null){
            pemeriksaanKapal = existingData
            initExistingData()
        }else{
            pemeriksaanKapal = PemeriksaanKapalModel()
        }

        // button
        binding.saveButton.setOnClickListener {
            onSaveButton()
        }
    }

    private fun initExistingData() {
        val peralatanP3KId = when (getCheckedIntByString(pemeriksaanKapal.peralatanP3K)) {
            1 -> R.id.radio_peralatanp3k_true
            2 -> R.id.radio_peralatanp3k_false
            else -> R.id.radio_peralatanp3k_failed
        }

        val oksigenId = when(getCheckedIntByString(pemeriksaanKapal.oxygenEmergency)) {
            1 -> R.id.radio_oxygen_true
            2 -> R.id.radio_oxygen_false
            else -> R.id.radio_oxygen_failed
        }

        val faskesId = when(getCheckedIntByString(pemeriksaanKapal.fasilitasMedis)) {
            1 -> R.id.radio_faskes_true
            2 -> R.id.radio_faskes_false
            else -> R.id.radio_faskes_failed
        }

        val antibiotikId = when(getCheckedIntByString(pemeriksaanKapal.obatAntibiotik)) {
            1 -> R.id.radio_antibiotik_true
            2 -> R.id.radio_antibiotik_false
            else -> R.id.radio_antibiotik_failed
        }

        val analgesikId = when(getCheckedIntByString(pemeriksaanKapal.obatAnalgesik)) {
            1 -> R.id.radio_analgesik_true
            2 -> R.id.radio_analgesik_false
            else -> R.id.radio_analgesik_failed
        }

        val obatLainnyaId = when(getCheckedIntByString(pemeriksaanKapal.obatLainnya)) {
            1 -> R.id.radio_obatlainnya_true
            2 -> R.id.radio_obatlainnya_false
            else -> R.id.radio_obatlainnya_failed
        }

        val narkotikId = when(getCheckedIntByString(pemeriksaanKapal.obatNarkotik)){
            1 -> R.id.radio_narkotik_true
            2 -> R.id.radio_narkotik_false
            else -> R.id.radio_narkotik_failed
        }

        with(binding) {
            radioPeralatanP3K.check(peralatanP3KId)
            radioOxygen.check(oksigenId)
            radioFasilitasMedis.check(faskesId)
            radioAntibiotik.check(antibiotikId)
            radioAnalgesik.check(analgesikId)
            radioObatLainnya.check(obatLainnyaId)
            radioNarkotik.check(narkotikId)
        }
    }

    private fun getCheckedIntByString(title: String): Int {
        return when (title) {
            getString(R.string.radio_lengkap) -> {
                1
            }
            getString(R.string.radio_tidak_lengkap) -> {
                2
            }
            else -> {
                3
            }
        }
    }

    private fun onSaveButton() {
        with(binding) {
            val isAllFilled = InputValidation.isAllRadioFilled(
                radioPeralatanP3K,
                radioOxygen,
                radioFasilitasMedis,
                radioAntibiotik,
                radioAnalgesik,
                radioObatLainnya,
                radioNarkotik
            )

            if (isAllFilled){
                val peralatanP3KValue = InputValidation.getSelectedRadioGroupValue(this, radioPeralatanP3K)
                val oksigenVal = InputValidation.getSelectedRadioGroupValue(this, radioOxygen)
                val faskesVal = InputValidation.getSelectedRadioGroupValue(this, radioFasilitasMedis)
                val antibiotikVal = InputValidation.getSelectedRadioGroupValue(this, radioAntibiotik)
                val analgesikVal = InputValidation.getSelectedRadioGroupValue(this, radioAnalgesik)
                val narkotikVal = InputValidation.getSelectedRadioGroupValue(this, radioNarkotik)
                val obatLainnyaVal = InputValidation.getSelectedRadioGroupValue(this, radioObatLainnya)

                val pemeriksaanData = PemeriksaanKapalModel(
                    peralatanP3K = peralatanP3KValue,
                    oxygenEmergency = oksigenVal,
                    fasilitasMedis = faskesVal,
                    obatAntibiotik = antibiotikVal,
                    obatAnalgesik = analgesikVal,
                    obatNarkotik = narkotikVal,
                    obatLainnya = obatLainnyaVal
                )

                val intent = Intent(this@P3KInputPemeriksaanActivity, P3KInputActivity::class.java)
                intent.putExtra("PEMERIKSAAN", pemeriksaanData)
                setResult(RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this@P3KInputPemeriksaanActivity, "Semua form belum teriisi!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}