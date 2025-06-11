package com.rahma.laundry.transaksi

import ModelTransaksiTambahan
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.rahma.laundry.KonfirmasiData
import com.rahma.laundry.R
import com.rahma.laundry.adapter.TransaksiTambahanAdapter

class Transaksi : AppCompatActivity() {
    private lateinit var btPilihPelanggan: Button
    private lateinit var btPilihLayanan: Button
    private lateinit var btTambahan: Button
    private lateinit var btProses: Button
    private lateinit var tvPelangganNama : TextView
    private lateinit var tvPelangganNohp: TextView
    private lateinit var tvLayananNama : TextView
    private lateinit var tvLayananHarga : TextView
    private lateinit var rvLayananTambahan : RecyclerView
    private lateinit var tvnamatambahan : TextView
    private lateinit var tvhargatambahan : TextView
    private val dataList = mutableListOf<ModelTransaksiTambahan>()

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihTambahan = 3
    private val konfirmasiData = 4

    private var idPelanggan: String=""
    private var idCabang: String=""
    private var namaPelanggan: String=""
    private var noHp:String=""
    private var idLayanan:String=""
    private var namaLayanan:String=""
    private var hargaLayanan:String=""
    private var namaTambahan:String=""
    private var hargaTambahan:String=""
    private var idPegawai:String=""
    private var idTambahan:String=""
    private lateinit var sharedPref: SharedPreferences
    private lateinit var tambahanAdapter: TransaksiTambahanAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaksi)
        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", null).toString()
        idPegawai = sharedPref.getString("idPegawai", null).toString()
        init()

        FirebaseApp.initializeApp(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = false
        rvLayananTambahan.layoutManager = layoutManager
        rvLayananTambahan.setHasFixedSize(true)
        tambahanAdapter = TransaksiTambahanAdapter(dataList)
        rvLayananTambahan.adapter = tambahanAdapter

        btPilihPelanggan.setOnClickListener{
            val intent = Intent( this, Pilih_Pelanggan::class.java)
            startActivityForResult(intent,pilihPelanggan)
        }
        btPilihLayanan.setOnClickListener{
            val intent = Intent(this, Pilih_Layanan::class.java)
            startActivityForResult(intent,pilihLayanan)
        }
        btTambahan.setOnClickListener{
            val intent = Intent(this, Pilih_Tambahan::class.java)
            startActivityForResult(intent,pilihTambahan)
        }

        btProses.setOnClickListener {
            val intent = Intent(this, KonfirmasiData::class.java)

            // Data pelanggan
            intent.putExtra("namaPelanggan", namaPelanggan)
            intent.putExtra("noHp", noHp)

            // Data layanan utama
            intent.putExtra("namaLayanan", namaLayanan)
            intent.putExtra("hargaLayanan", hargaLayanan)

            // Kirim seluruh list layanan tambahan (dataList) sebagai ArrayList Parcelable
            intent.putParcelableArrayListExtra("listTambahan", ArrayList(dataList) )
                    startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun init(){
        btPilihPelanggan = findViewById(R.id.buttonpelanggan)
        btPilihLayanan = findViewById(R.id.pilihlayanan)
        btTambahan = findViewById(R.id.bttambah)
        btProses = findViewById(R.id.btproses)
        tvPelangganNama = findViewById(R.id.tvnamapel_transaksi)
        tvPelangganNohp = findViewById(R.id.tvnohp_transaksi)
        tvLayananNama = findViewById(R.id.tvnamalayan_transaksi)
        tvLayananHarga = findViewById(R.id.tvharga_transaksi)
        rvLayananTambahan = findViewById(R.id.rvtransaksiTambahan)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == pilihPelanggan){
            if  (resultCode == RESULT_OK && data != null){
                idPelanggan = data.getStringExtra("idPelanggan").toString()
                val nama = data.getStringExtra("nama")
                val nomorHP = data.getStringExtra("noHp")
                tvPelangganNama.text = "Nama Pelanggan : $nama"
                tvPelangganNohp.text = "No HP : $nomorHP"
                namaPelanggan = nama.toString()
                noHp = nomorHP.toString()
            }
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Batal Memilih Pelanggan", Toast.LENGTH_SHORT).show()
            }
        }
        if(requestCode == pilihLayanan){
            if  (resultCode == RESULT_OK && data != null){
                idLayanan = data.getStringExtra("idLayanan").toString()
                val namalayan = data.getStringExtra("nama")
                val harga = data.getStringExtra("harga")
                tvLayananNama.text = "Nama Layanan : $namalayan"
                tvLayananHarga.text = "Harga : $harga"
                namaLayanan = namalayan.toString()
                hargaLayanan = harga.toString()
            }
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Batal Memilih Layanan", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == pilihTambahan) {
            if (resultCode == RESULT_OK && data != null) {
                idTambahan = data.getStringExtra("nomor").toString()
                val namatambahan = data.getStringExtra("nama").toString()
                val harga = data.getStringExtra("harga").toString()

                val modelTambahan = ModelTransaksiTambahan(idTambahan, namatambahan, harga)
                dataList.add(modelTambahan)

                tambahanAdapter.notifyDataSetChanged()
            }
        }

    }

}