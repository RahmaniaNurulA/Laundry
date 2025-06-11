package com.rahma.laundry.adapter

import ModelTransaksiTambahan
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.rahma.laundry.R

class TransaksiTambahanAdapter(
    private val transaksitambahanList: MutableList<ModelTransaksiTambahan>,
    private val showDeleteButton: Boolean = true
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

        // Sembunyikan tombol hapus jika showDeleteButton == false
        holder.bthapus.visibility = if (showDeleteButton) View.VISIBLE else View.GONE

        // Click listener untuk card
        holder.cvtampiltambahan.setOnClickListener {
            val intent = Intent()
            intent.putExtra("nomortambahan", item.idTambahan)
            intent.putExtra("namatambahan", item.namatambahan)
            intent.putExtra("hargatambahan", item.hargatambahan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }

        // Click listener untuk tombol hapus
        holder.bthapus.setOnClickListener {
            transaksitambahanList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, transaksitambahanList.size)
        }
    }

    override fun getItemCount(): Int {
        return transaksitambahanList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvtampiltambahan: View = itemView.findViewById(R.id.cvtampiltambahan)
        val idtambah: TextView = itemView.findViewById(R.id.tvnomor_tampil_tambahan)
        val tvnama: TextView = itemView.findViewById(R.id.tvtampil_namatambahan)
        val tvharga: TextView = itemView.findViewById(R.id.tvharga_tampiltambahan)
        val bthapus: ImageButton = itemView.findViewById(R.id.hapus)
    }

    // Fungsi tambahan untuk menghapus item berdasarkan ID (opsional)
    fun removeItemById(nomor: String) {
        val position = transaksitambahanList.indexOfFirst { it.idTambahan == nomor }
        if (position != -1) {
            transaksitambahanList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, transaksitambahanList.size)
        }
    }
}
