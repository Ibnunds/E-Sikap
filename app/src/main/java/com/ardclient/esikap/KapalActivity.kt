package com.ardclient.esikap

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
        }

        // init database
        database = KapalRoomDatabase.getDatabase(this)
        dao = database.getKapalDAO()

        binding.submitButton.setOnClickListener {
            val getNamaKapal = binding.inKapalName.editText?.text.toString()
            val getGrossTone = binding.inKapalGrossTone.editText?.text.toString()
            val getBendera = binding.inKapalBendera.editText?.text.toString()

            val isFormComplete = InputValidation.isAllFieldComplete(
                binding.inKapalName,
                binding.inKapalGrossTone,
                binding.inKapalBendera
            )

            if (isFormComplete){
                if (isUpdate){
                    onSaveData(KapalModel(id = kapal.id, namaKapal = getNamaKapal, grossTone = getGrossTone, bendera = getBendera))
                }else{
                    onSaveData(KapalModel(namaKapal = getNamaKapal, grossTone = getGrossTone, bendera = getBendera))
                }
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
}