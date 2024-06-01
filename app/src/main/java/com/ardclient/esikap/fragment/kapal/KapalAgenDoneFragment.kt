package com.ardclient.esikap.fragment.kapal

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardclient.esikap.DetailKapalActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.adapter.KapalAgenAdapter
import com.ardclient.esikap.databinding.FragmentKapalAgenDoneBinding
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.api.KapalListResponse
import com.ardclient.esikap.service.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KapalAgenDoneFragment : Fragment(R.layout.fragment_kapal_agen_done) {
    private lateinit var binding: FragmentKapalAgenDoneBinding
    var isInflated = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKapalAgenDoneBinding.bind(view)

        getKapalAgen("")

        with(binding){
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            searchView.editText
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.text = searchView.text
                    handleOnSearch()
                    searchView.hide()
                    false
                }

            searchBar.visibility = View.GONE

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
                if (!isInflated) {
                    searchBar.inflateMenu(R.menu.search_menu)
                    isInflated = true
                }
            }else{
                if (isInflated) {
                    searchBar.menu.clear()
                    isInflated = false
                }
            }

            getKapalAgen(keyword)
        }
    }

    private fun getKapalAgen(keyword: String) {
        val call = ApiClient.apiService.getKapalAgen("ALL", keyword)

        call.enqueue(object: Callback<ApiResponse<ArrayList<KapalListResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<ArrayList<KapalListResponse>>>,
                response: Response<ApiResponse<ArrayList<KapalListResponse>>>
            ) {
                binding.loadingView.visibility = View.GONE
                if (response.isSuccessful){
                    val data = response.body()?.data
                    if (!data.isNullOrEmpty()){
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.searchBar.visibility = View.VISIBLE
                        binding.noDataText.visibility = View.GONE
                        setupRecyclerView(data)
                    }else{
                        binding.noDataText.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                }else{
                    binding.noDataText.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<ArrayList<KapalListResponse>>>,
                t: Throwable
            ) {
                binding.loadingView.visibility = View.GONE
                binding.noDataText.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                Toast.makeText(requireContext(), t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(listData: ArrayList<KapalListResponse>) {
        with(binding){
            recyclerView.adapter = KapalAgenAdapter(listData, object : KapalAgenAdapter.OnKapalAgenListener{
                override fun onKapalClick(item: KapalListResponse) {
                    val intent = Intent(requireContext(), DetailKapalActivity::class.java)
                    val kapal = KapalModel(
                        id = item.id!!,
                        namaKapal = item.namaKapal!!,
                        namaAgen = item.namaAgen!!,
                        kaptenKapal = item.kaptenKapal!!,
                        grossTone = item.grossTone!!,
                        bendera = item.grossTone!!,
                        imo = item.imo!!,
                        negaraAsal = item.negaraAsal!!,
                        tipeKapal = item.tipeKapal!!,
                        flag = "AGEN",
                        tipeDokumen = item.tipeDokumen!!
                    )

                    intent.putExtra("KAPAL", kapal)
                    startActivity(intent)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        getKapalAgen("")
    }
}