package com.ardclient.esikap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.R
import com.ardclient.esikap.model.Sample

class SampleAdapter(
    private val listItems: ArrayList<Sample>
) : RecyclerView.Adapter<SampleAdapter.SampleViewHolder>(){

    class SampleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.card_title)
        var tvBody: TextView = itemView.findViewById(R.id.card_body)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_list, parent, false)
        return SampleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvTitle.text = item.title
        holder.tvBody.text = item.message
    }
}