package com.ardclient.esikap.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ardclient.esikap.DocumentListActivity
import com.ardclient.esikap.R
import com.google.android.material.card.MaterialCardView


class DokumenKapalFragment : Fragment(R.layout.fragment_dokumen_kapal) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val catBlue = view.findViewById<MaterialCardView>(R.id.cardBlue) // phqc
        val catGreen = view.findViewById<MaterialCardView>(R.id.cardGreen) // cop
        val catOrange = view.findViewById<MaterialCardView>(R.id.cardOrange) // sscec
        val catAmber = view.findViewById<MaterialCardView>(R.id.cardAmber) // p3k


        catBlue.setOnClickListener {
            onCategoryPressed("BLUE")
        }

    }

    private fun onCategoryPressed(type: String) {
        val intent = Intent(requireContext(), DocumentListActivity::class.java)
        startActivity(intent)
    }
}