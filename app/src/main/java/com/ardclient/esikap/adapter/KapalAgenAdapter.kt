package com.ardclient.esikap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.R
import com.ardclient.esikap.model.api.KapalListResponse
import com.ardclient.esikap.utils.DateTimeUtils

class KapalAgenAdapter(private val listItems: ArrayList<KapalListResponse>, private val listener: OnKapalAgenListener) : RecyclerView.Adapter<KapalAgenAdapter.ViewHolder>() {
    interface OnKapalAgenListener {
        fun onKapalClick(item: KapalListResponse)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvTitle: TextView = itemView.findViewById(R.id.card_title)
        var tvBody: TextView = itemView.findViewById(R.id.card_body)
        var tvDate:TextView = itemView.findViewById(R.id.card_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.kapal_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvTitle.text = item.namaKapal
        holder.tvBody.text = item.bendera
        holder.itemView.setOnClickListener {
            listener.onKapalClick(item)
        }

        val date = DateTimeUtils.formatDateTime(item.tanggalPermintaan!!)

        holder.tvDate.text = date
    }
}