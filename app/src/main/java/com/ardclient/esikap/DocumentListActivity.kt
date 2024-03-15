package com.ardclient.esikap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.adapter.PHQCAdapter
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.PHQCModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DocumentListActivity : AppCompatActivity() {
    private lateinit var tvNoData: TextView
    private lateinit var loadingBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var kapal: KapalModel

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

        // existing kapal data
        val kapalData = intent.getParcelableExtra<KapalModel>("KAPAL")

        if (kapalData != null){
            kapal = kapalData
        }

        // get PHQC DB
        getPHQCDatabase()

        // header
        header.setNavigationOnClickListener{
            finish()
        }

        // fab
        fab.setOnClickListener {
            val intent = Intent(this, PHQCInputActivity::class.java)
            intent.putExtra("KAPAL", kapal)
            startActivity(intent)
        }
    }

    private fun getPHQCDatabase() {
        val database = PHQCRoomDatabase.getDatabase(this)
        val dao = database.getPHQCDao()
        val listData = arrayListOf<PHQCModel>()

        listData.addAll(dao.getAllPHQC())

        loadingBar.visibility = View.GONE

        if (listData.size < 1){
            tvNoData.visibility = View.VISIBLE
        }else{
            tvNoData.visibility = View.GONE
        }

        setupRecyclerView("PHQC", listData)

    }

    private fun setupRecyclerView(type: String, listData: ArrayList<PHQCModel>) {
        when(type){
            "PHQC" -> {
                recyclerView.adapter = PHQCAdapter(listData, object : PHQCAdapter.PHQCListner {
                    override fun onItemClicked(phqc: PHQCModel) {
                        val intent = Intent(this@DocumentListActivity, PHQCDocumentDetailActivity::class.java)
                        intent.putExtra("PHQC", phqc)
                        startActivity(intent)
                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getPHQCDatabase()
    }
}