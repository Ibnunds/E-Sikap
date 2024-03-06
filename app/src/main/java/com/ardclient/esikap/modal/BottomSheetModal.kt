package com.ardclient.esikap.modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ardclient.esikap.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetModal: BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_type, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}