package com.rahma.laundry.adapter

import ModelTransaksiTambahan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rahma.laundry.R

class KonfirmasiTambahanAdapter(
    private val listTambahan: List<ModelTransaksiTambahan>
) : RecyclerView.Adapter<KonfirmasiTambahanAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomor: TextView = itemView.findViewById(R.id.tvnomor_tambahan)
        val tvNama: TextView = itemView.findViewById(R.id.tvpilihnamatambahan)
        val tvHarga: TextView = itemView.findViewById(R.id.tvharga_pilihtambahan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listTambahan.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTambahan[position]
        holder.tvNomor.text = "[${position + 1}]"
        holder.tvNama.text = item.namatambahan
        holder.tvHarga.text = "Rp${item.hargatambahan}"
    }
}
