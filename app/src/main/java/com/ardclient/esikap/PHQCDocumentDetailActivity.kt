package com.ardclient.esikap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import com.ardclient.esikap.database.phqc.PHQCDao
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.databinding.ActivityPhqcDocumentDetailBinding
import com.ardclient.esikap.model.PHQCModel
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DialogUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PHQCDocumentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhqcDocumentDetailBinding
    private lateinit var phqc: PHQCModel
    private lateinit var db: PHQCRoomDatabase
    private lateinit var dao: PHQCDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhqcDocumentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // init db
        db = PHQCRoomDatabase.getDatabase(this)
        dao = db.getPHQCDao()

        // handle selected data
        val existingData = intent.getParcelableExtra<PHQCModel>("PHQC")
        if (existingData != null){
            phqc = existingData
        }

        // handle detail view
        initDetail()


        // == Button Listener
        binding.updateButton.setOnClickListener {
            val intent = Intent(this, PHQCInputActivity::class.java)
            intent.putExtra("PHQC", phqc)
            startActivity(intent)
        }

        binding.deleteButton.setOnClickListener {
            DialogUtils.showDeleteDialog(this, object : DialogUtils.OnDeleteConfirmListener {
                override fun onDeleteConfirmed() {
                    deleteDokumen()
                }
            })
        }
    }

    private fun deleteDokumen() {
        dao.deletePHQC(phqc)
        Toast.makeText(this, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun initDetail() {
        binding.tvNamaKapal.text = phqc.kapal.namaKapal
        binding.tvDetailGrossTone.text = phqc.kapal.grossTone
        binding.tvDetailBendera.text = phqc.kapal.bendera
        binding.tvAgen.text = phqc.kapal.namaAgen
        binding.tvAsal.text = phqc.kapal.negaraAsal
        binding.tvTujuan.text = phqc.tujuan
        binding.tvDokumen.text = phqc.dokumenKapal
        binding.tvPemeriksaan.text = phqc.lokasiPemeriksaan
        binding.tvJmlABK.text = phqc.jumlahABK.toString()
        binding.tvDemam.text = phqc.deteksiDemam.toString()
        binding.tvSehat.text = phqc.jumlahSehat.toString()
        binding.tvSakit.text = phqc.jumlahSakit.toString()
        binding.tvMeninggal.text = phqc.jumlahMeninggal.toString()
        binding.tvDirujuk.text = phqc.jumlahDirujuk.toString()
        binding.tvSanitasi.text = phqc.statusSanitasi
        binding.tvKesimpulan.text = phqc.kesimpulan
        binding.tvPetugas.text = phqc.petugasPelaksana

        // signature
        val bitmapSign = Base64Utils.convertBase64ToBitmap(phqc.signature)

        binding.ivSignature.setImageBitmap(bitmapSign)
    }

    override fun onResume() {
        super.onResume()
        getUpdatedData()
    }

    private fun getUpdatedData() {
        val updatedData = dao.getPHQCById(phqc.id)
        phqc = updatedData[0]
        initDetail()
    }
}