package com.rahma.laundry.tambahan

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
import com.rahma.laundry.adapter.DataTambahanAdapter
import com.rahma.laundry.modeldata.ModelTambahan

class data_tambahan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var rvdatatambahan: RecyclerView
    lateinit var tambahanList: ArrayList<ModelTambahan>
    lateinit var fabDatatambah_tambahan: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_tambahan)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvdatatambahan.layoutManager=layoutManager
        rvdatatambahan.setHasFixedSize(true)
        tambahanList = arrayListOf<ModelTambahan>()
        tekan()
        getData()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rvdatatambahan = findViewById(R.id.rvDataTambahan)
        fabDatatambah_tambahan = findViewById(R.id.fabTambahan_Tambah)
    }

    fun getData(){
        val query = myRef.orderByChild("idTambahan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    tambahanList.clear()
                    for(dataSnapshot in snapshot.children){
                        val tambahan = dataSnapshot.getValue(ModelTambahan::class.java)
                        tambahanList.add(tambahan!!)
                    }
                    val adapter = DataTambahanAdapter(tambahanList)
                    rvdatatambahan.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@data_tambahan, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun tekan() {
        fabDatatambah_tambahan.setOnClickListener {
            val intent = Intent(this, tambah_tambahan::class.java)
            startActivity(intent)
        }
    }
}

