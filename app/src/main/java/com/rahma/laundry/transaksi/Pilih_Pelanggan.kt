package com.rahma.laundry.transaksi

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rahma.laundry.R
import com.rahma.laundry.adapter.PilihPelangganAdapter
import com.rahma.laundry.modeldata.ModelPelanggan
import androidx.appcompat.widget.SearchView



class Pilih_Pelanggan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var rvPilihPelanggan: RecyclerView
    lateinit var pelangganList: ArrayList<ModelPelanggan>
    lateinit var tvKosong:TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var sharedPref: SharedPreferences
    var idCabang: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_pelanggan)
        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        pelangganList = ArrayList()
        init()
        idCabang = sharedPref.getString("idCabang", null).toString()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvPilihPelanggan.layoutManager = layoutManager
        rvPilihPelanggan.setHasFixedSize(true)
        pelangganList = arrayListOf<ModelPelanggan>()
        getData()

        // SearchView listener
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun init(){
        rvPilihPelanggan = findViewById(R.id.rvPilihPelanggan)
        tvKosong = findViewById(R.id.tvPilih_Pelanggan_Kosong)
        searchView = findViewById(R.id.svpelanggan)
    }

    fun getData(){
        val query = myRef.orderByChild("idCabang").equalTo(idCabang).limitToLast(100)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    pelangganList.clear()
                    for (dataSnapshot in snapshot.children){
                        val pegawai = dataSnapshot.getValue(ModelPelanggan::class.java)
                        pelangganList.add(pegawai!!)
                    }
                    val adapter = PilihPelangganAdapter(pelangganList)
                    rvPilihPelanggan.adapter = adapter
                    tvKosong.visibility = View.GONE
                }else{
                    tvKosong.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
               Toast.makeText(this@Pilih_Pelanggan, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }
    fun filterList(text: String?) {
        val filteredList = ArrayList<ModelPelanggan>()
        for (item in pelangganList) {
            if (item.tvnamapel?.contains(text ?: "", ignoreCase = true) == true) {
                filteredList.add(item)
            }
        }

        if (filteredList.isNotEmpty()) {
            rvPilihPelanggan.adapter = PilihPelangganAdapter(filteredList)
            tvKosong.visibility = View.GONE
        } else {
            rvPilihPelanggan.adapter = PilihPelangganAdapter(filteredList)
            tvKosong.visibility = View.VISIBLE
        }
    }
}