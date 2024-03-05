package com.ardclient.esikap

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.adapter.SampleAdapter
import com.ardclient.esikap.database.sample.SampleRoomDatabase
import com.ardclient.esikap.model.Sample
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardFragment : Fragment(R.layout.fragment_dashboard){
    private lateinit var tvNoData: TextView
    private lateinit var loadingBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        val context = activity?.applicationContext
        loadingBar = view.findViewById(R.id.loading_view)
        tvNoData = view.findViewById(R.id.no_data_text)
        recyclerView = view.findViewById(R.id.recycler_view)

        tvNoData.visibility = View.INVISIBLE

        val listData = getSampleData(context!!)
        setupRecyclerView(context, view, listData)


    }

    private fun setupRecyclerView(context: Context, view: View, listData: ArrayList<Sample>) {

        recyclerView.apply {
            adapter = SampleAdapter(listData)
            layoutManager = LinearLayoutManager(context)
        }

    }

    private fun getSampleData(context: Context): ArrayList<Sample> {
        val database = SampleRoomDatabase.getDatabase(context)
        val dao = database.getSampleDao()
        val listItems = arrayListOf<Sample>()
        listItems.addAll(dao.getAllSample())

        loadingBar.visibility = View.INVISIBLE

        if (listItems.size < 1){
            tvNoData.visibility = View.VISIBLE
        }

        return listItems
    }
}