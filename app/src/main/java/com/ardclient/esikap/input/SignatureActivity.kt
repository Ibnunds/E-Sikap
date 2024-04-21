package com.ardclient.esikap.input

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ardclient.esikap.databinding.ActivitySignatureBinding
import com.ardclient.esikap.input.phqc.PHQCInputActivity
import com.ardclient.esikap.utils.SessionUtils
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.ByteArrayOutputStream


class SignatureActivity : AppCompatActivity() {
    private var isSigned: Boolean = false
    private var isSignedComplete: Boolean = false
    private var signatureBitmap: Bitmap? = null

    private lateinit var binding: ActivitySignatureBinding
    private lateinit var sendType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // signature pad
        val signPad = binding.signaturePad

        // image
        val signatureImage = binding.ivSignature

        // layout
        val savedFrame = binding.savedLinear
        val unsavedFrame = binding.unsavedLinear

        // input
        val namaPetugas = binding.etNamaPetugas

        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // handle if from existing
        val existingNama = intent.getStringExtra("NAMA")
        if (existingNama!!.isNotEmpty()) {
            namaPetugas.editText?.setText(existingNama)
        }

        sendType = intent.getStringExtra("TYPE") ?: ""

        if (sendType == "PETUGAS_2" || sendType == "PETUGAS_3"){
            binding.etNamaPetugas.isEnabled = true
            binding.etNIP.visibility = View.VISIBLE

            val existingNIP = intent.getStringExtra("NIP")
            if (existingNIP!!.isNotEmpty()){
                binding.etNIP.editText?.setText(existingNIP)
            }
        }else{
            namaPetugas.editText?.isEnabled = false
        }



        binding.clearButton.setOnClickListener {
            if (isSigned) {
                signPad.clear()
            } else {
                Toast.makeText(this, "Belum ada tanda tangan!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.changeButton.setOnClickListener {
            // layout
            savedFrame.visibility = View.GONE
            unsavedFrame.visibility = View.VISIBLE

            signPad.visibility = View.VISIBLE
            signatureImage.visibility = View.GONE
        }

        binding.saveButton.setOnClickListener {
            if (isSigned) {
                signatureImage.setImageBitmap(signPad.transparentSignatureBitmap)
                signatureBitmap = signPad.transparentSignatureBitmap
                signPad.visibility = View.GONE
                signatureImage.visibility = View.VISIBLE

                // layout
                savedFrame.visibility = View.VISIBLE
                unsavedFrame.visibility = View.GONE

                isSignedComplete = true
            } else {
                Toast.makeText(this, "Belum ada tanda tangan!", Toast.LENGTH_SHORT).show()
            }

        }

        binding.sendButton.setOnClickListener {
            val txNamaPetugas = namaPetugas.editText?.text.toString()
            val txNIP = binding.etNIP.editText?.text.toString()

            if (sendType == "PETUGAS_2" || sendType == "PETUGAS_3"){
                if (txNIP.isNullOrEmpty()){
                    Toast.makeText(this, "Form belum lengkap!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (isSignedComplete && txNamaPetugas.isNotEmpty()) {
                val decodedSignature = decodeSignature()

                val intent = Intent(this, PHQCInputActivity::class.java)
                intent.putExtra("NAMA", txNamaPetugas)
                intent.putExtra("SIGNATURE", decodedSignature)
                if (sendType == "PETUGAS_2" || sendType == "PETUGAS_3"){
                    intent.putExtra("NIP", txNIP)
                }
                intent.putExtra("TYPE", sendType)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Form belum lengkap!", Toast.LENGTH_SHORT).show()
            }
        }

        signPad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                Log.d("SIGN", "SIGN START")
            }

            override fun onSigned() {
                isSigned = true
            }

            override fun onClear() {
                isSigned = false
                isSignedComplete = false
            }

        })
    }

    private fun decodeSignature(): ByteArray {
        val bStream = ByteArrayOutputStream()
        signatureBitmap?.compress(Bitmap.CompressFormat.PNG, 100, bStream)

        return bStream.toByteArray()
    }

    // Handle all input focus and keyboard
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