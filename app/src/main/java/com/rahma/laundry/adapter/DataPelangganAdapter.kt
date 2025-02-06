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
import com.rahma.laundry.modeldata.ModelPelanggan


class DataPelangganAdapter(
    private val ListPelanggan:ArrayList<ModelPelanggan>) : RecyclerView.Adapter<DataPelangganAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddata_pelanggan,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ListPelanggan[position]
        holder.idpelanggan.text = item.idpel
        holder.tvnama.text = item.tvnamapel
        holder.tvalamat.text = item.tvalamatpel
        holder.tvnohp.text = item.tvnohppel
        holder.tvcabang.text = item.tvcabangpel
        holder.cvpelanggan.setOnClickListener{

        }
        holder.bthubungi.setOnClickListener{

        }
        holder.btlihat.setOnClickListener{

        }
    }

    override fun getItemCount(): Int {
        return ListPelanggan.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val cvpelanggan = itemView.findViewById<CardView>(R.id.cvpelanggan)
        val idpelanggan = itemView.findViewById<TextView>(R.id.idpel)
        val tvnama = itemView.findViewById<TextView>(R.id.tvnamapel)
        val tvalamat = itemView.findViewById<TextView>(R.id.tvalamatpel)
        val tvnohp = itemView.findViewById<TextView>(R.id.tvnohppel)
        val tvcabang = itemView.findViewById<TextView>(R.id.tvcabangpel)
        val bthubungi = itemView.findViewById<Button>(R.id.bthubungipel)
        val btlihat = itemView.findViewById<Button>(R.id.btlihatpel)
    }

}