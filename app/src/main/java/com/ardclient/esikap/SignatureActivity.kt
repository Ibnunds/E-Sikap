package com.ardclient.esikap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import java.io.ByteArrayOutputStream


class SignatureActivity : AppCompatActivity() {
    private var isSigned: Boolean = false
    private var isSignedComplete: Boolean = false
    private var signatureBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        // header
        val header = findViewById<MaterialToolbar>(R.id.topAppBar)

        // signature pad
        val signPad = findViewById<SignaturePad>(R.id.signaturePad)

        // button
        val clearButton = findViewById<Button>(R.id.clearButton)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val changeButton = findViewById<Button>(R.id.changeButton)
        val sendButton = findViewById<Button>(R.id.sendButton)

        // image
        val signatureImage = findViewById<ImageView>(R.id.ivSignature)

        // layout
        val savedFrame = findViewById<LinearLayout>(R.id.savedLinear)
        val unsavedFrame = findViewById<LinearLayout>(R.id.unsavedLinear)

        // input
        val namaPetugas = findViewById<TextInputLayout>(R.id.etNamaPetugas)

        header.setNavigationOnClickListener {
            finish()
        }


        // handle if from existing
        val existingNama = intent.getStringExtra("NAMA_PETUGAS")
        if (existingNama!!.isNotEmpty()){
            namaPetugas.editText?.setText(existingNama)
        }

        clearButton.setOnClickListener {
            if (isSigned){
                signPad.clear()
            }else{
                Toast.makeText(this, "Belum ada tanda tangan!", Toast.LENGTH_SHORT).show()
            }
        }

        changeButton.setOnClickListener {
            // layout
            savedFrame.visibility = View.GONE
            unsavedFrame.visibility = View.VISIBLE

            signPad.visibility = View.VISIBLE
            signatureImage.visibility = View.GONE
        }

        saveButton.setOnClickListener {
            if (isSigned){
                signatureImage.setImageBitmap(signPad.transparentSignatureBitmap)
                signatureBitmap = signPad.transparentSignatureBitmap
                signPad.visibility = View.GONE
                signatureImage.visibility = View.VISIBLE

                // layout
                savedFrame.visibility = View.VISIBLE
                unsavedFrame.visibility = View.GONE

                isSignedComplete = true
            }else{
                Toast.makeText(this, "Belum ada tanda tangan!", Toast.LENGTH_SHORT).show()
            }

        }

        sendButton.setOnClickListener {
            val txNamaPetugas = namaPetugas.editText?.text.toString()

            if (isSignedComplete && txNamaPetugas.isNotEmpty()){
                val decodedSignature = decodeSignature()

                val intent = Intent(this, PHQCInputActivity::class.java)
                intent.putExtra("NAMA_PETUGAS", txNamaPetugas)
                intent.putExtra("SIGNATURE", decodedSignature)
                setResult(RESULT_OK, intent)
                finish()
            }else{
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