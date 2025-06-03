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
import com.rahma.laundry.modeldata.ModelTransaksiTambahan
import com.rahma.laundry.transaksi.Transaksi

class TransaksiTambahanAdapter(private val transaksitambahanList: MutableList<ModelTransaksiTambahan>

) : RecyclerView.Adapter<TransaksiTambahanAdapter.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_tampil_tambahan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = transaksitambahanList[position]

        holder.idtambah.text = "[$nomor]"
        holder.tvnama.text = item.namatambahan
        holder.tvharga.text = "Harga : ${item.hargatambahan}"

        holder.cvtampiltambahan.setOnClickListener {
            val intent = Intent()
            intent.putExtra("nomortambahan", item.nomortambahan)
            intent.putExtra("namatambahan", item.namatambahan)
            intent.putExtra("hargatambahan", item.hargatambahan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return transaksitambahanList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvtampiltambahan = itemView.findViewById<View>(R.id.cvtampiltambahan)
        val idtambah: TextView = itemView.findViewById(R.id.tvnomor_tampil_tambahan)
        val tvnama: TextView = itemView.findViewById(R.id.tvtampil_namatambahan)
        val tvharga: TextView = itemView.findViewById(R.id.tvharga_tampiltambahan)
    }
}
