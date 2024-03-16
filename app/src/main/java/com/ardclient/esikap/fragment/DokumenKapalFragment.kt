package com.ardclient.esikap.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ardclient.esikap.DocumentListActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.database.kapal.KapalDAO
import com.ardclient.esikap.database.kapal.KapalRoomDatabase
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.databinding.FragmentDokumenKapalBinding
import com.ardclient.esikap.model.KapalModel

class DokumenKapalFragment : Fragment(R.layout.fragment_dokumen_kapal) {
    private lateinit var db: KapalRoomDatabase
    private lateinit var dao: KapalDAO
    private lateinit var kapal: KapalModel
    private lateinit var binding: FragmentDokumenKapalBinding

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
        binding = FragmentDokumenKapalBinding.bind(view)

        // init db
        db = KapalRoomDatabase.getDatabase(requireContext())
        dao = db.getKapalDAO()

        // existing data
        val kapalData = arguments?.getParcelable<KapalModel>(ARG_DATA)

        if (kapalData != null){
            getKapalData(kapalData.id)
        }

        // Document Count
        initPHQCDocumentCount()

        // On Category Card Pressed
        binding.cardBlue.setOnClickListener {
            onCategoryPressed("BLUE")
        }

    }

    private fun initPHQCDocumentCount() {
        val db = PHQCRoomDatabase.getDatabase(requireContext())
        val dao = db.getPHQCDao()

        val getData = dao.getAllPHQC(kapal.id)

        if (getData != null){
            binding.bodyBlue.text = "${getData.size} Dokumen"
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

    override fun onResume() {
        super.onResume()
        // Check Document Count
        initPHQCDocumentCount()
    }
}