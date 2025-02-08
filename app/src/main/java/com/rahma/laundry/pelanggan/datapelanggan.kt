package com.rahma.laundry.pelanggan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rahma.laundry.R
import com.rahma.laundry.adapter.DataPelangganAdapter
import com.rahma.laundry.modeldata.ModelPelanggan

class datapelanggan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var rvdatapelanggan: RecyclerView
    lateinit var pelangganList: ArrayList<ModelPelanggan>
    lateinit var fabData_Pelanggan_Tambah: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datapelanggan)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvdatapelanggan.layoutManager=layoutManager
        rvdatapelanggan.setHasFixedSize(true)
        pelangganList = arrayListOf<ModelPelanggan>()
        tekan()
        getData()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rvdatapelanggan = findViewById(R.id.rvDataPelanggan)
        fabData_Pelanggan_Tambah = findViewById(R.id.fabData_Pengguna_Tambah)
    }

    fun getData(){
        val query = myRef.orderByChild("idPelanggan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    pelangganList.clear()
                    for(dataSnapshot in snapshot.children){
                        val pelanggan = dataSnapshot.getValue(ModelPelanggan::class.java)
                        pelangganList.add(pelanggan!!)
                    }
                    val adapter = DataPelangganAdapter(pelangganList)
                    rvdatapelanggan.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@datapelanggan, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun tekan() {
        fabData_Pelanggan_Tambah.setOnClickListener {
            val intent = Intent(this, tambahPelanggan::class.java)
            startActivity(intent)
        }
    }
}