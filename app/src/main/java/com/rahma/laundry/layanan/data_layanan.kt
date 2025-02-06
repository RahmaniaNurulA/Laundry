package com.rahma.laundry.layanan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rahma.laundry.R
import com.rahma.laundry.pegawai.tambah_pegawai

class data_layanan : AppCompatActivity() {
    lateinit var fabLayanan_Tambah: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_layanan)
        init()
        tekan()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init(){
        fabLayanan_Tambah = findViewById(R.id.fabLayanan_Tambah)
    }

    fun tekan() {
        fabLayanan_Tambah.setOnClickListener {
            val intent = Intent(this, tambah_layanan::class.java)
            startActivity(intent)
        }
    }
}