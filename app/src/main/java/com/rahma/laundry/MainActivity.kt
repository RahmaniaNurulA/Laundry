package com.rahma.laundry

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rahma.laundry.cabang.data_cabang
import com.rahma.laundry.layanan.data_layanan
import com.rahma.laundry.pegawai.data_pegawai
import com.rahma.laundry.pegawai.tambah_pegawai
import com.rahma.laundry.pelanggan.datapelanggan
import com.rahma.laundry.pelanggan.tambahPelanggan
import com.rahma.laundry.tambahan.data_tambahan
import com.rahma.laundry.transaksi.Transaksi
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var ipelanggan: ImageButton
    lateinit var ipegawai: ImageButton
    lateinit var ilayan: ImageButton
    lateinit var itambah: ImageButton
    lateinit var icabang: ImageButton
    lateinit var itransaksi: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        tekan()
        val tanggal: TextView = findViewById(R.id.tanggal)
        val haloTextView: TextView = findViewById(R.id.halo)

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val currentDate = Date()

        val greeting = when (hour) {
            in 0..11 -> this.getString(R.string.pagi)
            in 12..17 -> this.getString(R.string.siang)
            else -> this.getString(R.string.malam)
        }

        haloTextView.text = "$greeting Rahmania"
        val formattedDate = dateFormat.format(currentDate)
        tanggal.text = formattedDate
    }

    fun init() {
        ipelanggan = findViewById(R.id.ipelanggan)
        ipegawai = findViewById(R.id.ipegawai)
        ilayan = findViewById(R.id.ilayan)
        itambah = findViewById(R.id.itambah)
        icabang = findViewById(R.id.icabang)
        itransaksi = findViewById(R.id.itransaksi)
    }

    fun tekan() {
        ipelanggan.setOnClickListener {
            val intent = Intent(this, datapelanggan::class.java)
            startActivity(intent)
        }
        ipegawai.setOnClickListener {
            val intent = Intent(this, data_pegawai::class.java)
            startActivity(intent)
        }
        ilayan.setOnClickListener {
            val intent = Intent(this, data_layanan::class.java)
            startActivity(intent)
        }
        itambah.setOnClickListener {
            val intent = Intent(this, data_tambahan::class.java)
            startActivity(intent)
        }
        icabang.setOnClickListener {
            val intent = Intent(this, data_cabang::class.java)
            startActivity(intent)
        }
        itransaksi.setOnClickListener {
            val intent = Intent(this, Transaksi::class.java)
            startActivity(intent)
        }
    }
}
