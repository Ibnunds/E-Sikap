package com.ardclient.esikap.fragment.kapal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardclient.esikap.DetailKapalActivity
import com.ardclient.esikap.KapalActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.adapter.KapalAdapter
import com.ardclient.esikap.database.kapal.KapalRoomDatabase
import com.ardclient.esikap.databinding.FragmentKapalLokalBinding
import com.ardclient.esikap.model.KapalModel

class KapalLokalFragment : Fragment(R.layout.fragment_kapal_lokal) {
    private lateinit var binding: FragmentKapalLokalBinding
    var isInflated = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKapalLokalBinding.bind(view)


        // get kapal lokal
        getSampleData()

        with(binding){
            recyclerView.layoutManager = LinearLayoutManager(requireContext())


            fab.setOnClickListener {
                val intent = Intent(context, KapalActivity::class.java)
                startActivity(intent)
            }

            searchView.editText
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.text = searchView.text
                    handleOnSearch()
                    searchView.hide()
                    false
                }

            searchBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_clear -> {
                        searchBar.text = ""
                        handleOnSearch()
                        true  // Returning true to indicate the click was handled
                    }
                    else -> false  // Return false for other items
                }
            }
        }
    }

    private fun handleOnSearch() {
        with(binding){
            val keyword = searchBar.text.toString()

            if (keyword.isNotEmpty()){
                if (!isInflated){
                    searchBar.inflateMenu(R.menu.search_menu)
                    isInflated = true
                }
            }else{
                if (isInflated){
                    searchBar.menu.clear()
                    isInflated = false
                }

            }

            val database = KapalRoomDatabase.getDatabase(requireContext())
            val dao = database.getKapalDAO()
            val listItems = arrayListOf<KapalModel>()

            listItems.addAll(dao.searchKapalByName("%${keyword}%"))

            loadingView.visibility = View.GONE

            if (listItems.size < 1){
                noDataText.visibility = View.VISIBLE
            }else{
                noDataText.visibility = View.GONE
            }

            setupRecyclerView(listItems)
        }
    }

    private fun setupRecyclerView(listData: ArrayList<KapalModel>) {
        with(binding){
            recyclerView.adapter = KapalAdapter(listData, object : KapalAdapter.KapalListener {
                override fun onItemClicked(kapal: KapalModel) {
                    val intent = Intent(requireContext(), DetailKapalActivity::class.java)
                    intent.putExtra("KAPAL", kapal)
                    startActivity(intent)
                }
            })
        }
    }

    private fun getSampleData() {
        with(binding){
            val database = KapalRoomDatabase.getDatabase(requireContext())
            val dao = database.getKapalDAO()
            val listItems = arrayListOf<KapalModel>()
            listItems.addAll(dao.getAllKapal())

            loadingView.visibility = View.GONE

            if (listItems.size < 1){
                searchBar.visibility = View.GONE
                noDataText.visibility = View.VISIBLE
            }else{
                searchBar.visibility = View.VISIBLE
                noDataText.visibility = View.GONE
            }

            setupRecyclerView(listItems)
        }

    }

    override fun onResume() {
        super.onResume()
        getSampleData()
    }
}