package com.ardclient.esikap.cop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.ActivityCopInputDokumenBinding
import com.ardclient.esikap.modal.ImageSelectorModal

class CopInputDokumenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCopInputDokumenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputDokumenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // dialog
        val imageSelectorDialog = ImageSelectorModal()

        // button
        binding.btnUploadMDH.setOnClickListener {
            imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
        }
    }
}