package com.ardclient.esikap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardclient.esikap.adapter.COPAdapter
import com.ardclient.esikap.adapter.P3KAdapter
import com.ardclient.esikap.adapter.PHQCAdapter
import com.ardclient.esikap.adapter.SSCECAdapter
import com.ardclient.esikap.cop.CopInputActivity
import com.ardclient.esikap.database.cop.COPRoomDatabase
import com.ardclient.esikap.database.p3k.P3KRoomDatabase
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.database.sscec.SSCECRoomDatabase
import com.ardclient.esikap.databinding.ActivityDocumentListBinding
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.model.PHQCModel
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.p3k.P3KInputActivity
import com.ardclient.esikap.sscec.SSCECInputActivity

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
                "ORANGE" -> Intent(this, SSCECInputActivity::class.java)
                "AMBER" -> Intent(this, P3KInputActivity::class.java)
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
            "ORANGE" -> {
                binding.topAppBar.title = "Dokumen SSCEC"
                getSSCECDatabase()
            }
            "AMBER" -> {
                binding.topAppBar.title = "Dokumen P3K"
                getP3KDatabase()
            }
        }
    }

    private fun getP3KDatabase() {
        val database = P3KRoomDatabase.getDatabase(this)
        val dao = database.getP3KDAO()
        val listData = arrayListOf<P3KModel>()

        listData.addAll(dao.getAllP3K(kapal.id))

        binding.loadingView.visibility = View.GONE

        if (listData.size < 1){
            binding.noDataText.visibility = View.VISIBLE
        }else{
            binding.noDataText.visibility = View.GONE
        }

        // setupRecyclerView("PHQC", listData)
        binding.recyclerView.adapter = P3KAdapter(listData, object : P3KAdapter.P3KListener {
            override fun onItemClicked(p3k: P3KModel) {
                val intent = Intent(this@DocumentListActivity, P3KInputActivity::class.java)
                intent.putExtra("SSCEC", p3k)
                intent?.putExtra("KAPAL", kapal)
                startActivity(intent)
            }
        })
    }

    private fun getSSCECDatabase() {
        val database = SSCECRoomDatabase.getDatabase(this)
        val dao = database.getSSCECDao()
        val listData = arrayListOf<SSCECModel>()

        listData.addAll(dao.getAllSSCEC(kapal.id))

        binding.loadingView.visibility = View.GONE

        if (listData.size < 1){
            binding.noDataText.visibility = View.VISIBLE
        }else{
            binding.noDataText.visibility = View.GONE
        }

        // setupRecyclerView("PHQC", listData)
        binding.recyclerView.adapter = SSCECAdapter(listData, object : SSCECAdapter.SSCEClistener {
            override fun onItemClicked(sscec: SSCECModel) {
                val intent = Intent(this@DocumentListActivity, SSCECInputActivity::class.java)
                intent.putExtra("SSCEC", sscec)
                intent?.putExtra("KAPAL", kapal)
                startActivity(intent)
            }
        })
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
                intent?.putExtra("KAPAL", kapal)
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
                intent?.putExtra("KAPAL", kapal)
                startActivity(intent)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        initDBbyType()
    }
}