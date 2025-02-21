package com.rahma.laundry.tambahan

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
import com.rahma.laundry.modeldata.ModelLayanan
import com.rahma.laundry.modeldata.ModelPegawai
import com.rahma.laundry.modeldata.ModelTambahan

class tambah_tambahan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var tvJudul: TextView
    lateinit var etNamatambahan: EditText
    lateinit var etHarga: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_tambahan)
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
        tvJudul = findViewById(R.id.judultambah)
        etNamatambahan = findViewById(R.id.etnamatambah)
        etHarga = findViewById(R.id.ethargatambah)
        etCabang = findViewById(R.id.etcabangtambah)
        btSimpan = findViewById(R.id.btsimpantambah)
    }
    fun cekValidasi(){
        val nama = etNamatambahan.text.toString()
        val harga = etHarga.text.toString()
        val cabang = etCabang.text.toString()
        //validasi data
        if (nama.isEmpty()){
            etNamatambahan.error=this.getString(R.string.validasi_nama_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_nama_tambahan), Toast.LENGTH_SHORT).show()
            etNamatambahan.requestFocus()
            return
        }
        if (harga.isEmpty()){
            etHarga.error=this.getString(R.string.validasi_harga_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_harga_tambahan), Toast.LENGTH_SHORT).show()
            etHarga.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_tambahan)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_tambahan), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        simpan()
    }
    fun simpan(){
        val TambahanBaru = myRef.push()
        val TambahanId = TambahanBaru.key
        val data = ModelTambahan(
            TambahanId.toString(),
            etNamatambahan.text.toString(),
            etHarga.text.toString(),
            etCabang.text.toString()
        )
        TambahanBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, this.getString(R.string.sukses_simpan_pegawai), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener() {
                Toast.makeText(this,this.getString(R.string.gagal_simpan_pegawai), Toast.LENGTH_SHORT).show()
            }
    }
}