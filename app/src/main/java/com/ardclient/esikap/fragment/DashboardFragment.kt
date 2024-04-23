package com.ardclient.esikap.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.search.SearchBar

class DashboardFragment : Fragment(R.layout.fragment_dashboard){
    private lateinit var tvNoData: TextView
    private lateinit var loadingBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var searchBar: SearchBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = activity?.applicationContext
        loadingBar = view.findViewById(R.id.loading_view)
        tvNoData = view.findViewById(R.id.no_data_text)
        recyclerView = view.findViewById(R.id.recycler_view)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        val searchView = view.findViewById<com.google.android.material.search.SearchView>(R.id.search_view)
        searchBar = view.findViewById(R.id.search_bar)

        // rv
        recyclerView.layoutManager = LinearLayoutManager(context)

        // get room data from db
        getSampleData(context!!)

        // fab
        fab.setOnClickListener {
            val intent = Intent(context, KapalActivity::class.java)
            startActivity(intent)
        }

        // SearchBar event handling
        searchView
            .editText
            .setOnEditorActionListener { _, _, _ ->
                searchBar.text = searchView.text
                handleOnSearch(requireContext())
                searchView.hide()
                false
            }

        searchBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_clear -> {
                    searchBar.text = ""
                    handleOnSearch(requireContext())
                    true  // Returning true to indicate the click was handled
                }
                else -> false  // Return false for other items
            }
        }
    }

    private fun handleOnSearch(context: Context) {
        val keyword = searchBar.text.toString()

        if (keyword.isNotEmpty()){
            searchBar.inflateMenu(R.menu.search_menu)
        }else{
            searchBar.menu.clear()
        }

        val database = KapalRoomDatabase.getDatabase(context)
        val dao = database.getKapalDAO()
        val listItems = arrayListOf<KapalModel>()

        listItems.addAll(dao.searchKapalByName("%${keyword}%"))

        loadingBar.visibility = View.GONE

        if (listItems.size < 1){
            tvNoData.visibility = View.VISIBLE
        }else{
            tvNoData.visibility = View.GONE
        }

        setupRecyclerView(context, listItems)
    }

    private fun setupRecyclerView(context: Context,  listData: ArrayList<KapalModel>) {
        recyclerView.adapter = KapalAdapter(listData, object : KapalAdapter.KapalListener {
            override fun onItemClicked(kapal: KapalModel) {
                val intent = Intent(context, DetailKapalActivity::class.java)
                intent.putExtra("KAPAL", kapal)
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