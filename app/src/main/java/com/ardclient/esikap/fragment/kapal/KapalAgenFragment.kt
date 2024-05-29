package com.ardclient.esikap.fragment.kapal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardclient.esikap.DetailKapalActivity
import com.ardclient.esikap.R
import com.ardclient.esikap.adapter.KapalAdapter
import com.ardclient.esikap.adapter.KapalAgenAdapter
import com.ardclient.esikap.databinding.FragmentKapalAgenBinding
import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.model.api.KapalListResponse
import com.ardclient.esikap.service.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KapalAgenFragment : Fragment(R.layout.fragment_kapal_agen) {
    private lateinit var binding: FragmentKapalAgenBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKapalAgenBinding.bind(view)

        getKapalAgen()

        with(binding){
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            searchView.editText
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.text = searchView.text
                    //handleOnSearch()
                    searchView.hide()
                    false
                }

            searchBar.visibility = View.GONE

            searchBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_clear -> {
                        searchBar.text = ""
                        //handleOnSearch()
                        true  // Returning true to indicate the click was handled
                    }
                    else -> false  // Return false for other items
                }
            }
        }
    }

    private fun getKapalAgen() {
        val call = ApiClient.apiService.getKapalAgen()

        call.enqueue(object: Callback<ApiResponse<ArrayList<KapalListResponse>>>{
            override fun onResponse(
                call: Call<ApiResponse<ArrayList<KapalListResponse>>>,
                response: Response<ApiResponse<ArrayList<KapalListResponse>>>
            ) {
                binding.loadingView.visibility = View.GONE
                if (response.isSuccessful){
                    val data = response.body()?.data
                    if (!data.isNullOrEmpty()){
                        binding.searchBar.visibility = View.VISIBLE
                        binding.noDataText.visibility = View.GONE
                        setupRecyclerView(data)
                    }else{
                        binding.noDataText.visibility = View.VISIBLE
                    }
                }else{
                    binding.noDataText.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<ArrayList<KapalListResponse>>>,
                t: Throwable
            ) {
                binding.loadingView.visibility = View.GONE
                binding.noDataText.visibility = View.VISIBLE
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
        getKapalAgen()
    }
}