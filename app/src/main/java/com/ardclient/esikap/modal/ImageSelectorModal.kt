package com.ardclient.esikap.modal

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.ardclient.esikap.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ImageSelectorModal(private val listener: OnImageSelectedListener? = null) : DialogFragment() {
    private lateinit var galleryButton: MaterialButton
    private lateinit var cameraButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private var currentPhotoPath: String? = null

    // companion object
    // Gallery
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            (activity as? OnImageSelectedListener)?.onImageSelected(it)
            dismiss()
        }
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

        // Camera
        val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                // Gambar diambil dari kamera, kirim kembali hasilnya ke aktivitas
                currentPhotoPath?.let {
                    val photoFile = File(it)
                    val photoUri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().packageName + ".provider",
                        photoFile
                    )
                    (activity as? OnImageSelectedListener)?.onImageSelected(photoUri)
                    dismiss()
                }
            }
        }

        galleryButton.setOnClickListener {
            //getContent.launch("image/*")
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            pickerActivityResultLauncher.launch(Intent.createChooser(intent, "Select a photo"))
            ImagePicker.with(requireActivity())
                .crop()
                .galleryOnly()
                .compress(1536)
                .maxResultSize(1080,1080)
                .galleryMimeTypes(  //Exclude gif images
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .createIntent {
                    startForImageResult.launch(it)
                }
        }

        cameraButton.setOnClickListener {
            //dispatchTakePictureIntent(takePicture)
            ImagePicker.with(requireActivity())
                .crop()
                .cameraOnly()
                .compress(1536)
                .maxResultSize(1080,1080)
                .createIntent {
                    startForImageResult.launch(it)
                }
        }
    }

    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    (activity as? OnImageSelectedListener)?.onImageSelected(fileUri)
                    listener?.onImageSelected(fileUri)
                    dismiss()
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }

    private val pickerActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            // This is the URI for the selected photo from the user.
            val photoUri = result.data!!.data
            // Set URI to the image view to display the image.
            //imageView.setImageURI(photoUri)
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val resolver = requireContext().contentResolver

            resolver.takePersistableUriPermission(photoUri!!, flags)
            photoUri?.let { uri ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10 and above, request persistent read permission
                    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    requireContext().contentResolver.takePersistableUriPermission(uri, flags)
                }
                (activity as? OnImageSelectedListener)?.onImageSelected(uri)
            }
            dismiss()
        }else{
            dismiss()
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

    private fun dispatchTakePictureIntent(takePicture: ActivityResultLauncher<Uri>) {
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
                    takePicture.launch(photoURI)
                }
            }
        }
    }


    // Interface untuk mendengarkan pemilihan gambar
    interface OnImageSelectedListener {
        fun onImageSelected(imageUri: Uri)
    }
}