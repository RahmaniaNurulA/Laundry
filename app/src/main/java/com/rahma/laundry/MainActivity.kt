package com.rahma.laundry

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tanggal: TextView = findViewById(R.id.tanggal)
        val haloTextView: TextView = findViewById(R.id.halo)

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val currentDate = Date()

        val greeting = when (hour) {
            in 0..11 -> "Selamat Pagi"
            in 12..17 -> "Selamat Siang"
            else -> "Selamat Malam"
        }

        haloTextView.text = "$greeting Rahmania"
        val formattedDate = dateFormat.format(currentDate)
        tanggal.text = formattedDate
    }
}
