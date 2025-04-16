package com.rahma.laundry.cabang

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
import com.rahma.laundry.adapter.DataCabangAdapter
import com.rahma.laundry.adapter.DataTambahanAdapter
import com.rahma.laundry.modeldata.ModelCabang
import com.rahma.laundry.modeldata.ModelTambahan

class data_cabang : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("cabang")
    lateinit var rvdatacabang: RecyclerView
    lateinit var cabangList: ArrayList<ModelCabang>
    lateinit var fabDatatambah_cabang: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvdatacabang.layoutManager=layoutManager
        rvdatacabang.setHasFixedSize(true)
        cabangList = arrayListOf<ModelCabang>()
        tekan()
        getData()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rvdatacabang = findViewById(R.id.rvDataCabang)
        fabDatatambah_cabang = findViewById(R.id.fabCabang_Tambah)
    }

    fun getData(){
        val query = myRef.orderByChild("idCabang").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    cabangList.clear()
                    for(dataSnapshot in snapshot.children){
                        val cabang = dataSnapshot.getValue(ModelCabang::class.java)
                        cabangList.add(cabang!!)
                    }
                    val adapter = DataCabangAdapter(cabangList)
                    rvdatacabang.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@data_cabang, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun tekan() {
        fabDatatambah_cabang.setOnClickListener {
            val intent = Intent(this, tambah_cabang::class.java)
            startActivity(intent)
        }
    }
}

