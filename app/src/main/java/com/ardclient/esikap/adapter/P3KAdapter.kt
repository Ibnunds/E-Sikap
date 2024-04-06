package com.ardclient.esikap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.R
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.utils.DateTimeUtils
import com.google.android.material.chip.Chip

class P3KAdapter(private val listItems: ArrayList<P3KModel>, private val listener: P3KListener) : RecyclerView.Adapter<P3KAdapter.ViewHolder>() {
    interface P3KListener {
        fun onItemClicked(p3k: P3KModel)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.card_title)
        val tvBody: TextView = itemView.findViewById(R.id.card_body)
        val chipStatus: Chip = itemView.findViewById(R.id.chipStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.document_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvTitle.text = "P3K ${item.kapal.namaKapal} #${item.id}"
        holder.tvBody.text = DateTimeUtils.formatDate(item.timestamp, "dd MMMM yyyy")
        holder.itemView.setOnClickListener {
            listener.onItemClicked(item)
        }

        if (item.isUpload){
            holder.chipStatus.text = "Sudah Diupload"
            holder.chipStatus.isChecked = true
        }else{
            holder.chipStatus.text = "Belum Diupload"
            holder.chipStatus.isChecked = false
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}