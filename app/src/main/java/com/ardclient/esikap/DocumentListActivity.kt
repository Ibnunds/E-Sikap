package com.ardclient.esikap

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DocumentListActivity : AppCompatActivity() {
    private lateinit var tvNoData: TextView
    private lateinit var loadingBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_list)

        // init
        val header = findViewById<MaterialToolbar>(R.id.topAppBar)
        loadingBar = findViewById(R.id.loading_view)
        tvNoData = findViewById(R.id.no_data_text)
        recyclerView = findViewById(R.id.recycler_view)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        // rv
        recyclerView.layoutManager = LinearLayoutManager(this)

        // header
        header.setNavigationOnClickListener{
            finish()
        }

        // fab
        fab.setOnClickListener {
            val intent = Intent(this, DocumentInputActivity::class.java)
            startActivity(intent)
        }
    }
}