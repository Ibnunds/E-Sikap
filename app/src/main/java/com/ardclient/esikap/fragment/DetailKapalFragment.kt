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
import com.ardclient.esikap.databinding.FragmentDetailKapalBinding
import com.ardclient.esikap.model.KapalModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DetailKapalFragment : Fragment(R.layout.fragment_detail_kapal) {
    private lateinit var kapal: KapalModel
    private lateinit var dao: KapalDAO
    private lateinit var db: KapalRoomDatabase

    // binding
    private lateinit var binding: FragmentDetailKapalBinding

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
        binding = FragmentDetailKapalBinding.bind(view)

        // init db
        db = KapalRoomDatabase.getDatabase(requireContext())
        dao = db.getKapalDAO()

        // Ambil data dari arguments
        val data = arguments?.getParcelable<KapalModel>(ARG_DATA)

        if (data != null){
            getKapalData(data.id)
        }

        // show detail
        binding.tvDetailNamaKapal.text = kapal.namaKapal
        binding.tvDetailGrossTone.text = kapal.grossTone
        binding.tvDetailBendera.text = kapal.bendera
        binding.tvDetailIMO.text = kapal.imo
        binding.tvDetailAgen.text = kapal.namaAgen
        binding.tvDetailAsal.text = kapal.negaraAsal
        binding.tvDetailKapten.text = kapal.kaptenKapal
        binding.tvDetailTipe.text = kapal.tipeKapal


        // handle on update button
        binding.updateDataButton.setOnClickListener {
            val intent = Intent(context, KapalActivity::class.java)
            intent.putExtra("KAPAL", data)
            startActivity(intent)
        }

        // handle on delete button
        binding.deleteDataButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_data_title))
                .setMessage(getString(R.string.delete_data_desc))
                .setNegativeButton(getString(R.string.cancel_button)) {dialog, which -> dialog.dismiss()}
                .setPositiveButton(getString(R.string.confirm_button)) {dialog, which -> deletDataKapal() }
                .show()
        }
    }

    private fun deletDataKapal() {
        dao.deleteKapal(kapal)
        Toast.makeText(context, getString(R.string.data_deleted_desc), Toast.LENGTH_SHORT).show()
        activity?.finish()
    }

    private fun getKapalData(id: Int) {
        val kapalData = dao.getKapalById(id)
        kapal = kapalData[0]
        refreshData()
    }

    private fun refreshData() {
        binding.tvDetailNamaKapal.text = kapal.namaKapal
        binding.tvDetailGrossTone.text = kapal.grossTone
        binding.tvDetailBendera.text = kapal.bendera
        binding.tvDetailIMO.text = kapal.imo
        binding.tvDetailAgen.text = kapal.namaAgen
        binding.tvDetailAsal.text = kapal.negaraAsal
        binding.tvDetailKapten.text = kapal.kaptenKapal
        binding.tvDetailTipe.text = kapal.tipeKapal
    }

    override fun onResume() {
        super.onResume()
        getKapalData(kapal.id)
    }
}