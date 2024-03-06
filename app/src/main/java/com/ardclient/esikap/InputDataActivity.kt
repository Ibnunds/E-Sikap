package com.ardclient.esikap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.ardclient.esikap.database.sample.SampleDAO
import com.ardclient.esikap.database.sample.SampleRoomDatabase
import com.ardclient.esikap.model.Sample
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout

class InputDataActivity : AppCompatActivity() {
    private lateinit var note: Sample
    private var isUpdate = false
    private lateinit var database: SampleRoomDatabase
    private lateinit var dao: SampleDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_data)

        val topBarBackutton = findViewById<MaterialToolbar>(R.id.topAppBar)
        val submitButton = findViewById<Button>(R.id.submitButton)
        // input
        val inputName = findViewById<TextInputLayout>(R.id.etName)
        val inputDesc = findViewById<TextInputLayout>(R.id.etDesc)

        // handle back
        topBarBackutton.setNavigationOnClickListener {
            super.onBackPressed()
        }

        // database
        database = SampleRoomDatabase.getDatabase(applicationContext)
        dao = database.getSampleDao()

        submitButton.setOnClickListener {
            val name = inputName.editText?.text.toString()
            val desc = inputDesc.editText?.text.toString()

            if (name.isEmpty() || desc.isEmpty()){
                Toast.makeText(applicationContext, "Form belum lengkap!", Toast.LENGTH_SHORT).show()
            }else{
                saveSample(Sample(title = name, message = desc))
                finish()
            }


        }
    }

    private fun saveSample(sample: Sample) {
        dao.insertSample(sample)

        Toast.makeText(applicationContext, "Sample Saved!", Toast.LENGTH_SHORT)
    }
}