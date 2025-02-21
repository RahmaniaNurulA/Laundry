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
import com.rahma.laundry.modeldata.ModelPegawai


class DataPegawaiAdapter(
    private val ListPegawai:ArrayList<ModelPegawai>) : RecyclerView.Adapter<DataPegawaiAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddata_pegawai,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ListPegawai[position]
        holder.idpegawai.text = item.idpeg
        holder.tvnama.text = item.tvnamapeg
        holder.tvalamat.text = "Alamat = ${item.tvalamatpeg}"
        holder.tvterdaftar.text = "Terdaftar = ${item.tvterdaftarpeg}"
        holder.tvnohp.text = "No HP = ${item.tvnohppeg}"
        holder.tvcabang.text = "Cabang = ${item.tvcabangpeg}"
        holder.cvpegawai.setOnClickListener{

        }
        holder.bthubungi.setOnClickListener{

        }
        holder.btlihat.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return ListPegawai.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cvpegawai = itemView.findViewById<CardView>(R.id.cvpegawai)
        val idpegawai = itemView.findViewById<TextView>(R.id.idpeg)
        val tvnama = itemView.findViewById<TextView>(R.id.tvnamapeg)
        val tvalamat = itemView.findViewById<TextView>(R.id.tvalamatpeg)
        val tvterdaftar = itemView.findViewById<TextView>(R.id.tvterdaftarpeg)
        val tvnohp = itemView.findViewById<TextView>(R.id.tvnohppeg)
        val tvcabang = itemView.findViewById<TextView>(R.id.tvcabangpeg)
        val bthubungi = itemView.findViewById<Button>(R.id.bthubungipeg)
        val btlihat = itemView.findViewById<Button>(R.id.btlihatpeg)
    }

}