package com.rahma.laundry

import ModelTransaksiTambahan
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rahma.laundry.adapter.KonfirmasiTambahanAdapter

class KonfirmasiData : AppCompatActivity() {
    private lateinit var btBatal: Button
    private lateinit var btPembayaran: Button
    private lateinit var tvNama: TextView
    private lateinit var tvNoHp: TextView
    private lateinit var tvLayanan: TextView
    private lateinit var tvHarga: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvBatal: TextView
    private lateinit var rvTambahan: RecyclerView
    private lateinit var overlayPopup: FrameLayout
    private lateinit var cvPembayaran: CardView

    // Tombol pembayaran
    private lateinit var btBayarNanti: Button
    private lateinit var btTunai: Button
    private lateinit var btQris: Button
    private lateinit var btDana: Button
    private lateinit var btGopay: Button
    private lateinit var btOvo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi_data)

        tvNama = findViewById(R.id.namapelanggan)
        tvNoHp = findViewById(R.id.nomorpelanggan)
        tvLayanan = findViewById(R.id.datalayanan)
        tvHarga = findViewById(R.id.hargalayanan)
        tvTotal = findViewById(R.id.hargatotal)
        btBatal = findViewById(R.id.btbatal)
        btPembayaran = findViewById(R.id.btpembayaran)
        rvTambahan = findViewById(R.id.rvkonfirmasiTambahan)
        overlayPopup = findViewById(R.id.overlayPopup)
        cvPembayaran = findViewById(R.id.cvpembayaran)
        tvBatal = findViewById(R.id.tvbatal)

        btBayarNanti = findViewById(R.id.btBayarnanti)
        btTunai = findViewById(R.id.btTunai)
        btQris = findViewById(R.id.btQris)
        btDana = findViewById(R.id.btDana)
        btGopay = findViewById(R.id.btGopay)
        btOvo = findViewById(R.id.btOvo)

        val nama = intent.getStringExtra("namaPelanggan") ?: ""
        val noHp = intent.getStringExtra("noHp") ?: ""
        val layanan = intent.getStringExtra("namaLayanan") ?: ""
        val hargaLayanan = intent.getStringExtra("hargaLayanan")?.replace("[^\\d]".toRegex(), "")?.toIntOrNull() ?: 0
        val tambahanList = intent.getParcelableArrayListExtra<ModelTransaksiTambahan>("listTambahan") ?: arrayListOf()

        tvNama.text = nama
        tvNoHp.text = noHp
        tvLayanan.text = layanan
        tvHarga.text = "Rp${hargaLayanan},00"

        rvTambahan.layoutManager = LinearLayoutManager(this)
        rvTambahan.adapter = KonfirmasiTambahanAdapter(tambahanList)

        var totalTambahan = 0
        for (item in tambahanList) {
            totalTambahan += item.hargatambahan.replace("[^\\d]".toRegex(), "").toIntOrNull() ?: 0
        }
        val total = hargaLayanan + totalTambahan
        tvTotal.text = "Rp${String.format("%,d", total).replace(",", ".")},00"

        btBatal.setOnClickListener {
            finish()
        }

        btPembayaran.setOnClickListener {
            overlayPopup.visibility = View.VISIBLE
            cvPembayaran.alpha = 0f
            cvPembayaran.animate().alpha(1f).setDuration(300).start()
        }

        tvBatal.setOnClickListener {
            overlayPopup.visibility = View.GONE
        }

        fun kirimDataKeCetakPembayaran(metode: String) {
            val intent = Intent(this, cetakPembayaran::class.java)
            intent.putExtra("namaPelanggan", nama)
            intent.putExtra("noHp", noHp)
            intent.putExtra("namaLayanan", layanan)
            intent.putExtra("hargaLayanan", hargaLayanan)
            intent.putExtra("totalBayar", total)
            intent.putExtra("metodePembayaran", metode)
            intent.putParcelableArrayListExtra("listTambahan", tambahanList)
            startActivity(intent)
        }

        btBayarNanti.setOnClickListener {
            kirimDataKeCetakPembayaran("Bayar Nanti")
        }
        btTunai.setOnClickListener {
            kirimDataKeCetakPembayaran("Tunai")
        }
        btQris.setOnClickListener {
            kirimDataKeCetakPembayaran("QRIS")
        }
        btDana.setOnClickListener {
            kirimDataKeCetakPembayaran("DANA")
        }
        btGopay.setOnClickListener {
            kirimDataKeCetakPembayaran("GoPay")
        }
        btOvo.setOnClickListener {
            kirimDataKeCetakPembayaran("OVO")
        }
    }
}
