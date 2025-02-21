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
import com.rahma.laundry.modeldata.ModelLayanan
import com.rahma.laundry.modeldata.ModelPegawai


class DataLayananAdapter(
    private val ListLayanan:ArrayList<ModelLayanan>) : RecyclerView.Adapter<DataLayananAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddata_layanan,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ListLayanan[position]
        holder.idlayanan.text = item.tvidlayan
        holder.tvnamalayanan.text = item.tvlayanan
        holder.tvharga.text = "Harga = ${item.tvhargalayan}"
        holder.tvcabang.text = "Cabang = ${item.tvcabanglayan}"
        holder.cvlayanan.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return ListLayanan.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cvlayanan = itemView.findViewById<CardView>(R.id.cvlayanan)
        val idlayanan = itemView.findViewById<TextView>(R.id.tvidlayan)
        val tvnamalayanan = itemView.findViewById<TextView>(R.id.tvlayanan)
        val tvharga = itemView.findViewById<TextView>(R.id.tvhargalayan)
        val tvcabang = itemView.findViewById<TextView>(R.id.tvcabanglayan)
    }

}