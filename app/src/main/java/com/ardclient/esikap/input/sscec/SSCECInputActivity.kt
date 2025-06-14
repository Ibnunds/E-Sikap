package com.ardclient.esikap.input.sscec

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.ardclient.esikap.R
import com.ardclient.esikap.input.SanitasiInputActivity
import com.ardclient.esikap.database.sscec.SSCECDao
import com.ardclient.esikap.database.sscec.SSCECRoomDatabase
import com.ardclient.esikap.databinding.ActivitySscecInputBinding
import com.ardclient.esikap.modal.SpinnerModal
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.COPUpdateStatusModel
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.model.SSCECUpdateStatusModel
import com.ardclient.esikap.model.api.FileModel
import com.ardclient.esikap.model.api.KapalStatusResponse
import com.ardclient.esikap.model.api.UploadFileModel
import com.ardclient.esikap.model.api.UploadModel
import com.ardclient.esikap.model.reusable.SanitasiModel
import com.ardclient.esikap.service.ApiClient
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DialogUtils
import com.ardclient.esikap.utils.LocaleHelper
import com.ardclient.esikap.utils.NetworkUtils
import com.ardclient.esikap.utils.SessionUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SSCECInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySscecInputBinding
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var isUpdate: Boolean = false
    private var existingID: Int = 0

    private lateinit var SSCECSanitasi: SanitasiModel
    private lateinit var SSCECDataUmum: SSCECModel
    private lateinit var SSCECSignature: SSCECModel

    private lateinit var sscecData: SSCECModel

    private lateinit var kapal: KapalModel

    // db
    private lateinit var db: SSCECRoomDatabase
    private lateinit var dao: SSCECDao

    // modal
    private lateinit var spinner: SpinnerModal
    private var isUploaded = false
    private var isHasUpdate = false

    // sessiion
    private var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySscecInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            if (!isUploaded && isHasUpdate || !isUploaded && !isUpdate){
                DialogUtils.showNotSavedDialog(this@SSCECInputActivity, object: DialogUtils.DialogListener {
                    override fun onConfirmed() {
                        finish()
                    }
                })
            }else{
                finish()
            }
        }

        // on back
        onBackPressedDispatcher.addCallback(this) {
            if (!isUploaded && isHasUpdate || !isUploaded && !isUpdate){
                DialogUtils.showNotSavedDialog(this@SSCECInputActivity, object: DialogUtils.DialogListener {
                    override fun onConfirmed() {
                        finish()
                    }
                })
            }else{
                finish()
            }
        }

        // modal
        spinner = SpinnerModal()

        // INIT DB
        db = SSCECRoomDatabase.getDatabase(this)
        dao = db.getSSCECDao()

        // existing kapal data
        kapal = intent.getParcelableExtra("KAPAL") ?: KapalModel()

        // session
        val session = SessionUtils.getUserSession(this)
        username = session.userName!!

        // existing data
        val existingData = intent.getParcelableExtra<SSCECModel>("SSCEC")
        if (existingData != null){
            isUpdate = true
            existingID = existingData.id
            SSCECSanitasi = existingData.sanitasi
            SSCECDataUmum = existingData
            SSCECSignature = existingData
            sscecData = existingData

            // button
            binding.bottomContainerEdit.visibility = View.VISIBLE
            binding.bottomContainerSave.visibility = View.GONE

            // header
            binding.topAppBar.title = getString(R.string.doc_sscec_title)

            // chip
            binding.chipSanitasi.isChecked = true
            binding.chipSanitasi.text = "Lengkap"
            binding.chipDataUmum.isChecked = true
            binding.chipDataUmum.text = "Lengkap"
            binding.chipDokumenKapal.isChecked = true
            binding.chipDokumenKapal.text = "Lengkap"
        }else{
            // init model
            SSCECSanitasi = SanitasiModel()
            SSCECDataUmum = SSCECModel()
            SSCECSignature = SSCECModel()

            sscecData = SSCECModel()
        }


        // hanlde on uploaded
        if (sscecData.isUpload){
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
                isHasUpdate = isUpdate!!
                if (isUpdate == true){
                    binding.uploadButton.isEnabled = false
                    binding.tvHasUpdate.visibility = View.VISIBLE
                }

                // == Basic Data
                val basicData = data?.getParcelableExtra<SSCECModel>("BASIC")
                if (basicData != null) {
                    SSCECDataUmum = basicData
                    binding.chipDataUmum.isChecked = true
                    binding.chipDataUmum.text = "Lengkap"
                }

                // == Doc Data
                val signData = data?.getParcelableExtra<SSCECModel>("SIGNATURE")
                if (signData != null) {
                    SSCECSignature = signData
                    binding.chipDokumenKapal.isChecked = true
                    binding.chipDokumenKapal.text = "Lengkap"
                }

                // == Sanitasi Data
                val sanitasi = data?.getParcelableExtra<SanitasiModel>("SANITASI")
                if (sanitasi != null) {
                    SSCECSanitasi = sanitasi
                    binding.chipSanitasi.isChecked = true
                    binding.chipSanitasi.text = "Lengkap"
                }

            }
        }

        binding.cardSanitasi.setOnClickListener {
            val intent = Intent(this, SanitasiInputActivity::class.java)
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipSanitasi.isChecked) {
                intent.putExtra("EXISTING_DATA", SSCECSanitasi)
            }
            launcher?.launch(intent)
        }

        binding.cardDataUmum.setOnClickListener {
            val intent = Intent(this, SSCECInputDataUmumActivity::class.java)
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipDataUmum.isChecked) {
                intent.putExtra("EXISTING_DATA", SSCECDataUmum)
            }
            launcher?.launch(intent)
        }

        binding.cardDokumenKapal.setOnClickListener {
            val intent = Intent(this, SSCECInputRekomendasiActivity::class.java)
            intent.putExtra("IS_UPLOAD", isUploaded)
            if (binding.chipDokumenKapal.isChecked) {
                intent.putExtra("EXISTING_DATA", SSCECSignature)
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
            DialogUtils.showDeleteDialog(this, object : DialogUtils.OnDeleteConfirmListener {
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
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onUploadButton() {
        spinner.show(supportFragmentManager, "LOADING")

        val fileHasilPemeriksaan = Base64Utils.uriToBase64(this, SSCECSanitasi.pemeriksanDoc.toUri())
        val fileMasalahKesehatan = Base64Utils.uriToBase64(this, SSCECSanitasi.masalahKesehatanFile.toUri())

        val fileList = listOf(
            UploadFileModel("hasilpemeriksaan", fileHasilPemeriksaan!!, SSCECDataUmum.id),
            UploadFileModel("masalahkesehatan", fileMasalahKesehatan!!, SSCECDataUmum.id)
        )

        // Handle result
        val uploadedFilesList = mutableListOf<FileModel>()
        var successCount = 0
        var errorCount = 0

        for (file in fileList) {
            if (errorCount == 0) {
                if (file.image != null) {
                    val call = ApiClient.apiService.uploadSSCECSingle(file)

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
                } else continue
            }else{
                onClearUploadedDoc()
            }
        }
    }

    private fun onClearUploadedDoc() {
        val call = ApiClient.apiService.uploadSSCECDelete(SSCECDataUmum.id.toString())

        call.enqueue(object : Callback<ApiResponse<Any>>{
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                spinner.dismiss()
                if (response.isSuccessful){
                    Toast.makeText(this@SSCECInputActivity, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@SSCECInputActivity, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                spinner.dismiss()
                Toast.makeText(this@SSCECInputActivity, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onUploadDokSuccess(uploadedFilesList: MutableList<FileModel>) {
        val kapalId: Int = if (sscecData.flag.lowercase() == "agen") {
            sscecData.kapalId
        } else {
            999123
        }

        val bodyRequest = UploadModel(sscecData, uploadedFilesList, kapalId)
        val call = ApiClient.apiService.uploadSSCEC(bodyRequest)

        call.enqueue(object : Callback<ApiResponse<Any>>{
            override fun onResponse(
                call: Call<ApiResponse<Any>>,
                response: Response<ApiResponse<Any>>
            ) {
                spinner.dismiss()
                if (response.isSuccessful){
                    onUploadSuccess()
                    //Toast.makeText(this@SSCECInputActivity, "SUKES", Toast.LENGTH_SHORT).show()
                }else{
                    spinner.dismiss()
                    Toast.makeText(this@SSCECInputActivity, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                spinner.dismiss()
                Toast.makeText(this@SSCECInputActivity, getString(R.string.error_something), Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun onUploadSuccess() {
        val updatedData = SSCECUpdateStatusModel(id = sscecData.id, isUpload = true)
        dao.updateSSCECStatus(updatedData)
        isUploaded = true
        Toast.makeText(this@SSCECInputActivity, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
        updateUIonUploaded()
    }

    private fun updateUIonUploaded() {
        binding.deleteButton.visibility = View.GONE
        binding.updateButton.visibility = View.GONE
        binding.uploadButton.text = getString(R.string.uploaded)
        binding.uploadButton.setBackgroundColor(getColor(R.color.gray))
        binding.uploadButton.setTextColor(getColor(R.color.black))
        binding.uploadButton.isEnabled = false
    }

    private fun onSaveButton() {
        if (binding.chipDataUmum.isChecked && binding.chipSanitasi.isChecked && binding.chipDokumenKapal.isChecked) {
            val sscecUpdate = if (isUpdate) {
                SSCECModel(
                    id = existingID,
                    kapalId = kapal.id,
                    kapal = kapal,
                    pelabuhanTujuan = SSCECDataUmum.pelabuhanTujuan,
                    tglTiba = SSCECDataUmum.tglTiba,
                    lokasiSandar = SSCECDataUmum.lokasiSandar,
                    jumlahABKAsing = SSCECDataUmum.jumlahABKAsing,
                    asingSehat = SSCECDataUmum.asingSehat,
                    asingSakit = SSCECDataUmum.asingSakit,
                    jumlahABKWNI = SSCECDataUmum.jumlahABKWNI,
                    wniSehat = SSCECDataUmum.wniSehat,
                    wniSakit = SSCECDataUmum.wniSakit,
                    recSSCEC = SSCECSignature.recSSCEC,
                    recTanggal = SSCECSignature.recTanggal,
                    recJam = SSCECSignature.recJam,
                    signNamaPetugas = SSCECSignature.signNamaPetugas,
                    signNamaKapten = SSCECSignature.signNamaKapten,
                    signKapten = SSCECSignature.signKapten,
                    signPetugas = SSCECSignature.signPetugas,
                    sanitasi = SSCECSanitasi,
                    jenisLayanan = SSCECDataUmum.jenisLayanan,
                    jenisPelayaran = SSCECDataUmum.jenisPelayaran,
                    sscecLama = SSCECDataUmum.sscecLama,
                    tempatTerbit = SSCECDataUmum.tempatTerbit,
                    username = username,
                    namaPetugas2 = SSCECSignature.namaPetugas2,
                    namaPetugas3 = SSCECSignature.namaPetugas3,
                    signPetugas2 = SSCECSignature.signPetugas2,
                    signPetugas3 = SSCECSignature.signPetugas3,
                    nipPetugas2 = SSCECSignature.nipPetugas2,
                    nipPetugas3 = SSCECSignature.nipPetugas3,
                )
            } else {
                SSCECModel(
                    kapalId = kapal.id,
                    kapal = kapal,
                    pelabuhanTujuan = SSCECDataUmum.pelabuhanTujuan,
                    tglTiba = SSCECDataUmum.tglTiba,
                    lokasiSandar = SSCECDataUmum.lokasiSandar,
                    jumlahABKAsing = SSCECDataUmum.jumlahABKAsing,
                    asingSehat = SSCECDataUmum.asingSehat,
                    asingSakit = SSCECDataUmum.asingSakit,
                    jumlahABKWNI = SSCECDataUmum.jumlahABKWNI,
                    wniSehat = SSCECDataUmum.wniSehat,
                    wniSakit = SSCECDataUmum.wniSakit,
                    recSSCEC = SSCECSignature.recSSCEC,
                    recTanggal = SSCECSignature.recTanggal,
                    recJam = SSCECSignature.recJam,
                    signNamaPetugas = SSCECSignature.signNamaPetugas,
                    signNamaKapten = SSCECSignature.signNamaKapten,
                    signKapten = SSCECSignature.signKapten,
                    signPetugas = SSCECSignature.signPetugas,
                    sanitasi = SSCECSanitasi,
                    jenisLayanan = SSCECDataUmum.jenisLayanan,
                    jenisPelayaran = SSCECDataUmum.jenisPelayaran,
                    sscecLama = SSCECDataUmum.sscecLama,
                    tempatTerbit = SSCECDataUmum.tempatTerbit,
                    username = username,
                    namaPetugas2 = SSCECSignature.namaPetugas2,
                    namaPetugas3 = SSCECSignature.namaPetugas3,
                    signPetugas2 = SSCECSignature.signPetugas2,
                    signPetugas3 = SSCECSignature.signPetugas3,
                    nipPetugas2 = SSCECSignature.nipPetugas2,
                    nipPetugas3 = SSCECSignature.nipPetugas3
                )
            }

            onSubmitData(sscecUpdate)
        } else {
            Toast.makeText(this@SSCECInputActivity, getString(R.string.data_not_completed), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onDeleteData() {
        dao.deleteSSCEC(SSCECDataUmum)
        Toast.makeText(this, getString(R.string.document_deleted), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun onSubmitData(sscec: SSCECModel) {
        Log.d("SAVED DATA SSCEC", sscec.sanitasi.toString())
        if (dao.getSSCECById(sscec.id).isEmpty()){
            dao.createSSCEC(sscec)
            Toast.makeText(this, getString(R.string.document_created), Toast.LENGTH_SHORT).show()
            finish()
        }else{
            dao.updateSSCEC(sscec)
            sscecData = sscec
            Toast.makeText(this, getString(R.string.document_updated), Toast.LENGTH_SHORT).show()
            // reset has update
            isHasUpdate = false
            binding.uploadButton.isEnabled = true
            binding.tvHasUpdate.visibility = View.GONE
        }
    }

    override fun attachBaseContext(base: Context?) {
        LocaleHelper().setLocale(base!!, LocaleHelper().getLanguage(base))
        super.attachBaseContext(LocaleHelper().onAttach(base))
    }
}