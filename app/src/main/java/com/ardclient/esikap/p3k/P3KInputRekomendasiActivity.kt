package com.ardclient.esikap.p3k

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ardclient.esikap.R
import com.ardclient.esikap.databinding.ActivityP3kInputRekomendasiBinding

class P3KInputRekomendasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityP3kInputRekomendasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityP3kInputRekomendasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}