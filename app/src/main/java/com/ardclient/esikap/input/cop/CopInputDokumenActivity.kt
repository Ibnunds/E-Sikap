package com.ardclient.esikap.input.cop

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.ardclient.esikap.databinding.ActivityCopInputDokumenBinding
import com.ardclient.esikap.modal.ImageSelectorModal
import com.ardclient.esikap.model.reusable.DokumenKapalModel
import com.ardclient.esikap.utils.InputValidation
import com.squareup.picasso.Picasso

class CopInputDokumenActivity : AppCompatActivity(), ImageSelectorModal.OnImageSelectedListener {
    private lateinit var binding: ActivityCopInputDokumenBinding
    private var pickedDoc: String = ""
    private lateinit var copDocData: DokumenKapalModel

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

        // check existing data
        val existingData = intent.getParcelableExtra<DokumenKapalModel>("EXISTING_DATA")
        if (existingData != null){
            copDocData = existingData
            initExistingData()
        }else{
            copDocData = DokumenKapalModel()
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

    private fun initExistingData() {
        // text form
        binding.etKarantina.editText?.setText(copDocData.isyaratKarantina)
        binding.etAktifitas.editText?.setText(copDocData.aktifitasKapal)

        // image form
        docMDH = copDocData.mdh
        docSSCEC = copDocData.sscec
        docVaksin = copDocData.daftarVaksinasi
        docABK = copDocData.daftarABK
        docBukuKuning = copDocData.bukuKuning
         docCertP3K = copDocData.certP3K
        docBukuSehat = copDocData.bukuKesehatan
        docPerjalanan = copDocData.catatanPerjalanan
        docShipParticular = copDocData.shipParticular
        docIzinBerlayar = copDocData.izinBerlayar
        docNarkotik = copDocData.daftarNarkotik
        docObat = copDocData.daftarObat
        docAlkes = copDocData.daftarAlkes

        // check updated doc
        checkUpdatedDoc()
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
                val intent = Intent(this@CopInputDokumenActivity, CopInputActivity::class.java)
                intent.putExtra("COP_DOC", documentData)
                setResult(RESULT_OK, intent)
                finish()
            }else{
                Toast.makeText(this@CopInputDokumenActivity, "Dokumen belum lengkap!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this@CopInputDokumenActivity, "Form belum lengkap!", Toast.LENGTH_SHORT).show()
        }
    }



    private fun pickDocument(documentType: String) {
        // dialog
        val imageSelectorDialog = ImageSelectorModal()

        pickedDoc = documentType
        imageSelectorDialog.show(supportFragmentManager, "IMAGE_PICKER")
    }

    override fun onImageSelected(imageUri: Uri) {
        //val imageBase64 = Base64Utils.uriToBase64(this, imageUri)
        val uriString = imageUri.toString()
        when(pickedDoc) {
            "MDH" -> {
                docMDH = uriString
            }
            "SSCEC" -> {
                docSSCEC = uriString
            }
            "Vaksin" -> {
                docVaksin = uriString
            }
            "ABK" -> {
                docABK = uriString
            }
            "BukuKuning" -> {
                docBukuKuning = uriString
            }
            "CertP3K" -> {
                docCertP3K = uriString
            }
            "BukuSehat" -> {
                docBukuSehat = uriString
            }
            "Perjalanan" -> {
                docPerjalanan = uriString
            }
            "ShipParticular" -> {
                docShipParticular = uriString
            }
            "IzinBerlayar" -> {
                docIzinBerlayar = uriString
            }
            "Narkotik" -> {
                docNarkotik = uriString
            }
            "Obat" -> {
                docObat = uriString
            }
            "Alkes" -> {
                docAlkes = uriString
            }
        }

        // check updated doc
        checkUpdatedDoc()
    }

    private fun checkUpdatedDoc() {
        val selectedTitle = "Update Dokumen"

        // MDH
        if (docMDH != null){
            binding.btnSelectMDH.text = selectedTitle
            binding.prevMDH.visibility = View.VISIBLE
            Picasso.get().load(docMDH).fit().into(binding.prevMDH)
        }

        //SSCEC
        if (docSSCEC != null){
            binding.btnSelectSSCEC.text = selectedTitle
            binding.prevSSCEC.visibility = View.VISIBLE
            Picasso.get().load(docSSCEC).fit().into(binding.prevSSCEC)
        }

        //Vaksin
        if (docVaksin != null){
            binding.btnSelectVaksin.text = selectedTitle
            binding.prevVaksin.visibility = View.VISIBLE
            Picasso.get().load(docVaksin).fit().into(binding.prevVaksin)
        }

        //ABK
        if (docABK != null){
            binding.btnSelectABK.text = selectedTitle
            binding.prevABK.visibility = View.VISIBLE
            Picasso.get().load(docABK).fit().into(binding.prevABK)
        }

        //Buku Kuning
        if (docBukuKuning != null){
            binding.btnSelectBukuKuning.text = selectedTitle
            binding.prevBukuKuning.visibility = View.VISIBLE
            Picasso.get().load(docBukuKuning).fit().into(binding.prevBukuKuning)
        }

        //P3K
        if (docCertP3K != null){
            binding.btnSelectCertP3K.text = selectedTitle
            binding.prevP3K.visibility = View.VISIBLE
            Picasso.get().load(docCertP3K).fit().into(binding.prevP3K)
        }

        //Buku Kesahatan
        if (docBukuSehat != null){
            binding.btnSelectBukuSehat.text = selectedTitle
            binding.prevBukuKesehatan.visibility = View.VISIBLE
            Picasso.get().load(docBukuSehat).fit().into(binding.prevBukuKesehatan)
        }

        //Catatan Perjalanan
        if (docPerjalanan != null){
            binding.btnSelectPerjalanan.text = selectedTitle
            binding.prevCatatanPerjalanan.visibility = View.VISIBLE
            Picasso.get().load(docPerjalanan).fit().into(binding.prevCatatanPerjalanan)
        }

        //Ship Particular
        if (docShipParticular != null){
            binding.btnSelectShipParticular.text = selectedTitle
            binding.prevShipParticular.visibility = View.VISIBLE
            Picasso.get().load(docShipParticular).fit().into(binding.prevShipParticular)
        }

        //Izin berlayar
        if (docIzinBerlayar != null){
            binding.btnSelectIzinBerlayar.text = selectedTitle
            binding.prevIzinBerlayar.visibility = View.VISIBLE
            Picasso.get().load(docIzinBerlayar).fit().into(binding.prevIzinBerlayar)
        }

        //Daftar Narkotik
        if (docNarkotik != null){
            binding.btnSelectNarkotik.text = selectedTitle
            binding.prevNarkotik.visibility = View.VISIBLE
            Picasso.get().load(docNarkotik).fit().into(binding.prevNarkotik)
        }

        //Daftar Obat
        if (docObat != null){
            binding.btnSelectObat.text = selectedTitle
            binding.prevObat.visibility = View.VISIBLE
            Picasso.get().load(docObat).fit().into(binding.prevObat)
        }

        //Alkse
        if (docAlkes != null){
            binding.btnSelectAlkse.text = selectedTitle
            binding.prevAlkes.visibility = View.VISIBLE
            Picasso.get().load(docAlkes).fit().into(binding.prevAlkes)
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