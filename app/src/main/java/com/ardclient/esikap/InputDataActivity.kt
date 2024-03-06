package com.ardclient.esikap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.ardclient.esikap.database.sample.SampleDAO
import com.ardclient.esikap.database.sample.SampleRoomDatabase
import com.ardclient.esikap.model.Sample
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout

class InputDataActivity : AppCompatActivity() {
    private lateinit var sample: Sample
    private var isUpdate = false
    private lateinit var database: SampleRoomDatabase
    private lateinit var dao: SampleDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_data)

        val topBarBackutton = findViewById<MaterialToolbar>(R.id.topAppBar)
        val submitButton = findViewById<Button>(R.id.submitButton) // save button
        val deleteButton = findViewById<Button>(R.id.deleteButton) // delete button
        val uploadButton = findViewById<Button>(R.id.uploadButton)

        // input
        val inputName = findViewById<TextInputLayout>(R.id.etName)
        val inputDesc = findViewById<TextInputLayout>(R.id.etDesc)

        // handle back navigation
        topBarBackutton.setNavigationOnClickListener {
            super.onBackPressed()
        }

        // Extras
        val existingData = intent.getParcelableExtra<Sample>("EXISTING")


        // check if from detail
        if (existingData != null){
            isUpdate = true
            sample = existingData


            // Input
            inputName.editText?.setText(sample.title)
            inputDesc.editText?.setText(sample.message)

            inputName.editText?.setSelection(sample.title.length)

            // Button
            submitButton.text = "Update"
            deleteButton.visibility = View.VISIBLE
            uploadButton.visibility = View.VISIBLE
        }

        // database
        database = SampleRoomDatabase.getDatabase(applicationContext)
        dao = database.getSampleDao()


        // save button
        submitButton.setOnClickListener {
            val name = inputName.editText?.text.toString()
            val desc = inputDesc.editText?.text.toString()

            if (name.isEmpty() || desc.isEmpty()){
                Toast.makeText(applicationContext, "Form belum lengkap!", Toast.LENGTH_SHORT).show()
            }else{
                if (isUpdate){
                    saveSample(Sample(id = sample.id, title = name, message = desc))
                }else{
                    saveSample(Sample(title = name, message = desc))
                }
            }
        }

        // delete button
        deleteButton.setOnClickListener {
            deleteSample(sample)
            finish()
        }
    }

    private fun saveSample(sample: Sample) {
        if (dao.getById(sample.id).isEmpty()){
            dao.insertSample(sample)
        }else{
            dao.updateSample(sample)
        }

        Toast.makeText(applicationContext, "Sample Saved!", Toast.LENGTH_SHORT)
        finish()
    }

    private fun deleteSample(sample: Sample){
        dao.deleteSample(sample)
        Toast.makeText(applicationContext, "Sample removed", Toast.LENGTH_SHORT).show()
    }
}