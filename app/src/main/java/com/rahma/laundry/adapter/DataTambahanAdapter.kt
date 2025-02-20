package com.rahma.laundry.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.rahma.laundry.R
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rahma.laundry.modeldata.ModelTambahan


class DataTambahanAdapter(
    private val ListTambahan:ArrayList<ModelTambahan>) : RecyclerView.Adapter<DataTambahanAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddata_tambahan,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ListTambahan[position]
        holder.tvidtambah.text = item.tvidtambah
        holder.tvtambahan.text = item.tvtambahan
        holder.tvharga.text = item.tvhargatambahan
        holder.tvcabang.text = item.tvcabangtambahan
        holder.cvtambahan.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return ListTambahan.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cvtambahan = itemView.findViewById<CardView>(R.id.cvtambahan)
        val tvidtambah = itemView.findViewById<TextView>(R.id.tvidtambah)
        val tvtambahan = itemView.findViewById<TextView>(R.id.tvtambahan)
        val tvharga = itemView.findViewById<TextView>(R.id.tvhargatambahan)
        val tvcabang = itemView.findViewById<TextView>(R.id.tvcabangtambahan)
    }

}