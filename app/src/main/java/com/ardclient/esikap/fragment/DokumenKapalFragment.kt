package com.ardclient.esikap.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ardclient.esikap.DocumentListActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.database.cop.COPRoomDatabase
import com.ardclient.esikap.database.kapal.KapalDAO
import com.ardclient.esikap.database.kapal.KapalRoomDatabase
import com.ardclient.esikap.database.p3k.P3KRoomDatabase
import com.ardclient.esikap.database.phqc.PHQCRoomDatabase
import com.ardclient.esikap.database.sscec.SSCECRoomDatabase
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
            if (kapalData.flag != "AGEN"){
                getKapalData(kapalData.id)
            }else{
                kapal = kapalData
                handleNeededCard(kapal.tipeDokumen)
            }
        }

        // Document Count
        initPHQCDocumentCount()
        initCOPDocumentCount()
        initSSCECDocumentCount()
        initP3KDocumentCount()

        // On Category Card Pressed
        binding.cardBlue.setOnClickListener {
            onCategoryPressed("BLUE")
        }

        binding.cardGreen.setOnClickListener {
            onCategoryPressed("GREEN")
        }

        binding.cardOrange.setOnClickListener {
            onCategoryPressed("ORANGE")
        }

        binding.cardAmber.setOnClickListener {
            onCategoryPressed("AMBER")
        }

    }

    private fun handleNeededCard(dokList: String) {
        val keySplit = dokList.split(",")
        val keyList = listOf<String>("PHQC", "COP", "SSCEC", "P3K")

        for (key in keyList) {
            if (key in keySplit) {
                // do nothing
            } else {
                when(key) {
                    "PHQC" -> binding.frameBlue.visibility = View.GONE
                    "COP" -> binding.frameGreen.visibility = View.GONE
                    "SSCEC" -> binding.frameOrange.visibility = View.GONE
                    "P3K" -> binding.frameAmber.visibility = View.GONE
                }
            }
        }
    }

    private fun initP3KDocumentCount() {
        val db = P3KRoomDatabase.getDatabase(requireContext())
        val dao = db.getP3KDAO()

        val getData = dao.getAllP3K(kapal.id, kapal.flag)

        val title = "${getData.size} ${getString(R.string.document_title)}"

        binding.bodyAmber.text = title
    }

    private fun initPHQCDocumentCount() {
        val db = PHQCRoomDatabase.getDatabase(requireContext())
        val dao = db.getPHQCDao()

        val getData = dao.getAllPHQC(kapal.id, kapal.flag)

        val title = "${getData.size} ${getString(R.string.document_title)}"

        binding.bodyBlue.text = title
    }

    private fun initCOPDocumentCount() {
        val db = COPRoomDatabase.getDatabase(requireContext())
        val dao = db.getCOPDao()

        val getData = dao.getAllCOP(kapal.id, kapal.flag)

        val title = "${getData.size} ${getString(R.string.document_title)}"

        binding.bodyGreen.text = title
    }

    private fun initSSCECDocumentCount() {
        val db = SSCECRoomDatabase.getDatabase(requireContext())
        val dao = db.getSSCECDao()

        val getData = dao.getAllSSCEC(kapal.id, kapal.flag)

        val title = "${getData.size} ${getString(R.string.document_title)}"

        binding.bodyOrange.text = title
    }

    private fun getKapalData(id: Int) {
        val kapalData = dao.getKapalById(id)
        kapal = kapalData[0]
    }

    private fun onCategoryPressed(type: String) {
        val intent = Intent(requireContext(), DocumentListActivity::class.java)
        intent.putExtra("TYPE", type)
        intent.putExtra("KAPAL", kapal)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Check Document Count
        initPHQCDocumentCount()
        initSSCECDocumentCount()
        initCOPDocumentCount()
        initP3KDocumentCount()
    }
}