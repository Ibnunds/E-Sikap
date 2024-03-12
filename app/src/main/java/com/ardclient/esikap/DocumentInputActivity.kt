package com.ardclient.esikap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ardclient.esikap.fragment.input.phqc.PHQCFragment
import com.google.android.material.appbar.MaterialToolbar

class DocumentInputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_input)

        // init
        val header = findViewById<MaterialToolbar>(R.id.topAppBar)

        // fragment
        val blue1Fragment = PHQCFragment()

        // header
        header.setNavigationOnClickListener {
            finish()
        }

        // init fragment
        setCurrentFragment(blue1Fragment)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.inputFragment, fragment).commit()
    }
}