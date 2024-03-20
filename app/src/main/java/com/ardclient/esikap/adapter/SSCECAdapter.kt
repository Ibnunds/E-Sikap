package com.ardclient.esikap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.R
import com.ardclient.esikap.model.SSCECModel

class SSCECAdapter(private val listItems: ArrayList<SSCECModel>, private val listener: SSCEClistener) : RecyclerView.Adapter<SSCECAdapter.ViewHolder>() {
    interface SSCEClistener {
        fun onItemClicked(sscec: SSCECModel)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.card_title)
        val tvBody: TextView = itemView.findViewById(R.id.card_body)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.document_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvTitle.text = "SSCEC ${item.kapal.namaKapal} - ${item.id}"
        holder.tvBody.text = "timestamp"
        holder.itemView.setOnClickListener {
            listener.onItemClicked(item)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}