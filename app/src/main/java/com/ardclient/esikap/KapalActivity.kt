package com.ardclient.esikap

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.ardclient.esikap.database.kapal.KapalDAO
import com.ardclient.esikap.database.kapal.KapalRoomDatabase
import com.ardclient.esikap.databinding.ActivityKapalBinding
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.utils.InputValidation

class KapalActivity : AppCompatActivity() {
    private lateinit var kapal: KapalModel
    private lateinit var database: KapalRoomDatabase
    private lateinit var dao: KapalDAO
    private var isUpdate = false
    private lateinit var binding: ActivityKapalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKapalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // existing data
        val existingData = intent.getParcelableExtra<KapalModel>("KAPAL")

        // Handle on header back nav
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // handle if existing data exist
        if (existingData != null){
            isUpdate = true
            kapal = existingData

            // input
            binding.inKapalName.editText?.setText(kapal.namaKapal)
            binding.inKapalGrossTone.editText?.setText(kapal.grossTone)
            binding.inKapalBendera.editText?.setText(kapal.bendera)
            binding.inKapalImo.editText?.setText(kapal.imo)
            binding.inKapalAgen.editText?.setText(kapal.namaAgen)
            binding.inKapalAsal.editText?.setText(kapal.negaraAsal)
            binding.inKapalKapten.editText?.setText(kapal.kaptenKapal)

            if (kapal.tipeKapal == "Ferry"){
                binding.radioType.check(R.id.radio_type_true)
            }else{
                binding.radioType.check(R.id.radio_type_false)
            }
        }

        // init database
        database = KapalRoomDatabase.getDatabase(this)
        dao = database.getKapalDAO()

        binding.submitButton.setOnClickListener {
            val getNamaKapal = binding.inKapalName.editText?.text.toString()
            val getGrossTone = binding.inKapalGrossTone.editText?.text.toString()
            val getBendera = binding.inKapalBendera.editText?.text.toString()
            val getIMO = binding.inKapalImo.editText?.text.toString()
            val getAgen = binding.inKapalAgen.editText?.text.toString()
            val getAsal = binding.inKapalAsal.editText?.text.toString()
            val getKapten = binding.inKapalKapten.editText?.text.toString()
            val isTipeSelected = binding.radioType.checkedRadioButtonId != -1
            val radioTypeVal = getSelectedRadioGroupValue(binding.radioType)

            val isFormComplete = InputValidation.isAllFieldComplete(
                binding.inKapalName,
                binding.inKapalGrossTone,
                binding.inKapalBendera,
                binding.inKapalImo,
                binding.inKapalAgen,
                binding.inKapalAsal,
                binding.inKapalKapten,
            )

            if (isFormComplete && isTipeSelected){
                if (isUpdate){
                    onSaveData(KapalModel(
                        id = kapal.id,
                        namaKapal = getNamaKapal,
                        grossTone = getGrossTone,
                        bendera = getBendera,
                        imo = getIMO,
                        namaAgen = getAgen,
                        negaraAsal = getAsal,
                        kaptenKapal = getKapten,
                        tipeKapal = radioTypeVal
                    ))
                }else{
                    onSaveData(KapalModel(
                        namaKapal = getNamaKapal,
                        grossTone = getGrossTone,
                        bendera = getBendera,
                        imo = getIMO,
                        namaAgen = getAgen,
                        negaraAsal = getAsal,
                        kaptenKapal = getKapten,
                        tipeKapal = radioTypeVal
                    ))
                }
            }else{
                Toast.makeText(this@KapalActivity, "Data belum lengkap!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSaveData(kapal: KapalModel) {
        if (dao.getKapalById(kapal.id).isEmpty()){
            dao.insertKapal(kapal)
        }else{
            dao.updateKapal(kapal)
        }

        Toast.makeText(this, "Data kapal berhasil disimpan!", Toast.LENGTH_SHORT).show()
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

    fun getSelectedRadioGroupValue(radioGroup: RadioGroup): String {
        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        val checkedRadioButton = findViewById<RadioButton>(checkedRadioButtonId)
        return checkedRadioButton.text.toString()
    }
}