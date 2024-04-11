package com.ardclient.esikap.input.p3k

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.ardclient.esikap.R
import com.ardclient.esikap.database.p3k.P3KDao
import com.ardclient.esikap.database.p3k.P3KRoomDatabase
import com.ardclient.esikap.databinding.ActivityP3kInputBinding
import com.ardclient.esikap.modal.SpinnerModal
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.model.P3KUpdateStatusModel
import com.ardclient.esikap.model.api.FileModel
import com.ardclient.esikap.model.api.UploadFileModel
import com.ardclient.esikap.model.api.UploadModel
import com.ardclient.esikap.model.reusable.PemeriksaanKapalModel
import com.ardclient.esikap.service.ApiClient
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DialogUtils
import com.ardclient.esikap.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class P3KInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityP3kInputBinding
    private lateinit var kapal: KapalModel
    private var isUpdate: Boolean = false
    private var existingID: Int = 0

    private var launcher: ActivityResultLauncher<Intent>? = null

    // model
    private lateinit var P3KDataUmum: P3KModel
    private lateinit var P3KPemeriksaan: PemeriksaanKapalModel
    private lateinit var P3KRekomendasi: P3KModel
    private lateinit var p3kData: P3KModel

    // DB
    private lateinit var db: P3KRoomDatabase
    private lateinit var dao: P3KDao

    // modal
    private lateinit var spinner: SpinnerModal

    private var isUploaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityP3kInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            DialogUtils.showNotSavedDialog(this@P3KInputActivity, object: DialogUtils.DialogListener {
                override fun onConfirmed() {
                    finish()
                }
            })
        }

        DialogUtils.showNotSavedDialog(this@P3KInputActivity, object: DialogUtils.DialogListener {
            override fun onConfirmed() {
                finish()
            }
        })

        spinner = SpinnerModal()

        // existing data
        val existingData = intent.getParcelableExtra<P3KModel>("P3K")
        if (existingData != null){
            isUpdate = true
            existingID = existingData.id
            P3KDataUmum = existingData
            P3KRekomendasi = existingData
            P3KPemeriksaan = existingData.pemeriksaan
            p3kData = existingData

            // button
            binding.bottomContainerEdit.visibility = View.VISIBLE
            binding.bottomContainerSave.visibility = View.GONE

            // header
            binding.topAppBar.title = "Dokumen P3K"

            // chip
            binding.chipSanitasi.isChecked = true
            binding.chipSanitasi.text = "Lengkap"
            binding.chipDataUmum.isChecked = true
            binding.chipDataUmum.text = "Lengkap"
            binding.chipDokumenKapal.isChecked = true
            binding.chipDokumenKapal.text = "Lengkap"
        }else{
            // init mode
            P3KDataUmum = P3KModel()
            P3KRekomendasi = P3KModel()
            P3KPemeriksaan = PemeriksaanKapalModel()
            p3kData = P3KModel()
        }



        // db
        db = P3KRoomDatabase.getDatabase(this)
        dao = db.getP3KDAO()

        // existing kapal data
        kapal = intent.getParcelableExtra("KAPAL") ?: KapalModel()
        if (p3kData.isUpload){
            isUploaded = true
            updateUIonUploaded()
        }

        // handle input result
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                // Receive Data
                val isUpdate = data?.getBooleanExtra("HAS_UPDATE", false)
                if (isUpdate == true){
                    binding.uploadButton.isEnabled = false
                    binding.tvHasUpdate.visibility = View.VISIBLE
                }

                // == Basic Data
                val basicData = data?.getParcelableExtra<P3KModel>("BASIC")
                if (basicData != null) {
                    P3KDataUmum = basicData
                    binding.chipDataUmum.isChecked = true
                    binding.chipDataUmum.text = "Lengkap"
                }

                // == Doc Data
                val signData = data?.getParcelableExtra<P3KModel>("SIGNATURE")
                if (signData != null) {
                    P3KRekomendasi = signData
                    binding.chipDokumenKapal.isChecked = true
                    binding.chipDokumenKapal.text = "Lengkap"
                }

                // == Sanitasi Data
                val pemeriksaan = data?.getParcelableExtra<PemeriksaanKapalModel>("PEMERIKSAAN")
                if (pemeriksaan != null) {
                    P3KPemeriksaan = pemeriksaan
                    binding.chipSanitasi.isChecked = true
                    binding.chipSanitasi.text = "Lengkap"
                }

            }
        }

        binding.cardDataUmum.setOnClickListener {
            val intent = Intent(this, P3KInputDataUmumActivity::class.java)
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipDataUmum.isChecked) {
                intent.putExtra("EXISTING_DATA", P3KDataUmum)
            }
            launcher?.launch(intent)
        }

        binding.cardSanitasi.setOnClickListener {
            val intent = Intent(this, P3KInputPemeriksaanActivity::class.java)
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipSanitasi.isChecked) {
                intent.putExtra("EXISTING_DATA", P3KPemeriksaan)
            }
            launcher?.launch(intent)
        }

        binding.cardDokumenKapal.setOnClickListener {
            val intent = Intent(this, P3KInputRekomendasiActivity::class.java)
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipDokumenKapal.isChecked) {
                intent.putExtra("EXISTING_DATA", P3KRekomendasi)
            }
            intent.putExtra("KAPTEN", kapal.kaptenKapal)
            launcher?.launch(intent)
        }

        binding.submitButton.setOnClickListener {
            onSaveButton()
        }

        binding.updateButton.setOnClickListener {
            onSaveButton()
        }

        binding.deleteButton.setOnClickListener {
            DialogUtils.showDeleteDialog(this, object: DialogUtils.OnDeleteConfirmListener {
                override fun onDeleteConfirmed() {
                    onDeleteData()
                }
            })
        }

        binding.uploadButton.setOnClickListener {
            DialogUtils.showUploadDialog(this, object : DialogUtils.DialogListener{
                override fun onConfirmed() {
                    onPreUploadButton()
                }
            })
        }
    }

    private fun onPreUploadButton() {
        if (NetworkUtils.isNetworkAvailable(this)){
            onUploadButton()
        }else{
            Toast.makeText(this, "Tidak ada koneksi internet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onUploadButton() {
        spinner.show(supportFragmentManager, "LOADING")

        val masalahKesehatanFile = Base64Utils.uriToBase64(this, p3kData.pemeriksaan.masalahFile.toUri())

        val uploadedFilesList = mutableListOf<FileModel>()

        val uploadDocData = UploadFileModel("masalahkesehatan",
            masalahKesehatanFile, p3kData.id)

        if (masalahKesehatanFile.isNullOrEmpty()){
            val call = ApiClient.apiService.uploadP3KSingle(uploadDocData)

            call.enqueue(object: Callback<ApiResponse<FileModel>>{
                override fun onResponse(
                    call: Call<ApiResponse<FileModel>>,
                    response: Response<ApiResponse<FileModel>>
                ) {
                    if (response.isSuccessful) {
                        val fileModel = response.body()?.data
                        if (fileModel != null) {
                            uploadedFilesList.add(fileModel)
                        }
                        onDocumentUploaded(uploadedFilesList)
                    } else {
                        onErrorUpload()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<FileModel>>, t: Throwable) {
                    onErrorUpload()
                }
            })
        }else{
            onDocumentUploaded(uploadedFilesList)
        }
    }

    private fun onErrorUpload() {
        val call = ApiClient.apiService.uploadP3KDelete(p3kData.id.toString())

        call.enqueue(object : Callback<ApiResponse<Any>>{
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                spinner.dismiss()
                if (response.isSuccessful){
                    Toast.makeText(this@P3KInputActivity, "Upload gagal dan berhasil membersihkan cache!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@P3KInputActivity, "Upload gagal dan gagal membersihkan cache!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                spinner.dismiss()
                Toast.makeText(this@P3KInputActivity, "Upload gagal dan gagal membersihkan cache!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun onDocumentUploaded(uploadedFilesList: MutableList<FileModel>) {
        Log.d("UPLOADED IMAGE", uploadedFilesList.toString())
        val bodyRequest = UploadModel(p3kData, uploadedFilesList)
        val call = ApiClient.apiService.uploadP3K(bodyRequest)

        call.enqueue(object: Callback<ApiResponse<Any>>{
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                spinner.dismiss()
                if (response.isSuccessful){
                    onUploadSuccess()
                    //Toast.makeText(this@CopInputActivity, "SUKSES", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@P3KInputActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                spinner.dismiss()
                Toast.makeText(this@P3KInputActivity, "Ada sesuatu yang tidak beres, mohon coba lagi!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun onUploadSuccess() {
        val updatedData = P3KUpdateStatusModel(id = p3kData.id, isUpload = true)
        dao.updateP3KStatus(updatedData)
        isUploaded = true
        Toast.makeText(this@P3KInputActivity, "Berhasil Upload", Toast.LENGTH_SHORT).show()
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

    private fun onSaveButton() {
        if (binding.chipDataUmum.isChecked && binding.chipDokumenKapal.isChecked && binding.chipSanitasi.isChecked){
            val data = if (isUpdate) {
                P3KModel(
                    id = existingID,
                    kapal = kapal,
                    kapalId = kapal.id,
                    pemeriksaan = P3KPemeriksaan,
                    jenisLayanan = P3KDataUmum.jenisLayanan,
                    jenisPelayanan = P3KDataUmum.jenisPelayanan,
                    lokasiPemeriksaan = P3KDataUmum.lokasiPemeriksaan,
                    tglDiperiksa = P3KDataUmum.tglDiperiksa,
                    jmlABK = P3KDataUmum.jmlABK,
                    abkSehat = P3KDataUmum.abkSehat,
                    abkSakit = P3KDataUmum.abkSakit,
                    recP3K = P3KRekomendasi.recP3K,
                    recTanggal = P3KRekomendasi.recTanggal,
                    recJam = P3KRekomendasi.recJam,
                    signKapten = P3KRekomendasi.signKapten,
                    signPetugas = P3KRekomendasi.signPetugas,
                    signNamaKapten = P3KRekomendasi.signNamaKapten,
                    signNamaPetugas = P3KRekomendasi.signNamaPetugas
                )
            } else {
                P3KModel(
                    kapal = kapal,
                    kapalId = kapal.id,
                    pemeriksaan = P3KPemeriksaan,
                    jenisLayanan = P3KDataUmum.jenisLayanan,
                    jenisPelayanan = P3KDataUmum.jenisPelayanan,
                    lokasiPemeriksaan = P3KDataUmum.lokasiPemeriksaan,
                    tglDiperiksa = P3KDataUmum.tglDiperiksa,
                    jmlABK = P3KDataUmum.jmlABK,
                    abkSehat = P3KDataUmum.abkSehat,
                    abkSakit = P3KDataUmum.abkSakit,
                    recP3K = P3KRekomendasi.recP3K,
                    recTanggal = P3KRekomendasi.recTanggal,
                    recJam = P3KRekomendasi.recJam,
                    signKapten = P3KRekomendasi.signKapten,
                    signPetugas = P3KRekomendasi.signPetugas,
                    signNamaKapten = P3KRekomendasi.signNamaKapten,
                    signNamaPetugas = P3KRekomendasi.signNamaPetugas
                )
            }

            onSubmitData(data)
        }else{
            Toast.makeText(this@P3KInputActivity, "Data belum lengkap!", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun onSubmitData(data: P3KModel) {
        if (dao.getP3KById(data.id).isEmpty()){
            dao.createP3K(data)
            Toast.makeText(this, "Dokumen berhasil dibuat!", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            dao.updateP3K(data)
            Toast.makeText(this, "Dokumen berhasil diupdate!", Toast.LENGTH_SHORT).show()
            // reset has update
            binding.uploadButton.isEnabled = true
            binding.tvHasUpdate.visibility = View.GONE
            p3kData = data
        }
    }

    private fun onDeleteData() {
        dao.deleteP3K(P3KDataUmum)
        Toast.makeText(this, "Dokumen berhasil dihapus!", Toast.LENGTH_SHORT).show()
        finish()
    }
}