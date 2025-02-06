package com.rahma.laundry.pegawai

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rahma.laundry.R

class data_pegawai : AppCompatActivity() {
    lateinit var fabData_pegawai_Tambah: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)
        init()
        tekan()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun init(){
        fabData_pegawai_Tambah = findViewById(R.id.fabData_pegawai_Tambah)
    }

    fun tekan() {
        fabData_pegawai_Tambah.setOnClickListener {
            val intent = Intent(this, tambah_pegawai::class.java)
            startActivity(intent)
        }
    }
}