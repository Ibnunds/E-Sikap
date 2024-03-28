package com.ardclient.esikap.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.ardclient.esikap.R
import com.ardclient.esikap.model.reusable.DokumenKapalListModel
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso

class DokumenInputAdapter(private val listData: ArrayList<DokumenKapalListModel>, private val listener: UploadButtonListener?) : RecyclerView.Adapter<DokumenInputAdapter.ViewHolder>() {

    interface UploadButtonListener{
        fun onUploadButton(key: String)
        fun onRadioChangedListener(key: String, radioVal: String)

        fun onNoteChanged(key: String, inputText: CharSequence?)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.document_input_list, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val inNote: TextInputLayout = itemView.findViewById(R.id.in_note)
        val radio: RadioGroup = itemView.findViewById(R.id.radio)
        val selectButton: Button = itemView.findViewById(R.id.btnSelectDoc)
        val prevDoc : ImageView = itemView.findViewById(R.id.prevDoc)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.tvTitle.text = item.title

        // handle existing

        if (!item.checkedVal.isNullOrEmpty()){
            if (item.checkedVal == "Ada"){
                holder.radio.check(R.id.radio_true)
                holder.selectButton.visibility = View.VISIBLE
            }else{
                holder.radio.check(R.id.radio_false)
            }
        }

        // note layout
        if (item.needNote) holder.inNote.visibility = View.VISIBLE else holder.inNote.visibility = View.GONE

        // radio and doc
        holder.radio.setOnCheckedChangeListener { _, checkedId ->
            val radioLabel = if (checkedId == R.id.radio_true) "Ada" else "Tidak ada"
            listener?.onRadioChangedListener(item.key, radioLabel)
            if (checkedId == R.id.radio_true) {
                holder.selectButton.visibility = View.VISIBLE
            } else {
                holder.prevDoc.visibility = View.GONE
                holder.selectButton.visibility = View.GONE
            }
        }

        holder.selectButton.setOnClickListener {
            listener?.onUploadButton(item.key)
        }

        // preview doc
        if (!item.docImage.isNullOrEmpty()){
            holder.prevDoc.visibility = View.VISIBLE
            holder.selectButton.text = "Update Dokumen"
            Picasso.get().load(item.docImage).fit().into(holder.prevDoc)
        }

        // note
        holder.inNote.editText?.doOnTextChanged {inputText, _, _, _ ->
            listener?.onNoteChanged(item.key, inputText)
        }


        if (!item.note.isNullOrBlank()){
            holder.inNote.editText?.setText(item.note)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}