package com.ardclient.esikap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardclient.esikap.adapter.COPAdapter
import com.ardclient.esikap.adapter.P3KAdapter
import com.ardclient.esikap.adapter.PHQCAdapter
import com.ardclient.esikap.adapter.SSCECAdapter
import com.ardclient.esikap.database.cop.COPRoomDatabase
import com.ardclient.esikap.database.p3k.P3KRoomDatabase
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.database.sscec.SSCECRoomDatabase
import com.ardclient.esikap.databinding.ActivityDocumentListBinding
import com.ardclient.esikap.input.cop.CopInputActivity
import com.ardclient.esikap.input.p3k.P3KInputActivity
import com.ardclient.esikap.input.phqc.PHQCDocumentDetailActivity
import com.ardclient.esikap.input.phqc.PHQCInputActivity
import com.ardclient.esikap.input.sscec.SSCECInputActivity
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.model.PHQCModel
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.utils.LocaleHelper


class DocumentListActivity : AppCompatActivity() {

    private lateinit var kapal: KapalModel

    // type handler
    private var listType: String? = null

    private lateinit var binding: ActivityDocumentListBinding

    private val PERMISSION_CODE = 1001

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

        // Permission
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    onAddDocument()
                } else {
                    Toast.makeText(this, "Izin akses media tidak diberikan!", Toast.LENGTH_SHORT).show()
                }
            }

        // fab
        binding.fab.setOnClickListener {
//            when {
//                ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.READ_MEDIA_IMAGES
//                ) == PackageManager.PERMISSION_GRANTED -> {
//                    // You can use the API that requires the permission.
//                    onAddDocument()
//                }
//                else -> {
//                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
//                        requestPermissionLauncher.launch(
//                            Manifest.permission.READ_MEDIA_IMAGES)
//                    }else{
//                        ActivityCompat.requestPermissions(
//                            this, arrayOf(
//                                Manifest.permission.READ_EXTERNAL_STORAGE
//                            ),
//                            PERMISSION_CODE
//                        )
//                    }
//                }
//            }
            onAddDocument()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode === PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                onAddDocument()
            }else{
                Toast.makeText(this, getString(R.string.no_media_permission), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onAddDocument(){
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

    private fun initDBbyType() {
        when(listType) {
            "BLUE" -> {
                binding.topAppBar.title = getString(R.string.doc_phqc_title)
                getPHQCDatabase()
            }
            "GREEN" -> {
                binding.topAppBar.title = getString(R.string.doc_cop_title)
                getCOPDatabase()
            }
            "ORANGE" -> {
                binding.topAppBar.title = getString(R.string.doc_sscec_title)
                getSSCECDatabase()
            }
            "AMBER" -> {
                binding.topAppBar.title = getString(R.string.doc_p3k_title)
                getP3KDatabase()
            }
        }
    }

    private fun getP3KDatabase() {
        val database = P3KRoomDatabase.getDatabase(this)
        val dao = database.getP3KDAO()
        val listData = arrayListOf<P3KModel>()

        listData.addAll(dao.getAllP3K(kapal.id, kapal.flag))

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
                intent.putExtra("P3K", p3k)
                intent?.putExtra("KAPAL", kapal)
                startActivity(intent)
            }
        })
    }

    private fun getSSCECDatabase() {
        val database = SSCECRoomDatabase.getDatabase(this)
        val dao = database.getSSCECDao()
        val listData = arrayListOf<SSCECModel>()

        listData.addAll(dao.getAllSSCEC(kapal.id, kapal.flag))

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

        listData.addAll(dao.getAllPHQC(kapal.id, kapal.flag))

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

        listData.addAll(dao.getAllCOP(kapal.id, kapal.flag))

        binding.loadingView.visibility = View.GONE

        if (listData.size < 1){
            binding.noDataText.visibility = View.VISIBLE
        }else{
            binding.noDataText.visibility = View.GONE
        }

        // setupRecyclerView("COP", listData)
        binding.recyclerView.adapter = COPAdapter(listData, object : COPAdapter.COPListner {
            override fun onItemClicked(cop: COPModel) {
                val intent = Intent(this@DocumentListActivity, CopInputActivity::class.java)
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

    override fun attachBaseContext(base: Context?) {
        LocaleHelper().setLocale(base!!, LocaleHelper().getLanguage(base))
        super.attachBaseContext(LocaleHelper().onAttach(base))
    }
}