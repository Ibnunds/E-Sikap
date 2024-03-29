package com.ardclient.esikap.input.cop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ardclient.esikap.databinding.ActivityCopInputSignatureBinding

class CopInputSignatureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCopInputSignatureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputSignatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}