package com.ardclient.esikap.input.cop

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
import com.ardclient.esikap.input.SanitasiInputActivity
import com.ardclient.esikap.database.cop.COPDao
import com.ardclient.esikap.database.cop.COPRoomDatabase
import com.ardclient.esikap.databinding.ActivityCopInputBinding
import com.ardclient.esikap.modal.SpinnerModal
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.model.COPUpdateStatusModel
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.api.FileModel
import com.ardclient.esikap.model.api.UploadFileModel
import com.ardclient.esikap.model.api.UploadModel
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.model.reusable.SanitasiModel
import com.ardclient.esikap.service.ApiClient
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DialogUtils
import com.ardclient.esikap.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CopInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCopInputBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private lateinit var kapal: KapalModel
    private var isUpdate: Boolean = false
    private var existingID: Int = 0

    // result data
    private lateinit var copBasicData: COPModel
    private lateinit var copDocData: DokumenKapalModel
    private lateinit var copSanitasi: SanitasiModel
    private lateinit var copSignature: COPModel

    // db
    private lateinit var db: COPRoomDatabase
    private lateinit var dao: COPDao

    // modal
    private lateinit var spinner: SpinnerModal
    private var isUploaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }


        // modal
        spinner = SpinnerModal()

        // db
        db = COPRoomDatabase.getDatabase(this)
        dao = db.getCOPDao()

        // existing data
        val existingData = intent.getParcelableExtra<COPModel>("COP")
        if (existingData != null){
            isUpdate = true
            existingID = existingData.id
            copBasicData = existingData
            copSanitasi = existingData.sanitasiKapal
            copDocData = existingData.dokumenKapal
            copSignature = existingData

            // button
            binding.bottomContainerEdit.visibility = View.VISIBLE
            binding.bottomContainerSave.visibility = View.GONE

            // header
            binding.topAppBar.title = "Dokumen COP"

            // chip
            binding.chipCOPSanitasi.isChecked = true
            binding.chipCOPSanitasi.text = "Lengkap"
            binding.chipCOPDataUmum.isChecked = true
            binding.chipCOPDataUmum.text = "Lengkap"
            binding.chipCOPDokumenKapal.isChecked = true
            binding.chipCOPDokumenKapal.text = "Lengkap"
            binding.chipCOPRekomendasi.isChecked = true
            binding.chipCOPRekomendasi.text = "Lengkap"
        }else{
            // init value
            copBasicData = COPModel()
            copDocData = DokumenKapalModel()
            copSanitasi = SanitasiModel()
            copSignature = COPModel()
        }

        // existing kapal data
        kapal = intent.getParcelableExtra("KAPAL") ?: KapalModel()

        if (copBasicData.isUpload){
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
                val basicCOPData = data?.getParcelableExtra<COPModel>("COP_BASIC")
                if (basicCOPData !=null){
                    copBasicData = basicCOPData
                    binding.chipCOPDataUmum.isChecked = true
                    binding.chipCOPDataUmum.text = "Lengkap"
                }

                // == Doc Data
                val docCOPdata = data?.getParcelableExtra<DokumenKapalModel>("COP_DOC")
                if (docCOPdata != null){
                    copDocData = docCOPdata
                    binding.chipCOPDokumenKapal.isChecked = true
                    binding.chipCOPDokumenKapal.text = "Lengkap"
                }

                // == Sanitasi Data
                val sanitasi = data?.getParcelableExtra<SanitasiModel>("SANITASI")
                if (sanitasi !=null){
                    copSanitasi = sanitasi
                    binding.chipCOPSanitasi.isChecked = true
                    binding.chipCOPSanitasi.text = "Lengkap"
                }

                // == Signature Data
                val signature = data?.getParcelableExtra<COPModel>("SIGNATURE")
                if (signature !=null){
                    copSignature = signature
                    binding.chipCOPRekomendasi.isChecked = true
                    binding.chipCOPRekomendasi.text = "Lengkap"
                }

            }
        }

        // card button
        binding.cardCOPDataUmum.setOnClickListener {
            val intent = Intent(this, CopInputDataUmumActivity::class.java)
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipCOPDataUmum.isChecked){
                intent.putExtra("EXISTING_DATA", copBasicData)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPDokumenKapal.setOnClickListener {
            val intent = Intent(this, CopInputDokumenActivity::class.java)
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipCOPDokumenKapal.isChecked){
                intent.putExtra("EXISTING_DATA", copDocData)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPSanitasi.setOnClickListener {
            val intent = Intent(this, SanitasiInputActivity::class.java)
            intent.putExtra("SENDER", "COP")
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipCOPSanitasi.isChecked){
                intent.putExtra("EXISTING_DATA", copSanitasi)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPRekomendasi.setOnClickListener {
            val intent = Intent(this, CopInputSignatureActivity::class.java)
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipCOPRekomendasi.isChecked){
                intent.putExtra("EXISTING_DATA", copSignature)
            }
            launcher?.launch(intent)
        }

        binding.submitButton.setOnClickListener {
            onSaveButton()
        }

        binding.updateButton.setOnClickListener {
            onSaveButton()
        }

        binding.deleteButton.setOnClickListener {
            DialogUtils.showDeleteDialog(this, object : DialogUtils.OnDeleteConfirmListener {
                override fun onDeleteConfirmed() {
                    onDeleteData()
                }
            })
        }

        binding.uploadButton.setOnClickListener {
            DialogUtils.showUploadDialog(this@CopInputActivity, object : DialogUtils.DialogListener{
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
        // File
        val fileMDH = Base64Utils.uriToBase64(this, copDocData.mdhDoc.toUri())
        val fileSSCEC = Base64Utils.uriToBase64(this, copDocData.sscecDoc.toUri())
        val fileP3K = Base64Utils.uriToBase64(this, copDocData.p3kDoc.toUri())
        val fileBukuKesehatan = Base64Utils.uriToBase64(this, copDocData.bukuKesehatanDoc.toUri())
        val fileBukuVaksin = Base64Utils.uriToBase64(this, copDocData.bukuVaksinDoc.toUri())
        val fileDaftarABK = Base64Utils.uriToBase64(this, copDocData.daftarABKDoc.toUri())
        val fileDaftarVaksin = Base64Utils.uriToBase64(this, copDocData.daftarVaksinasiDoc.toUri())
        val fileDaftarObat = Base64Utils.uriToBase64(this, copDocData.daftarObatDoc.toUri())
        val fileDaftarNarkotik = Base64Utils.uriToBase64(this, copDocData.daftarNarkotikDoc.toUri())
        val fileLPOC = Base64Utils.uriToBase64(this, copDocData.lpocDoc.toUri())
        val fileShipParticular = Base64Utils.uriToBase64(this, copDocData.shipParticularDoc.toUri())
        val fileLPC = Base64Utils.uriToBase64(this, copDocData.lpcDoc.toUri())
        val fileHasilPemeriksaan = Base64Utils.uriToBase64(this, copSanitasi.pemeriksanDoc.toUri())
        val filePenerbitanDokumen = Base64Utils.uriToBase64(this, copSignature.docFile.toUri())
        val fileMasalahKesehatan = Base64Utils.uriToBase64(this, copSanitasi.masalahKesehatanFile.toUri())

        val fileList = listOf(
            UploadFileModel("mdh", fileMDH!!, copBasicData.id),
            UploadFileModel("sscec", fileSSCEC!!, copBasicData.id),
            UploadFileModel("p3k", fileP3K!!, copBasicData.id),
            UploadFileModel("bukukesehatan", fileBukuKesehatan!!, copBasicData.id),
            UploadFileModel("bukuvaksin", fileBukuVaksin!!, copBasicData.id),
            UploadFileModel("daftarabk", fileDaftarABK!!, copBasicData.id),
            UploadFileModel("daftarvaksin", fileDaftarVaksin!!, copBasicData.id),
            UploadFileModel("daftarobat", fileDaftarObat!!, copBasicData.id),
            UploadFileModel("daftarnarkotik", fileDaftarNarkotik!!, copBasicData.id),
            UploadFileModel("lpoc", fileLPOC!!, copBasicData.id),
            UploadFileModel("shipparticular", fileShipParticular!!, copBasicData.id),
            UploadFileModel("lpc", fileLPC!!, copBasicData.id),
            UploadFileModel("hasilpemeriksaan", fileHasilPemeriksaan!!, copBasicData.id),
            UploadFileModel("penerbitandokumen", filePenerbitanDokumen!!, copBasicData.id),
            UploadFileModel("masalahkesehatan", fileMasalahKesehatan!!, copBasicData.id)
        )

        // Handle result
        val uploadedFilesList = mutableListOf<FileModel>()
        var successCount = 0
        var errorCount = 0


        // Handle upload
        for (file in fileList){
            if (errorCount == 0){
                if (file.image != null) {
                    val call = ApiClient.apiService.uploadCOPSingle(file)

                    call.enqueue(object: Callback<ApiResponse<FileModel>>{
                        override fun onResponse(
                            call: Call<ApiResponse<FileModel>>,
                            response: Response<ApiResponse<FileModel>>
                        ) {
                            if (response.isSuccessful) {
                                val fileModel = response.body()?.data
                                if (fileModel != null) {
                                    uploadedFilesList.add(fileModel)
                                    successCount++
                                }
                            } else {
                                errorCount++
                            }

                            // Cek apakah semua file sudah diunggah
                            if (successCount + errorCount == fileList.size) {
                                onUploadDokSuccess(uploadedFilesList)
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse<FileModel>>, t: Throwable) {
                            errorCount++
                        }

                    })
                }else{
                    continue
                }
            }else{
                onClearUploadedDoc()
            }
        }
    }

    private fun onUploadDokSuccess(uploadedFilesList: MutableList<FileModel>) {
        val bodyRequest = UploadModel(copBasicData, uploadedFilesList)
        val call = ApiClient.apiService.uploadCOP(bodyRequest)

        call.enqueue(object : Callback<ApiResponse<Any>>{
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                spinner.dismiss()
                if (response.isSuccessful){
                    spinner.dismiss()
                    onUploadSuccess()
                    //Toast.makeText(this@CopInputActivity, "SUKSES", Toast.LENGTH_SHORT).show()
                }else{
                    spinner.dismiss()
                    Toast.makeText(this@CopInputActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                spinner.dismiss()
                Toast.makeText(this@CopInputActivity, "Ada sesuatu yang tidak beres, mohon coba lagi!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun onClearUploadedDoc() {
        val call = ApiClient.apiService.uploadCOPDelete(copBasicData.id.toString())

        call.enqueue(object : Callback<ApiResponse<Any>>{
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                spinner.dismiss()
                if (response.isSuccessful){
                    Toast.makeText(this@CopInputActivity, "Berhasil membersihkan cache!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@CopInputActivity, "Gagal membersihkan cache!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                spinner.dismiss()
                Toast.makeText(this@CopInputActivity, "Berhasil membersihkan cache!", Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun onUploadSuccess() {
        val updatedData = COPUpdateStatusModel(id = copBasicData.id, isUpload = true)
        dao.updateCOPStatus(updatedData)
        isUploaded = true
        Toast.makeText(this@CopInputActivity, "Berhasil Upload", Toast.LENGTH_SHORT).show()
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
        if (binding.chipCOPSanitasi.isChecked && binding.chipCOPDataUmum.isChecked && binding.chipCOPDokumenKapal.isChecked){
            val data = if (isUpdate) {
                COPModel(
                    id = existingID,
                    kapal = kapal,
                    kapalId = kapal.id,
                    tujuan = copBasicData.tujuan,
                    tglTiba = copBasicData.tglTiba,
                    lokasiSandar = copBasicData.lokasiSandar,
                    jumlahABKAsing = copBasicData.jumlahABKAsing,
                    asingSakit = copBasicData.asingSakit,
                    asingSehat = copBasicData.asingSehat,
                    jumlahABKWNI = copBasicData.jumlahABKWNI,
                    wniSakit = copBasicData.wniSakit,
                    wniSehat = copBasicData.wniSehat,
                    jumlahPenumpangWNI = copBasicData.jumlahPenumpangWNI,
                    penumpangSakit = copBasicData.penumpangSakit,
                    penumpangSehat = copBasicData.penumpangSehat,
                    jumlahPenumpangAsing = copBasicData.jumlahPenumpangAsing,
                    penumpangAsingSakit = copBasicData.penumpangAsingSakit,
                    penumpangAsingSehat = copBasicData.penumpangAsingSehat,
                    dokumenKapal = copDocData,
                    sanitasiKapal = copSanitasi,
                    obatP3K = copSignature.obatP3K,
                    pelanggaranKarantina = copSignature.pelanggaranKarantina,
                    dokumenKesehatanKapal = copSignature.dokumenKesehatanKapal,
                    signNamaPetugas = copSignature.signNamaPetugas,
                    signNamaKapten = copSignature.signNamaKapten,
                    signPetugas = copSignature.signPetugas,
                    signKapten = copSignature.signKapten,
                    docFile = copSignature.docFile,
                    docJam = copSignature.docJam,
                    docTanggal = copSignature.docTanggal,
                    docType = copSignature.docType
                )
            } else {
                COPModel(
                    kapal = kapal,
                    kapalId = kapal.id,
                    tujuan = copBasicData.tujuan,
                    tglTiba = copBasicData.tglTiba,
                    lokasiSandar = copBasicData.lokasiSandar,
                    jumlahABKAsing = copBasicData.jumlahABKAsing,
                    asingSakit = copBasicData.asingSakit,
                    asingSehat = copBasicData.asingSehat,
                    jumlahABKWNI = copBasicData.jumlahABKWNI,
                    wniSakit = copBasicData.wniSakit,
                    wniSehat = copBasicData.wniSehat,
                    jumlahPenumpangWNI = copBasicData.jumlahPenumpangWNI,
                    penumpangSakit = copBasicData.penumpangSakit,
                    penumpangSehat = copBasicData.penumpangSehat,
                    jumlahPenumpangAsing = copBasicData.jumlahPenumpangAsing,
                    penumpangAsingSakit = copBasicData.penumpangAsingSakit,
                    penumpangAsingSehat = copBasicData.penumpangAsingSehat,
                    dokumenKapal = copDocData,
                    sanitasiKapal = copSanitasi,
                    obatP3K = copSignature.obatP3K,
                    pelanggaranKarantina = copSignature.pelanggaranKarantina,
                    dokumenKesehatanKapal = copSignature.dokumenKesehatanKapal,
                    signNamaPetugas = copSignature.signNamaPetugas,
                    signNamaKapten = copSignature.signNamaKapten,
                    signPetugas = copSignature.signPetugas,
                    signKapten = copSignature.signKapten,
                    docFile = copSignature.docFile,
                    docJam = copSignature.docJam,
                    docTanggal = copSignature.docTanggal,
                    docType = copSignature.docType
                )
            }

            onSubmitData(data)
        }else{
            Toast.makeText(this@CopInputActivity, "Data belum lengkap!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onDeleteData() {
        dao.deleteCOP(copBasicData)
        Toast.makeText(this, "Dokumen berhasil dihapus!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun onSubmitData(data: COPModel) {
        if (dao.getCOPById(data.id).isEmpty()){
            dao.createCOP(data)
            Toast.makeText(this, "Dokumen berhasil dibuat!", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            dao.updateCOP(data)
            Toast.makeText(this, "Dokumen berhasil diupdate!", Toast.LENGTH_SHORT).show()
            // reset has update
            binding.uploadButton.isEnabled = true
            binding.tvHasUpdate.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("DOK_KAPAL", copDocData.toString())
    }
}