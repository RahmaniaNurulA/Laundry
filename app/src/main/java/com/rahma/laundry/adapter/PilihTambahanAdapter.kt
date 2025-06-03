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
import com.rahma.laundry.modeldata.ModelTambahan
import com.rahma.laundry.transaksi.Transaksi

class PilihTambahanAdapter(private val tambahanList: ArrayList<ModelTambahan>

) : RecyclerView.Adapter<PilihTambahanAdapter.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_pilih_tambahan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = tambahanList[position]

        holder.idtambah.text = "[$nomor]"
        holder.tvnama.text = item.tvtambahan
        holder.tvharga.text = "Harga : ${item.tvhargatambahan}"

        holder.cvtambahan.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idTambahan", item.tvidtambah)
            intent.putExtra("nama", item.tvtambahan)
            intent.putExtra("harga", item.tvhargatambahan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return tambahanList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvtambahan: CardView = itemView.findViewById(R.id.cvpilihtambahan)
        val idtambah: TextView = itemView.findViewById(R.id.tvnomor_tambahan)
        val tvnama: TextView = itemView.findViewById(R.id.tvpilihnamatambahan)
        val tvharga: TextView = itemView.findViewById(R.id.tvharga_pilihtambahan)
    }
}
