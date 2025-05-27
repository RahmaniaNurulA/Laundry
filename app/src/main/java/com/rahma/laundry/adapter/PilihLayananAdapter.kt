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
import com.rahma.laundry.modeldata.ModelLayanan
import com.rahma.laundry.transaksi.Transaksi

class PilihLayananAdapter(private val layananList: ArrayList<ModelLayanan>

) : RecyclerView.Adapter<PilihLayananAdapter.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.cardpilih_layanan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = layananList[position]

        holder.idlayan.text = "[$nomor]"
        holder.tvnama.text = item.tvlayanan
        holder.tvharga.text = "Harga : ${item.tvhargalayan}"

        holder.cvlayanan.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idLayanan", item.tvidlayan)
            intent.putExtra("nama", item.tvlayanan)
            intent.putExtra("harga", item.tvhargalayan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return layananList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvlayanan: CardView = itemView.findViewById(R.id.cvpilihlayan)
        val idlayan: TextView = itemView.findViewById(R.id.tvnomor_layan)
        val tvnama: TextView = itemView.findViewById(R.id.tvpilihnamalayanan)
        val tvharga: TextView = itemView.findViewById(R.id.tvharga_pilihlayan)
    }
}
