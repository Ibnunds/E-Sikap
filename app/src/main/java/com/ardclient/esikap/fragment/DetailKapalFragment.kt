package com.ardclient.esikap.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.ardclient.esikap.R

class DetailKapalFragment : Fragment(R.layout.fragment_detail_kapal) {

    companion object {
        const val ARG_DATA = "KAPAL"
        fun newInstance(data:String): DetailKapalFragment {
            val fragment = DetailKapalFragment()
            val args = Bundle()
            args.putString(ARG_DATA, data)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil data dari arguments
        val data = arguments?.getString(ARG_DATA)

        Log.d("FRAGMENT_DATA", data!!)
    }
}