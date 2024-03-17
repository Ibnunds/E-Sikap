package com.ardclient.esikap

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardclient.esikap.adapter.COPAdapter
import com.ardclient.esikap.adapter.PHQCAdapter
import com.ardclient.esikap.database.cop.COPRoomDatabase
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.databinding.ActivityDocumentListBinding
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.PHQCModel

class DocumentListActivity : AppCompatActivity() {

    private lateinit var kapal: KapalModel

    // type handler
    private var listType: String? = null

    private lateinit var binding: ActivityDocumentListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // rv
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // get type
        listType = intent.getStringExtra("TYPE")

        // existing kapal data
        val kapalData = intent.getParcelableExtra<KapalModel>("KAPAL")

        if (kapalData != null){
            kapal = kapalData
        }

        // get db by type
        initDBbyType()

        // header
        binding.topAppBar.setNavigationOnClickListener{
            finish()
        }

        // fab
        binding.fab.setOnClickListener {
            val intent = when(listType) {
                "BLUE" -> Intent(this, PHQCInputActivity::class.java)
                "GREEN" -> Intent(this, CopInputActivity::class.java)
                else -> null // Mungkin perlu penanganan lebih lanjut tergantung dari kasus Anda
            }
            intent?.putExtra("KAPAL", kapal)
            intent?.let { startActivity(it) }
        }
    }

    private fun initDBbyType() {
        when(listType) {
            "BLUE" -> {
                binding.topAppBar.title = "Dokumen PHQC"
                getPHQCDatabase()
            }
            "GREEN" -> {
                binding.topAppBar.title = "Dokumen COP"
                getCOPDatabase()
            }
        }
    }

    private fun getPHQCDatabase() {
        val database = PHQCRoomDatabase.getDatabase(this)
        val dao = database.getPHQCDao()
        val listData = arrayListOf<PHQCModel>()

        listData.addAll(dao.getAllPHQC(kapal.id))

        binding.loadingView.visibility = View.GONE

        if (listData.size < 1){
            binding.noDataText.visibility = View.VISIBLE
        }else{
            binding.noDataText.visibility = View.GONE
        }

        // setupRecyclerView("PHQC", listData)
        binding.recyclerView.adapter = PHQCAdapter(listData, object : PHQCAdapter.PHQCListner {
            override fun onItemClicked(phqc: PHQCModel) {
                val intent = Intent(this@DocumentListActivity, PHQCDocumentDetailActivity::class.java)
                intent.putExtra("PHQC", phqc)
                startActivity(intent)
            }
        })

    }

    private fun getCOPDatabase() {
        val database = COPRoomDatabase.getDatabase(this)
        val dao = database.getCOPDao()
        val listData = arrayListOf<COPModel>()

        listData.addAll(dao.getAllCOP(kapal.id))

        binding.loadingView.visibility = View.GONE

        if (listData.size < 1){
            binding.noDataText.visibility = View.VISIBLE
        }else{
            binding.noDataText.visibility = View.GONE
        }

        // setupRecyclerView("COP", listData)
        binding.recyclerView.adapter = COPAdapter(listData, object : COPAdapter.COPListner {
            override fun onItemClicked(cop: COPModel) {
                val intent = Intent(this@DocumentListActivity, PHQCDocumentDetailActivity::class.java)
                intent.putExtra("COP", cop)
                startActivity(intent)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        initDBbyType()
    }
}