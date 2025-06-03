package com.rahma.laundry.transaksi

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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rahma.laundry.R
import com.rahma.laundry.adapter.PilihLayananAdapter
import com.rahma.laundry.adapter.PilihTambahanAdapter
import com.rahma.laundry.modeldata.ModelLayanan
import com.rahma.laundry.modeldata.ModelTambahan

class Pilih_Tambahan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var rvPilihtambahan: RecyclerView
    lateinit var tambahanList: ArrayList<ModelTambahan>
    lateinit var tvKosong: TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var sharedPref: SharedPreferences
    var idCabang: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_tambahan)
        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        tambahanList = ArrayList()
        init()
        idCabang = sharedPref.getString("idCabang", null).toString()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvPilihtambahan.layoutManager = layoutManager
        rvPilihtambahan.setHasFixedSize(true)
        tambahanList = arrayListOf()
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
        rvPilihtambahan = findViewById(R.id.rvPilihTambahan)
        tvKosong = findViewById(R.id.tvPilih_Tambahan_Kosong)
        searchView = findViewById(R.id.svtambahan)
    }

    fun getData(){
        val query = myRef.orderByChild("idCabang").equalTo(idCabang).limitToLast(100)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    tambahanList.clear()
                    for (dataSnapshot in snapshot.children){
                        val tambahan = dataSnapshot.getValue(ModelTambahan::class.java)
                        tambahanList.add(tambahan!!)
                    }
                    val adapter = PilihTambahanAdapter(tambahanList)
                    rvPilihtambahan.adapter = adapter
                    tvKosong.visibility = View.GONE
                }else{
                    tvKosong.visibility = View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Pilih_Tambahan, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun filterList(text: String?) {
        val filteredList = ArrayList<ModelTambahan>()
        for (item in tambahanList) {
            if (item.tvtambahan?.contains(text ?: "", ignoreCase = true) == true) {
                filteredList.add(item)
            }
        }

        if (filteredList.isNotEmpty()) {
            rvPilihtambahan.adapter = PilihTambahanAdapter(filteredList)
            tvKosong.visibility = View.GONE
        } else {
            rvPilihtambahan.adapter = PilihTambahanAdapter(filteredList)
            tvKosong.visibility = View.VISIBLE
        }
    }

}