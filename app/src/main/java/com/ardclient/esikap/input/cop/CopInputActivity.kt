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
import com.ardclient.esikap.input.SanitasiInputActivity
import com.ardclient.esikap.database.cop.COPDao
import com.ardclient.esikap.database.cop.COPRoomDatabase
import com.ardclient.esikap.databinding.ActivityCopInputBinding
import com.ardclient.esikap.modal.SpinnerModal
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.UploadResponse
import com.ardclient.esikap.model.api.UploadImageRequest
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.model.reusable.SanitasiModel
import com.ardclient.esikap.service.ApiClient
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.DialogUtils
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
                    Log.d("SANITASI KAPAL", sanitasi.toString())
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
            if (binding.chipCOPDataUmum.isChecked){
                intent.putExtra("EXISTING_DATA", copBasicData)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPDokumenKapal.setOnClickListener {
            val intent = Intent(this, CopInputDokumenActivity::class.java)
            if (binding.chipCOPDokumenKapal.isChecked){
                intent.putExtra("EXISTING_DATA", copDocData)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPSanitasi.setOnClickListener {
            val intent = Intent(this, SanitasiInputActivity::class.java)
            intent.putExtra("SENDER", "COP")
            if (binding.chipCOPSanitasi.isChecked){
                intent.putExtra("EXISTING_DATA", copSanitasi)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPRekomendasi.setOnClickListener {
            val intent = Intent(this, CopInputSignatureActivity::class.java)
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
            //onUploadButton()
        }
    }

    private fun onUploadButton() {
        spinner.show(supportFragmentManager, "LOADING")
        val allDocs = convertAllDocumentsToBase64(copDocData)

        uploadAllDocuments(allDocs) {result ->
            spinner.dismiss()
            val uploadResult: UploadResponse = result
            val uploadStatus = uploadResult.status

            if (uploadStatus == "DONE"){
                Toast.makeText(this@CopInputActivity, "Upload dokumen sukses!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@CopInputActivity, "Upload dokumen gagal, mohon coba lagi!", Toast.LENGTH_SHORT).show()
            }

            Log.d("UPLOAD_RESPONSE", uploadResult.toString())
        }
    }

    private fun convertAllDocumentsToBase64(copDocData: DokumenKapalModel): Map<String, String?> {
        val documents = mutableMapOf<String, String?>()

        // Memeriksa apakah URI tidak null sebelum mengonversinya ke base64
        copDocData.mdh?.let { documents["docMDH"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.sscec?.let { documents["docSSCEC"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.daftarVaksinasi?.let { documents["docVaksinasi"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.daftarABK?.let { documents["docDaftarABK"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.bukuVaksin?.let { documents["docBukuVaksin"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.certP3K?.let { documents["docP3K"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.bukuKesehatan?.let { documents["docBukuKesehatan"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.lpoc?.let { documents["docLPOC"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.shipParticular?.let { documents["docShipParticular"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.lpc?.let { documents["docLPC"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.daftarNarkotik?.let { documents["docNarkotik"] = Base64Utils.uriToBase64(this, it.toUri()) }
        copDocData.daftarObat?.let { documents["docObatObatan"] = Base64Utils.uriToBase64(this, it.toUri()) }

        return documents
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

            Log.d("SAVED_X3D", data.sanitasiKapal.toString())

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

    fun uploadAllDocuments(documents: Map<String, String?>, callback: (UploadResponse) -> Unit) {
        var successCount = 0
        var errorCount = 0
        val uploadResults = mutableMapOf<String, String>()

        for ((docName, document) in documents) {
            // Membuat body request untuk unggah dokumen
            val bodyRequest = UploadImageRequest(document!!, "BATCH")

            // Melakukan panggilan API untuk mengunggah dokumen
            val call = ApiClient.apiService.uploadImage(bodyRequest)

            call.enqueue(object : Callback<ApiResponse<Map<String, Any>>> {
                override fun onResponse(
                    call: Call<ApiResponse<Map<String, Any>>>,
                    response: Response<ApiResponse<Map<String, Any>>>
                ) {
                    if (response.isSuccessful) {
                        val uploadedUrl = response.body()?.data?.get("url") ?: ""
                        uploadResults[docName] = uploadedUrl.toString()
                        successCount++
                    } else {
                        errorCount++
                    }

                    // Jika semua dokumen telah diunggah, panggil callback dengan hasil
                    if (successCount + errorCount == documents.size) {
                        val result = if (errorCount == 0) {
                            UploadResponse("DONE", successCount.toString(), errorCount.toString(),  uploadResults)
                        } else {
                            UploadResponse("ERROR", successCount.toString(), errorCount.toString(), emptyMap())
                        }
                        callback.invoke(result)
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Map<String, Any>>>, t: Throwable) {
                    errorCount++

                    // Jika semua dokumen telah diunggah, panggil callback dengan hasil
                    if (successCount + errorCount == documents.size) {
                        val result = if (errorCount == 0) {
                            UploadResponse("DONE", successCount.toString(), errorCount.toString(), uploadResults)
                        } else {
                            UploadResponse("ERROR", successCount.toString(), errorCount.toString(), emptyMap())
                        }
                        callback.invoke(result)
                    }
                }
            })
        }
    }
}