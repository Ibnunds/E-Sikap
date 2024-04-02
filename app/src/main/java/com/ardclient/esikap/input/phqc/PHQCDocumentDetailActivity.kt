package com.ardclient.esikap.input.phqc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ardclient.esikap.R
import com.ardclient.esikap.database.phqc.PHQCDao
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.databinding.ActivityPhqcDocumentDetailBinding
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.PHQCModel
import com.ardclient.esikap.model.PHQCStatusUpdateModel
import com.ardclient.esikap.model.api.UploadModel
import com.ardclient.esikap.service.ApiClient
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DialogUtils
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PHQCDocumentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhqcDocumentDetailBinding
    private lateinit var phqc: PHQCModel
    private lateinit var db: PHQCRoomDatabase
    private lateinit var dao: PHQCDao

    private var isUploaded = false
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

        if (phqc.isUpload){
            isUploaded = true
            updateUIonUploaded()
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

        binding.uploadButton.setOnClickListener {
            //onUploadButton()
        }


    }

    private fun onUploadButton() {
        val bodyRequest = UploadModel(phqc)
        val call = ApiClient.apiService.uploadPHQC(bodyRequest)

        call.enqueue(object : Callback<ApiResponse<Any>>{
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                if (response.isSuccessful){
                    onUploadSuccess()
                }else{
                    Toast.makeText(this@PHQCDocumentDetailActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                Toast.makeText(this@PHQCDocumentDetailActivity, "Ada sesuatu yang tidak beres, mohon coba lagi!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun onUploadSuccess() {
        val updatedData = PHQCStatusUpdateModel(id = phqc.id, isUpload = true)
        dao.updateStatusPHQC(updatedData)
        isUploaded = true
        Toast.makeText(this@PHQCDocumentDetailActivity, "Berhasil Upload", Toast.LENGTH_SHORT).show()
        updateUIonUploaded()
    }

    private fun updateUIonUploaded() {
        binding.deleteButton.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.uploadButton.text = "Sudah Diupload"
        binding.uploadButton.setBackgroundColor(getColor(R.color.gray))
        binding.uploadButton.setTextColor(getColor(R.color.black))
        binding.uploadButton.isEnabled = false
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
        binding.tvJmlPenumpang.text = phqc.jumlahPenumpang.toString()
        binding.tvCustDemam.text = phqc.custDeteksiDemam.toString()
        binding.tvCustSehat.text = phqc.custJumlahSehat.toString()
        binding.tvCustSakit.text = phqc.custJumlahSakit.toString()
        binding.tvCustMeninggal.text = phqc.custJumlahMeninggal.toString()
        binding.tvCustDirujuk.text = phqc.custJumlahDirujuk.toString()
        binding.tvSanitasi.text = phqc.statusSanitasi
        binding.tvKesimpulan.text = phqc.kesimpulan
        binding.tvPetugas.text = phqc.petugasPelaksana
        binding.tvTanggal.text = phqc.tanggalDiperiksa
        Picasso.get().load(phqc.pemeriksaanFile).fit().into(binding.prevHasil)

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