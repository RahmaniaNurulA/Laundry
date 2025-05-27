package com.rahma.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.rahma.laundry.R
import com.rahma.laundry.modeldata.ModelPelanggan
import com.rahma.laundry.transaksi.Transaksi

class PilihPelangganAdapter(private val pelangganList: ArrayList<ModelPelanggan>

) : RecyclerView.Adapter<PilihPelangganAdapter.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.cardpilih_pelanggan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = pelangganList[position]

        holder.idpelanggan.text = "[$nomor]"
        holder.tvnama.text = item.tvnamapel
        holder.tvalamat.text = "Alamat : ${item.tvalamatpel}"
        holder.tvnohp.text = "No HP : ${item.tvnohppel}"

        holder.cvpelanggan.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idPelanggan", item.idpel)
            intent.putExtra("nama", item.tvnamapel)
            intent.putExtra("noHp", item.tvnohppel)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return pelangganList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvpelanggan: CardView = itemView.findViewById(R.id.cvpilihpelanggan)
        val idpelanggan: TextView = itemView.findViewById(R.id.nomorpel_pilih)
        val tvnama: TextView = itemView.findViewById(R.id.tvnamapel_pilih)
        val tvalamat: TextView = itemView.findViewById(R.id.tvalamatpel_pilih)
        val tvnohp: TextView = itemView.findViewById(R.id.tvnohppel_pilih)
    }
}
