package com.rahma.laundry.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rahma.laundry.R
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.database.DatabaseReference
import com.rahma.laundry.modeldata.ModelPelanggan
import com.rahma.laundry.pegawai.tambah_pegawai
import com.rahma.laundry.pelanggan.tambahPelanggan


class DataPelangganAdapter(
    private val ListPelanggan:ArrayList<ModelPelanggan>) :
    RecyclerView.Adapter<DataPelangganAdapter.ViewHolder>(){
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddata_pelanggan,parent,false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ListPelanggan[position]
        holder.idpelanggan.text = item.idpel
        holder.tvnama.text = item.tvnamapel
        holder.tvalamat.text = "Alamat = ${item.tvalamatpel}"
        holder.tvnohp.text = "No HP = ${item.tvnohppel}"
        holder.tvcabang.text = "Cabang = ${item.tvcabangpel}"
        holder.cvpelanggan.setOnClickListener{
            val intent = Intent(appContext, tambahPelanggan::class.java)
            intent.putExtra("judulpel", "Edit Pelanggan")
            intent.putExtra("idpel", item.idpel)
            intent.putExtra("tvnamapeg", item.tvnamapel)
            intent.putExtra("tvalamatpeg", item.tvalamatpel)
            intent.putExtra("tvcabangpeg", item.tvcabangpel)
            intent.putExtra("tvnohppeg", item.tvnohppel)
            appContext.startActivity(intent)
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