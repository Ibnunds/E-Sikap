package com.ardclient.esikap.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ardclient.esikap.DocumentListActivity
import com.ardclient.esikap.PHQCInputActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.database.kapal.KapalDAO
import com.ardclient.esikap.database.kapal.KapalRoomDatabase
import com.ardclient.esikap.database.phqc.PHQCDao
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.model.KapalModel
import com.google.android.material.card.MaterialCardView


class DokumenKapalFragment : Fragment(R.layout.fragment_dokumen_kapal) {
    private lateinit var db: KapalRoomDatabase
    private lateinit var dao: KapalDAO
    private lateinit var kapal: KapalModel

    companion object {
        private const val ARG_DATA = "KAPAL"
        fun newInstance(data: KapalModel): DokumenKapalFragment {
            val fragment = DokumenKapalFragment()
            val args = Bundle()
            args.putParcelable(ARG_DATA, data)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val catBlue = view.findViewById<MaterialCardView>(R.id.cardBlue) // phqc
        val catGreen = view.findViewById<MaterialCardView>(R.id.cardGreen) // cop
        val catOrange = view.findViewById<MaterialCardView>(R.id.cardOrange) // sscec
        val catAmber = view.findViewById<MaterialCardView>(R.id.cardAmber) // p3k

        // init db
        db = KapalRoomDatabase.getDatabase(requireContext())
        dao = db.getKapalDAO()

        // existing data
        val kapalData = arguments?.getParcelable<KapalModel>(ARG_DATA)

        if (kapalData != null){
            getKapalData(kapalData.id)
        }

        catBlue.setOnClickListener {
            onCategoryPressed("BLUE")
        }

    }

    private fun getKapalData(id: Int) {
        val kapalData = dao.getKapalById(id)
        kapal = kapalData[0]
    }

    private fun onCategoryPressed(type: String) {
        when(type){
            "BLUE" -> {
                val intent = Intent(requireContext(), DocumentListActivity::class.java)
                intent.putExtra("KAPAL", kapal)
                startActivity(intent)
            }
        }

    }
}