package com.ardclient.esikap.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.DetailKapalActivity
import com.ardclient.esikap.KapalActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.adapter.KapalAdapter
import com.ardclient.esikap.database.kapal.KapalRoomDatabase
import com.ardclient.esikap.model.KapalModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardFragment : Fragment(R.layout.fragment_dashboard){
    private lateinit var tvNoData: TextView
    private lateinit var loadingBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = activity?.applicationContext
        loadingBar = view.findViewById(R.id.loading_view)
        tvNoData = view.findViewById(R.id.no_data_text)
        recyclerView = view.findViewById(R.id.recycler_view)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        // rv
        recyclerView.layoutManager = LinearLayoutManager(context)


        // init behavior
        tvNoData.visibility = View.GONE

        // get room data from db
        getSampleData(context!!)

        // fab
        fab.setOnClickListener {
            val intent = Intent(context, KapalActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView(context: Context,  listData: ArrayList<KapalModel>) {
        recyclerView.adapter = KapalAdapter(listData, object : KapalAdapter.KapalListener {
            override fun onItemClicked(kapal: KapalModel) {
                val intent = Intent(context, DetailKapalActivity::class.java)
                intent.putExtra("DETAIL_KAPAL", kapal)
                startActivity(intent)
            }
        })
    }

    private fun getSampleData(context: Context) {
        val database = KapalRoomDatabase.getDatabase(context)
        val dao = database.getKapalDAO()
        val listItems = arrayListOf<KapalModel>()
        listItems.addAll(dao.getAllKapal())

        loadingBar.visibility = View.GONE

        if (listItems.size < 1){
            tvNoData.visibility = View.VISIBLE
        }else{
            tvNoData.visibility = View.GONE
        }

        setupRecyclerView(context, listItems)
    }

    override fun onResume() {
        super.onResume()
        val context = activity?.applicationContext
        getSampleData(context!!)
    }
}