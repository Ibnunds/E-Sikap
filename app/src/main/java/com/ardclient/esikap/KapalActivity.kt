package com.ardclient.esikap

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import com.ardclient.esikap.database.kapal.KapalDAO
import com.ardclient.esikap.database.kapal.KapalRoomDatabase
import com.ardclient.esikap.model.KapalModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout

class KapalActivity : AppCompatActivity() {
    private lateinit var kapal: KapalModel
    private lateinit var database: KapalRoomDatabase
    private lateinit var dao: KapalDAO
    private var isUpdate = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kapal)

        // app bar
        val header = findViewById<MaterialToolbar>(R.id.topAppBar)

        // input
        val namaKapal = findViewById<TextInputLayout>(R.id.in_kapal_name)
        val grossTone = findViewById<TextInputLayout>(R.id.in_kapal_gross_tone)
        val bendera = findViewById<TextInputLayout>(R.id.in_kapal_bendera)

        // button
        val saveButton = findViewById<Button>(R.id.submitButton)

        // existing data
        val existingData = intent.getParcelableExtra<KapalModel>("KAPAL")

        // Handle on header back nav
        header.setNavigationOnClickListener {
            finish()
        }

        // handle if existing data exist
        if (existingData != null){
            isUpdate = true
            kapal = existingData

            // input
            namaKapal.editText?.setText(kapal.namaKapal)
            grossTone.editText?.setText(kapal.grossTone)
            bendera.editText?.setText(kapal.bendera)
        }

        // init database
        database = KapalRoomDatabase.getDatabase(this)
        dao = database.getKapalDAO()

        saveButton.setOnClickListener {
            val getNamaKapal = namaKapal.editText?.text.toString()
            val getGrossTone = grossTone.editText?.text.toString()
            val getBendera = bendera.editText?.text.toString()

            // Hide keyboard
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            // clear focus
            bendera.clearFocus()
            namaKapal.clearFocus()
            grossTone.clearFocus()

            if (getNamaKapal.isEmpty() || getGrossTone.isEmpty() || getBendera.isEmpty()){
                Toast.makeText(this, "Form belum lengkap!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
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
}