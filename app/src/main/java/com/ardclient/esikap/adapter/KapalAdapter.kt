package com.ardclient.esikap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.R
import com.ardclient.esikap.model.KapalModel
import com.ardclient.esikap.utils.DateTimeUtils

class KapalAdapter(
    private val listItems: ArrayList<KapalModel>,
    private val listener: KapalListener
):RecyclerView.Adapter<KapalAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.card_title)
        var tvBody: TextView = itemView.findViewById(R.id.card_body)
        var tvDate:TextView = itemView.findViewById(R.id.card_date)
    }

    interface KapalListener {
        fun onItemClicked(kapal: KapalModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.kapal_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvTitle.text = item.namaKapal
        holder.tvBody.text = item.bendera
        holder.itemView.setOnClickListener{
            listener.onItemClicked(item)
        }
        holder.tvDate.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}