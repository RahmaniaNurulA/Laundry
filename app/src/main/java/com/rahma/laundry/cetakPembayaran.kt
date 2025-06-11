package com.rahma.laundry

import ModelTransaksiTambahan
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.rahma.laundry.R
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rahma.laundry.adapter.TransaksiTambahanAdapter

class cetakPembayaran : AppCompatActivity() {

    private lateinit var tvIdTransaksi: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvPelanggan: TextView
    private lateinit var tvKaryawan: TextView
    private lateinit var tvLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var tvTotalTambahan: TextView
    private lateinit var tvTotalBayar: TextView
    private lateinit var rvTambahan: RecyclerView
    private lateinit var btnWA: Button
    private lateinit var btnCetak: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cetak_pembayaran)

        // Inisialisasi komponen UI
        tvIdTransaksi = findViewById(R.id.IdTransaksi)
        tvTanggal = findViewById(R.id.tanggaltransaksi)
        tvPelanggan = findViewById(R.id.pelanggantransaksi)
        tvKaryawan = findViewById(R.id.karyawantransaksi)
        tvLayanan = findViewById(R.id.layanancetak)
        tvHargaLayanan = findViewById(R.id.hargalayanancetak)
        tvTotalTambahan = findViewById(R.id.totaltambahan)
        tvTotalBayar = findViewById(R.id.totalbayarcetak)
        rvTambahan = findViewById(R.id.rvtransaksiTambahan)
        btnWA = findViewById(R.id.buttonWhatsapp)
        btnCetak = findViewById(R.id.buttonCetak)

        // Ambil data dari intent
        val idTransaksi = intent.getStringExtra("idTransaksi") ?: "-"
        val tanggal = intent.getStringExtra("tanggal") ?: "-"
        val pelanggan = intent.getStringExtra("pelanggan") ?: "-"
        val karyawan = intent.getStringExtra("karyawan") ?: "-"
        val layanan = intent.getStringExtra("layanan") ?: "-"
        val hargaLayanan = intent.getIntExtra("hargaLayanan", 0)
        val listTambahan = intent.getParcelableArrayListExtra<ModelTransaksiTambahan>("listTambahan") ?: arrayListOf()

        // Set ke UI
        tvIdTransaksi.text = idTransaksi
        tvTanggal.text = tanggal
        tvPelanggan.text = pelanggan
        tvKaryawan.text = karyawan
        tvLayanan.text = layanan
        tvHargaLayanan.text = "Rp${String.format("%,d", hargaLayanan).replace(",", ".")},00"

        // Setup RecyclerView (tanpa tombol hapus)
        rvTambahan.layoutManager = LinearLayoutManager(this)
        rvTambahan.adapter = TransaksiTambahanAdapter(listTambahan.toMutableList(), showDeleteButton = false)

        // Hitung total tambahan
        var totalTambahan = 0
        for (item in listTambahan) {
            val harga = item.hargatambahan.replace("[^\\d]".toRegex(), "").toIntOrNull() ?: 0
            totalTambahan += harga
        }

        val totalBayar = hargaLayanan + totalTambahan

        tvTotalTambahan.text = "Rp${String.format("%,d", totalTambahan).replace(",", ".")},00"
        tvTotalBayar.text = "Rp${String.format("%,d", totalBayar).replace(",", ".")},00"

        // Tombol WhatsApp
        btnWA.setOnClickListener {
            // Implementasi kirim ke WhatsApp
        }

        // Tombol Cetak
        btnCetak.setOnClickListener {
            // Implementasi cetak
        }
    }
}
