package com.ardclient.esikap.fragment.input.phqc

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.ardclient.esikap.R
import com.ardclient.esikap.SignatureActivity
import com.ardclient.esikap.database.phqc.PHQCDao
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.databinding.FragmentPhqcBinding
import com.ardclient.esikap.fragment.DetailKapalFragment
import com.ardclient.esikap.fragment.DokumenKapalFragment
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.PHQCModel
import com.google.android.material.textfield.TextInputLayout
import java.io.ByteArrayOutputStream


class PHQCFragment : Fragment(R.layout.fragment_phqc) {
    private var launcher: ActivityResultLauncher<Intent>? = null
    private var nmPetugas: String? = null
    private var base64Sign: String? = null
    private lateinit var binding: FragmentPhqcBinding

    // database
    private lateinit var phqc: PHQCModel
    private lateinit var database: PHQCRoomDatabase
    private lateinit var dao: PHQCDao
    private lateinit var kapal: KapalModel

    companion object {
        private const val ARG_DATA = "KAPAL"
        fun newInstance(data: KapalModel): PHQCFragment {
            val fragment = PHQCFragment()
            val args = Bundle()
            args.putParcelable(ARG_DATA, data)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhqcBinding.bind(view)

        with(binding) {

            // handle sign result
            launcher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    val namaPetugas = data?.getStringExtra("NAMA_PETUGAS")
                    val decodedSign = data?.getByteArrayExtra("SIGNATURE")

                    val encodedSign = BitmapFactory.decodeByteArray(decodedSign, 0, decodedSign!!.size)
                    base64Sign = bitmapToBase64(encodedSign)

                    if (!namaPetugas.isNullOrEmpty()) {
                        signLayout.visibility = View.VISIBLE
                        addSignButton.visibility = View.GONE

                        tvSignName.text = "Tanda tangan : $namaPetugas"
                        nmPetugas = namaPetugas
                        ivSignature.setImageBitmap(encodedSign)
                    }
                }
            }

            // Ambil data dari arguments
            val kapalData = arguments?.getParcelable<KapalModel>(ARG_DATA)

            if (kapalData != null){
                kapal = kapalData
            }

            // init database
            database = PHQCRoomDatabase.getDatabase(requireContext())
            dao = database.getPHQCDao()

            ivSignature.setOnClickListener {
                val intent = Intent(requireContext(), SignatureActivity::class.java)
                intent.putExtra("NAMA_PETUGAS", nmPetugas)
                launcher!!.launch(intent)
            }

            addSignButton.setOnClickListener {
                val intent = Intent(requireContext(), SignatureActivity::class.java)
                intent.putExtra("NAMA_PETUGAS", "")
                launcher!!.launch(intent)
            }

            saveButton.setOnClickListener {
                onSaveButtonPressed()
            }
        }
    }

    private fun onSaveButtonPressed() {
        // Mengakses input menggunakan binding
        val agen = binding.etAgen.editText?.text.toString()
        val asal = binding.etAsal.editText?.text.toString()
        val tujuan = binding.etTujuan.editText?.text.toString()
        val dokumen = binding.etDokumen.editText?.text.toString()
        val pemeriksaan = binding.etPemeriksaan.editText?.text.toString()
        val jmlABK = binding.etJmlABK.editText?.text.toString()
        val demam = binding.etDemam.editText?.text.toString()
        val jmlSakit = binding.etJmlSakit.editText?.text.toString()
        val jmlSehat = binding.etJmlSehat.editText?.text.toString()
        val jmlMeninggal = binding.etJmlMeninggal.editText?.text.toString()
        val jmlDirujuk = binding.etJmlDirujuk.editText?.text.toString()
        val sanitasi = binding.etSanitasi.editText?.text.toString()
        val kesimpulan = binding.etKesimpulan.editText?.text.toString()

        // Mengecek apakah semua input terisi
        val isAllFilled = areAllFieldsFilled(
            binding.etAgen,
            binding.etAsal,
            binding.etTujuan,
            binding.etDokumen,
            binding.etPemeriksaan,
            binding.etJmlABK,
            binding.etDemam,
            binding.etJmlSakit,
            binding.etJmlSehat,
            binding.etJmlMeninggal,
            binding.etJmlDirujuk,
            binding.etSanitasi,
            binding.etKesimpulan
        )

        if (isAllFilled){
            if (nmPetugas != null){
                onSaveData(PHQCModel(
                    kapalId = kapal.id,
                    namaAgen = agen,
                    namaKapal = kapal.namaKapal,
                    grosTone = kapal.grossTone,
                    bendera = kapal.bendera,
                    negaraAsal = asal,
                    tujuan = tujuan,
                    dokumenKapal = dokumen,
                    lokasiPemeriksaan = pemeriksaan,
                    jumlahABK = jmlABK.toInt(),
                    deteksiDemam = demam.toInt(),
                    jumlahSehat = jmlSehat.toInt(),
                    jumlahSakit =  jmlSakit.toInt(),
                    jumlahMeninggal = jmlMeninggal.toInt(),
                    jumlahDirujuk = jmlDirujuk.toInt(),
                    statusSanitasi = sanitasi,
                    kesimpulan = kesimpulan,
                    petugasPelaksana = nmPetugas!!,
                    signature = base64Sign!!
                ))
            }else{
                Toast.makeText(requireContext(), "Belum ada tanda tangan!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(), "Mohon lengkapi semua input", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSaveData(phqc: PHQCModel) {
        dao.createPHQC(phqc)

        Toast.makeText(requireContext(), "Dokumen berhasil dibuat!", Toast.LENGTH_SHORT).show()
        activity?.finish()
    }

    private fun areAllFieldsFilled(vararg fields: TextInputLayout): Boolean {
        for (field in fields) {
            if (field.editText?.text!!.isEmpty()) {
                field.error = "${field.hint.toString()} tidak boleh kosong."
                return false
            }else{
                field.isErrorEnabled = false
                field.error = null
            }
        }
        return true
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // Ubah ke format yang diinginkan
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}