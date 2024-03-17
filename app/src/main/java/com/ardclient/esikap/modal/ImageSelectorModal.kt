package com.ardclient.esikap.modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ardclient.esikap.R
import com.google.android.material.button.MaterialButton

class ImageSelectorModal : DialogFragment() {
    private lateinit var galleryButton: MaterialButton
    private lateinit var cameraButton: MaterialButton
    private lateinit var cancelButton: MaterialButton

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
    }
}