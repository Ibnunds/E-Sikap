package com.ardclient.esikap.cop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ardclient.esikap.SanitasiInputActivity
import com.ardclient.esikap.database.cop.COPDao
import com.ardclient.esikap.database.cop.COPRoomDatabase
import com.ardclient.esikap.databinding.ActivityCopInputBinding
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.model.reusable.SanitasiModel
import com.ardclient.esikap.utils.DialogUtils

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

    // db
    private lateinit var db: COPRoomDatabase
    private lateinit var dao: COPDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

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
        }else{
            // init value
            copBasicData = COPModel()
            copDocData = DokumenKapalModel()
            copSanitasi = SanitasiModel()
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

            }
        }

        // card button
        binding.cardCOPDataUmum.setOnClickListener {
            val intent = Intent(this, CopInputDataUmumActivity::class.java)
            if (copBasicData.tujuan.isNotEmpty()){
                intent.putExtra("EXISTING_DATA", copBasicData)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPDokumenKapal.setOnClickListener {
            val intent = Intent(this, CopInputDokumenActivity::class.java)
            if (copDocData.aktifitasKapal.isNotEmpty()){
                intent.putExtra("EXISTING_DATA", copDocData)
            }
            launcher?.launch(intent)
        }

        binding.cardCOPSanitasi.setOnClickListener {
            val intent = Intent(this, SanitasiInputActivity::class.java)
            intent.putExtra("SENDER", "COP")
            if (copSanitasi.sanDapur.isNotEmpty()){
                intent.putExtra("EXISTING_DATA", copSanitasi)
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
                    rekomendasi = copBasicData.rekomendasi
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
                    rekomendasi = copBasicData.rekomendasi
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
        }
    }
}