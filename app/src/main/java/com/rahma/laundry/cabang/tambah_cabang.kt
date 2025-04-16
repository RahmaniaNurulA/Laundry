package com.rahma.laundry.cabang

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.rahma.laundry.R
import com.rahma.laundry.modeldata.ModelCabang
import com.rahma.laundry.modeldata.ModelLayanan
import com.rahma.laundry.modeldata.ModelPegawai
import com.rahma.laundry.modeldata.ModelTambahan

class tambah_cabang : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("cabang")
    lateinit var tvJudul: TextView
    lateinit var etNamacabang: EditText
    lateinit var etkontak: EditText
    lateinit var etalamat: EditText
    lateinit var btSimpan: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_cabang)
        init()
        btSimpan.setOnClickListener{
            cekValidasi()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun init(){
        tvJudul = findViewById(R.id.judulcabang)
        etNamacabang = findViewById(R.id.etnamacabang)
        etkontak = findViewById(R.id.etkontakcabang)
        etalamat = findViewById(R.id.etalamatcabang)
        btSimpan = findViewById(R.id.btsimpancabang)
    }
    fun cekValidasi(){
        val nama = etNamacabang.text.toString()
        val kontak = etkontak.text.toString()
        val alamat = etalamat.text.toString()
        //validasi data
        if (nama.isEmpty()){
            etNamacabang.error=this.getString(R.string.validasi_nama_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_nama_tambahan), Toast.LENGTH_SHORT).show()
            etNamacabang.requestFocus()
            return
        }
        if (kontak.isEmpty()){
            etkontak.error=this.getString(R.string.validasi_harga_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_harga_tambahan), Toast.LENGTH_SHORT).show()
            etkontak.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etalamat.error = this.getString(R.string.validasi_cabang_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_tambahan), Toast.LENGTH_SHORT).show()
            etalamat.requestFocus()
            return
        }
        simpan()
    }
    fun simpan(){
        val CabangBaru = myRef.push()
        val CabangId = CabangBaru.key
        val data = ModelCabang(
            CabangId.toString(),
            etNamacabang.text.toString(),
            etkontak.text.toString(),
            etalamat.text.toString()
        )
        CabangBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, this.getString(R.string.sukses_simpan_pegawai), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener() {
                Toast.makeText(this,this.getString(R.string.gagal_simpan_pegawai), Toast.LENGTH_SHORT).show()
            }
    }
}