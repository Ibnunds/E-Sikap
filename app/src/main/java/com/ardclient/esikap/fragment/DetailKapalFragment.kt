package com.ardclient.esikap.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ardclient.esikap.KapalActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.database.kapal.KapalDAO
import com.ardclient.esikap.database.kapal.KapalRoomDatabase
import com.ardclient.esikap.model.KapalModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DetailKapalFragment : Fragment(R.layout.fragment_detail_kapal) {
    private lateinit var kapal: KapalModel
    private lateinit var dao: KapalDAO
    private lateinit var db: KapalRoomDatabase

    // detail text
    private lateinit var namaKapal: TextView
    private lateinit var grossTone: TextView
    private lateinit var bendera: TextView

    companion object {
        const val ARG_DATA = "KAPAL"
        fun newInstance(data:KapalModel): DetailKapalFragment {
            val fragment = DetailKapalFragment()
            val args = Bundle()
            args.putParcelable(ARG_DATA, data)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init component
        namaKapal = view.findViewById(R.id.tvDetailNamaKapal)
        grossTone = view.findViewById(R.id.tvDetailGrossTone)
        bendera = view.findViewById(R.id.tvDetailBendera)
        val updateButton = view.findViewById<Button>(R.id.updateDataButton)
        val deleteButton = view.findViewById<Button>(R.id.deleteDataButton)

        // init db
        db = KapalRoomDatabase.getDatabase(requireContext())
        dao = db.getKapalDAO()

        // Ambil data dari arguments
        val data = arguments?.getParcelable<KapalModel>(ARG_DATA)

        if (data != null){
            getKapalData(data.id)
        }

        // show detail
        namaKapal.text = kapal.namaKapal
        grossTone.text = kapal.grossTone
        bendera.text = kapal.bendera


        // handle on update button
        updateButton.setOnClickListener {
            val intent = Intent(context, KapalActivity::class.java)
            intent.putExtra("KAPAL", data)
            startActivity(intent)
        }

        // handle on delete button
        deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Data")
                .setMessage("Apakah anda yakin ingin menghapus data ini?")
                .setNegativeButton("Batalkan") {dialog, which -> dialog.dismiss()}
                .setPositiveButton("Konfirmasi") {dialog, which -> deletDataKapal() }
                .show()
        }
    }

    private fun deletDataKapal() {
        dao.deleteKapal(kapal)
        Toast.makeText(context, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show()
        activity?.finish()
    }

    private fun getKapalData(id: Int) {
        val kapalData = dao.getKapalById(id)
        kapal = kapalData[0]
        refreshData()
    }

    private fun refreshData() {
        namaKapal.text = kapal.namaKapal
        grossTone.text = kapal.grossTone
        bendera.text = kapal.bendera
    }

    override fun onResume() {
        super.onResume()
        getKapalData(kapal.id)
    }
}