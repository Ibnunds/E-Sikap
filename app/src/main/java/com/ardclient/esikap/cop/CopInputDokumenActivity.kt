package com.ardclient.esikap.cop

import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.ardclient.esikap.databinding.ActivityCopInputDokumenBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.utils.Base64Utils
import com.ardclient.esikap.utils.InputValidation

class CopInputDokumenActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var binding: ActivityCopInputDokumenBinding
    private var pickedDoc: String = ""

    // document base64
    private var docMDH: String? = null
    private var docSSCEC: String? = null
    private var docVaksin: String? = null
    private var docABK: String? = null
    private var docBukuKuning: String? = null
    private var docCertP3K: String? = null
    private var docBukuSehat: String? = null
    private var docPerjalanan: String? = null
    private var docShipParticular: String? = null
    private var docIzinBerlayar: String? = null
    private var docNarkotik: String? = null
    private var docObat: String? = null
    private var docAlkes: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopInputDokumenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // header
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // button
        binding.saveButton.setOnClickListener {
            onSaveButton()
        }

        // select button
        binding.btnSelectMDH.setOnClickListener {
            pickDocument("MDH")
        }

        binding.btnSelectSSCEC.setOnClickListener {
            pickDocument("SSCEC")
        }

        binding.btnSelectVaksin.setOnClickListener {
            pickDocument("Vaksin")
        }

        binding.btnSelectABK.setOnClickListener {
            pickDocument("ABK")
        }

        binding.btnSelectBukuKuning.setOnClickListener {
            pickDocument("BukuKuning")
        }

        binding.btnSelectCertP3K.setOnClickListener {
            pickDocument("CertP3K")
        }

        binding.btnSelectBukuSehat.setOnClickListener {
            pickDocument("BukuSehat")
        }

        binding.btnSelectPerjalanan.setOnClickListener {
            pickDocument("Perjalanan")
        }

        binding.btnSelectShipParticular.setOnClickListener {
            pickDocument("ShipParticular")
        }

        binding.btnSelectIzinBerlayar.setOnClickListener {
            pickDocument("IzinBerlayar")
        }

        binding.btnSelectNarkotik.setOnClickListener {
            pickDocument("Narkotik")
        }

        binding.btnSelectObat.setOnClickListener {
            pickDocument("Obat")
        }

        binding.btnSelectAlkse.setOnClickListener {
            pickDocument("Alkes")
        }
    }

    private fun onSaveButton() {
        val isFormComplete = InputValidation.isAllFieldComplete(
            binding.etKarantina,
            binding.etAktifitas
        )

        if (isFormComplete){

            val isyaratKarantinaValue = binding.etKarantina.editText?.text.toString()
            val aktifitasKapal = binding.etAktifitas.editText?.text.toString()

            if (docMDH != null && docSSCEC != null && docVaksin != null && docABK != null && docBukuKuning != null && docCertP3K != null && docBukuSehat != null && docPerjalanan != null && docShipParticular != null && docIzinBerlayar != null && docNarkotik != null && docObat != null && docAlkes != null){
                val documentData = DokumenKapalModel(isyaratKarantina = isyaratKarantinaValue, aktifitasKapal = aktifitasKapal, mdh = docMDH!!, sscec = docSSCEC!!, daftarVaksinasi = docVaksin!!, daftarABK = docABK!!, bukuKuning = docBukuKuning!!, certP3K = docCertP3K!!, bukuKesehatan = docBukuSehat!!, catatanPerjalanan = docPerjalanan!!, shipParticular = docShipParticular!!, izinBerlayar = docIzinBerlayar!!, daftarNarkotik = docNarkotik!!, daftarObat = docObat!!, daftarAlkes = docAlkes!!)
                //startActivity()
            }else{
                Toast.makeText(this@CopInputDokumenActivity, "Dokumen belum lengkap!", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun pickDocument(documentType: String) {
        // dialog
        val imageSelectorDialog = ImageSelectorModal()

        pickedDoc = documentType
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }

    override fun onImageSelected(imageUri: Uri) {
        val imageBase64 = Base64Utils.uriToBase64(this, imageUri)
        when(pickedDoc) {
            "MDH" -> {
                docMDH = imageBase64
            }
            "SSCEC" -> {
                docSSCEC = imageBase64
            }
            "Vaksin" -> {
                docVaksin = imageBase64
            }
            "ABK" -> {
                docABK = imageBase64
            }
            "BukuKuning" -> {
                docBukuKuning = imageBase64
            }
            "CertP3K" -> {
                docCertP3K = imageBase64
            }
            "BukuSehat" -> {
                docBukuSehat = imageBase64
            }
            "Perjalanan" -> {
                docPerjalanan = imageBase64
            }
            "ShipParticular" -> {
                docShipParticular = imageBase64
            }
            "IzinBerlayar" -> {
                docIzinBerlayar = imageBase64
            }
            "Narkotik" -> {
                docNarkotik = imageBase64
            }
            "Obat" -> {
                docObat = imageBase64
            }
            "Alkes" -> {
                docAlkes = imageBase64
            }
        }

        // check updated doc
        checkUpdatedDoc()
    }

    private fun checkUpdatedDoc() {
        val selectedTitle = "1 dokumen dipilih."

        // MDH
        if (docMDH != null){
            binding.btnSelectMDH.text = selectedTitle
        }

        //SSCEC
        if (docSSCEC != null){
            binding.btnSelectSSCEC.text = selectedTitle
        }

        //Vaksin
        if (docVaksin != null){
            binding.btnSelectVaksin.text = selectedTitle
        }

        //ABK
        if (docABK != null){
            binding.btnSelectABK.text = selectedTitle
        }

        //Buku Kuning
        if (docBukuKuning != null){
            binding.btnSelectBukuKuning.text = selectedTitle
        }

        //P3K
        if (docCertP3K != null){
            binding.btnSelectCertP3K.text = selectedTitle
        }

        //Buku Kesahatan
        if (docBukuSehat != null){
            binding.btnSelectBukuSehat.text = selectedTitle
        }

        //Catatan Perjalanan
        if (docPerjalanan != null){
            binding.btnSelectPerjalanan.text = selectedTitle
        }

        //Ship Particular
        if (docShipParticular != null){
            binding.btnSelectShipParticular.text = selectedTitle
        }

        //Izin berlayar
        if (docIzinBerlayar != null){
            binding.btnSelectIzinBerlayar.text = selectedTitle
        }

        //Daftar Narkotik
        if (docNarkotik != null){
            binding.btnSelectNarkotik.text = selectedTitle
        }

        //Daftar Obat
        if (docObat != null){
            binding.btnSelectObat.text = selectedTitle
        }

        //Alkse
        if (docAlkes != null){
            binding.btnSelectAlkse.text = selectedTitle
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}