package com.ardclient.esikap.modal

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.ardclient.esikap.R
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageSelectorModal : DialogFragment() {
    private lateinit var galleryButton: MaterialButton
    private lateinit var cameraButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private var currentPhotoPath: String? = null

    companion object {
        const val REQUEST_CODE_GALLERY = 1
        const val REQUEST_CODE_CAMERA = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_image_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        galleryButton = view.findViewById(R.id.galleryButton)
        cameraButton = view.findViewById(R.id.cameraButton)
        cancelButton = view.findViewById(R.id.cancelButton)

        cancelButton.setOnClickListener {
            dismiss()
        }

        galleryButton.setOnClickListener {
            val galleryIntent = Intent(MediaStore.ACTION_PICK_IMAGES)
            startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
        }

        cameraButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    // Gambar dipilih dari galeri, kirim kembali hasilnya ke aktivitas
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        (activity as? OnImageSelectedListener)?.onImageSelected(it)
                    }
                    dismiss()
                }
                REQUEST_CODE_CAMERA -> {
                    // Gambar diambil dari kamera, kirim kembali hasilnya ke aktivitas
                    currentPhotoPath?.let {
                        val photoFile = File(it)
                        val photoUri = FileProvider.getUriForFile(
                            requireContext(),
                            requireContext().packageName + ".provider",
                            photoFile
                        )
                        (activity as? OnImageSelectedListener)?.onImageSelected(photoUri)
                    }
                    dismiss()
                }
            }
        }
    }

    private fun createImageFile(): File? {
        // Membuat nama file gambar
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Simpan jalur file
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera app to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().packageName + ".provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
                }
            }
        }
    }


    // Interface untuk mendengarkan pemilihan gambar
    interface OnImageSelectedListener {
        fun onImageSelected(imageUri: Uri)
    }
}