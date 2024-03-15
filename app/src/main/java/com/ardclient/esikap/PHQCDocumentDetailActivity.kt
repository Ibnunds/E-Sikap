package com.ardclient.esikap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ardclient.esikap.database.phqc.PHQCDao
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.databinding.ActivityPhqcDocumentDetailBinding
import com.ardclient.esikap.model.PHQCModel
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

        // handle selected data
        val existingData = intent.getParcelableExtra<PHQCModel>("PHQC")
        if (existingData != null){
            phqc = existingData
        }

        // init db
        db = PHQCRoomDatabase.getDatabase(this)
        dao = db.getPHQCDao()

        // handle detail view
        initDetail()

        binding.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Hapus Data")
                .setMessage("Apakah anda yakin ingin menghapus data ini?")
                .setNegativeButton("Batalkan") {dialog, which -> dialog.dismiss()}
                .setPositiveButton("Konfirmasi") {dialog, which -> deleteDokumen() }
                .show()
        }
    }

    private fun deleteDokumen() {
        dao.deletePHQC(phqc)
        Toast.makeText(this, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun initDetail() {
        binding.tvNamaKapal.text = phqc.namaKapal
        binding.tvDetailGrossTone.text = phqc.grosTone
        binding.tvDetailBendera.text = phqc.bendera
        binding.tvAgen.text = phqc.namaAgen
        binding.tvAsal.text = phqc.negaraAsal
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
    }
}