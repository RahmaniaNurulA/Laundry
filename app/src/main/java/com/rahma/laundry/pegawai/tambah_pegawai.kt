package com.rahma.laundry.pegawai

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
import com.rahma.laundry.modeldata.ModelPegawai
import com.rahma.laundry.modeldata.ModelPelanggan

class tambah_pegawai : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etDaftar: EditText
    lateinit var etNoHP: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pegawai)
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
        tvJudul = findViewById(R.id.judulpeg)
        etNama = findViewById(R.id.etnamapeg)
        etAlamat = findViewById(R.id.etalamatpeg)
        etDaftar = findViewById(R.id.etterdaftarpeg)
        etNoHP = findViewById(R.id.etnohppeg)
        etCabang = findViewById(R.id.etcabangpeg)
        btSimpan = findViewById(R.id.btsimpanpeg)
    }
    fun cekValidasi(){
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val daftar = etDaftar.text.toString()
        val noHP = etNoHP.text.toString()
        val cabang = etCabang.text.toString()
        //validasi data
        if (nama.isEmpty()){
            etNama.error=this.getString(R.string.validasi_nama_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nama_pegawai), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()){
            etAlamat.error=this.getString(R.string.validasi_alamat_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pegawai), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (daftar.isEmpty()){
            etDaftar.error=this.getString(R.string.validasi_daftar_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_daftar_pegawai), Toast.LENGTH_SHORT).show()
            etDaftar.requestFocus()
            return
        }
        if (noHP.isEmpty()){
            etNoHP.error=this.getString(R.string.validasi_nohp_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nohp_pegawai), Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_pegawai), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        simpan()
    }
    fun simpan(){
        val pegawaiBaru = myRef.push()
        val pegawaiId = pegawaiBaru.key
        val data = ModelPegawai(
            pegawaiId.toString(),
            etNama.text.toString(),
            etAlamat.text.toString(),
            etDaftar.text.toString(),
            etNoHP.text.toString(),
            etCabang.text.toString()
        )
        pegawaiBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, this.getString(R.string.sukses_simpan_pegawai), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener() {
                Toast.makeText(this,this.getString(R.string.gagal_simpan_pegawai), Toast.LENGTH_SHORT).show()
            }
    }
}