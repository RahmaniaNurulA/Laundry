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
import com.rahma.laundry.modeldata.ModelCabang
import com.rahma.laundry.modeldata.ModelTambahan


class DataCabangAdapter(
    private val ListCabang:ArrayList<ModelCabang>) : RecyclerView.Adapter<DataCabangAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddata_cabang,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ListCabang[position]
        holder.tvidcabang.text = item.tvidcabang
        holder.tvcabang.text = item.tvnamacabangcv
        holder.tvkontak.text = "No = ${item.tvkontakcabangcv}"
        holder.tvalamat.text = "Alamat = ${item.tvalamatcabangcv}"
        holder.cvcabang.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return ListCabang.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cvcabang = itemView.findViewById<CardView>(R.id.cvdatacabang)
        val tvidcabang = itemView.findViewById<TextView>(R.id.tvidcabang)
        val tvcabang = itemView.findViewById<TextView>(R.id.tvnamacabangcv)
        val tvkontak = itemView.findViewById<TextView>(R.id.tvkontakcabangcv)
        val tvalamat = itemView.findViewById<TextView>(R.id.tvalamatcabangcv)
    }

}