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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class tambah_pegawai : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNoHP: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    var idPegawai:String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pegawai)
        init()
        getData()

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
        etNoHP = findViewById(R.id.etnohppeg)
        etCabang = findViewById(R.id.etcabangpeg)
        btSimpan = findViewById(R.id.btsimpanpeg)
    }

    fun getData(){
        idPegawai = intent.getStringExtra("idpeg").toString()
        val judul = intent.getStringExtra("judulpeg")
        val nama = intent.getStringExtra("tvnamapeg")
        val alamat = intent.getStringExtra("tvalamatpeg")
        val nohp = intent.getStringExtra("tvnohppeg")
        val cabang = intent.getStringExtra("tvcabangpeg")

        tvJudul.text = judul
        etNama.setText(nama)
        etAlamat.setText(alamat)
        etNoHP.setText(nohp)
        etCabang.setText(cabang)
        if (!tvJudul.text.equals(this.getString(R.string.judulpeg))){
            if (judul.equals("Edit Pegawai")){
                mati()
                btSimpan.text = "Sunting"
            }
        }else{
            hidup()
            etNama.requestFocus()
            btSimpan.text="Simpan"
        }
    }

    fun mati(){
        etNama.isEnabled=false
        etAlamat.isEnabled=false
        etNoHP.isEnabled=false
        etCabang.isEnabled=false
    }

    fun hidup(){
        etNama.isEnabled=true
        etAlamat.isEnabled=true
        etNoHP.isEnabled=true
        etCabang.isEnabled=true
    }

    fun update(){
        val pegawaiRef =  database.getReference("pegawai").child(idPegawai)
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val data = ModelPegawai(
            idPegawai,
            etNama.text.toString(),
            etAlamat.text.toString(),
            etCabang.text.toString(),
            etNoHP.text.toString(),
            currentTime
        )

        val updateData = mutableMapOf<String,Any>()
        updateData["tvnamapeg"] = data.tvnamapeg.toString()
        updateData["tvalamatpeg"] = data.tvalamatpeg.toString()
        updateData["tvterdaftarpeg"] = data.tvterdaftarpeg.toString()
        updateData["tvnohppeg"] = data.tvnohppeg.toString()
        updateData["tvcabangpeg"] = data.tvcabangpeg.toString()
        pegawaiRef.setValue(data).addOnSuccessListener {
            Toast.makeText(this@tambah_pegawai,"Data Pegawai Berhasil Diperbarui",Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this@tambah_pegawai,"Data Pegawai Gagal Diperbarui",Toast.LENGTH_SHORT).show()
        }

    }
    fun cekValidasi(){
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val cabang = etCabang.text.toString()
        val noHP = etNoHP.text.toString()
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
        if (btSimpan.text.equals("Simpan")){
            simpan()
        }else if (btSimpan.text.equals("Sunting")){
            hidup()
            etNama.requestFocus()
            btSimpan.text="Perbarui"
        }else if (btSimpan.text.equals("Perbarui")){
            update()
        }
    }
    fun simpan(){
        val pegawaiBaru = myRef.push()
        val pegawaiId = pegawaiBaru.key
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val data = ModelPegawai(
            pegawaiId.toString(),
            etNama.text.toString(),
            etAlamat.text.toString(),
            etCabang.text.toString(),
            etNoHP.text.toString(),
            currentTime
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