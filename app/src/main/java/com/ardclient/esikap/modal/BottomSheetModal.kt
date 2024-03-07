package com.ardclient.esikap.modal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ardclient.esikap.InputDataActivity
import com.ardclient.esikap.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView

class BottomSheetModal: BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val catBlue = view.findViewById<MaterialCardView>(R.id.catBlue)

        catBlue.setOnClickListener {
            val intent = Intent(context, InputDataActivity::class.java)
            startActivity(intent)
            dismiss()
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}