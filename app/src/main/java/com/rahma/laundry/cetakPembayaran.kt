package com.rahma.laundry

import ModelTransaksiTambahan
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rahma.laundry.R
import com.rahma.laundry.adapter.TransaksiTambahanAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class cetakPembayaran : AppCompatActivity() {

    private lateinit var tvIdTransaksi: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvPelanggan: TextView
    private lateinit var tvKaryawan: TextView
    private lateinit var tvLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var tvTotalTambahan: TextView
    private lateinit var tvTotalBayar: TextView
    private lateinit var tvMetodePembayaran: TextView
    private lateinit var rvTambahan: RecyclerView
    private lateinit var btnWA: Button
    private lateinit var btnCetak: Button

    // Bluetooth variables
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
        private const val BLUETOOTH_PERMISSION_CODE = 102
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cetak_pembayaran)

        initViews()
        receiveAndDisplayData()
        checkPermissions()
        initBluetooth()
    }

    private fun initViews() {
        tvIdTransaksi = findViewById(R.id.IdTransaksi)
        tvTanggal = findViewById(R.id.tanggaltransaksi)
        tvPelanggan = findViewById(R.id.pelanggantransaksi)
        tvKaryawan = findViewById(R.id.karyawantransaksi)
        tvLayanan = findViewById(R.id.layanancetak)
        tvHargaLayanan = findViewById(R.id.hargalayanancetak)
        tvTotalTambahan = findViewById(R.id.totaltambahan)
        tvTotalBayar = findViewById(R.id.totalbayarcetak)
        rvTambahan = findViewById(R.id.rvtransaksiTambahan)
        btnWA = findViewById(R.id.buttonWhatsapp)
        btnCetak = findViewById(R.id.buttonCetak)
    }

    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.BLUETOOTH)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.BLUETOOTH_ADMIN)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), STORAGE_PERMISSION_CODE)
        }
    }

    private fun initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth tidak didukung pada perangkat ini", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied. Beberapa fitur mungkin tidak berfungsi.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun receiveAndDisplayData() {
        val namaPelanggan = intent.getStringExtra("namaPelanggan") ?: "-"
        val noHp = intent.getStringExtra("noHp") ?: "-"
        val namaLayanan = intent.getStringExtra("namaLayanan") ?: "-"
        val hargaLayanan = intent.getStringExtra("hargaLayanan")?.toIntOrNull() ?: 0
        val totalBayar = intent.getStringExtra("totalBayar")?.toIntOrNull() ?: 0
        val metodePembayaran = intent.getStringExtra("metodePembayaran") ?: "-"
        val listTambahan = intent.getParcelableArrayListExtra<ModelTransaksiTambahan>("listTambahan") ?: arrayListOf()

        Log.d("cetakPembayaran", "Data diterima:")
        Log.d("cetakPembayaran", "Nama: $namaPelanggan")
        Log.d("cetakPembayaran", "No HP: $noHp")

        val idTransaksi = generateTransactionId()
        val tanggal = getCurrentDate()
        val karyawan = "Admin"

        tvIdTransaksi.text = idTransaksi
        tvTanggal.text = tanggal
        tvPelanggan.text = namaPelanggan
        tvKaryawan.text = karyawan
        tvLayanan.text = namaLayanan
        tvHargaLayanan.text = "Rp${String.format("%,d", hargaLayanan).replace(",", ".")},00"

        rvTambahan.layoutManager = LinearLayoutManager(this)
        rvTambahan.adapter = TransaksiTambahanAdapter(listTambahan.toMutableList(), showDeleteButton = false)

        var totalTambahan = 0
        for (item in listTambahan) {
            val harga = item.hargatambahan.replace("[^\\d]".toRegex(), "").toIntOrNull() ?: 0
            totalTambahan += harga
        }

        tvTotalTambahan.text = "Rp${String.format("%,d", totalTambahan).replace(",", ".")},00"
        tvTotalBayar.text = "Rp${String.format("%,d", totalBayar).replace(",", ".")},00"

        setupButtons(namaPelanggan, noHp, namaLayanan, hargaLayanan, totalBayar, metodePembayaran, listTambahan, idTransaksi, tanggal, karyawan)
    }

    private fun generateTransactionId(): String {
        val timestamp = System.currentTimeMillis()
        return "TRX${timestamp.toString().takeLast(8)}"
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun setupButtons(
        namaPelanggan: String,
        noHp: String,
        namaLayanan: String,
        hargaLayanan: Int,
        totalBayar: Int,
        metodePembayaran: String,
        listTambahan: ArrayList<ModelTransaksiTambahan>,
        idTransaksi: String,
        tanggal: String,
        karyawan: String
    ) {
        btnWA.setOnClickListener {
            sendToWhatsApp(namaPelanggan, noHp, namaLayanan, hargaLayanan, totalBayar, metodePembayaran, listTambahan, idTransaksi, tanggal)
        }

        btnCetak.setOnClickListener {
            showPrintOptions(namaPelanggan, namaLayanan, hargaLayanan, totalBayar, metodePembayaran, listTambahan, idTransaksi, tanggal, karyawan)
        }
    }

    private fun sendToWhatsApp(
        namaPelanggan: String,
        noHp: String,
        namaLayanan: String,
        hargaLayanan: Int,
        totalBayar: Int,
        metodePembayaran: String,
        listTambahan: ArrayList<ModelTransaksiTambahan>,
        idTransaksi: String,
        tanggal: String
    ) {
        try {
            val message = buildWhatsAppMessage(namaPelanggan, namaLayanan, hargaLayanan, totalBayar, metodePembayaran, listTambahan, idTransaksi, tanggal)
            val cleanPhoneNumber = cleanPhoneNumber(noHp)

            // Langsung ke aplikasi WhatsApp
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.putExtra("jid", "$cleanPhoneNumber@s.whatsapp.net")
            intent.setPackage("com.whatsapp")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // Fallback jika WhatsApp tidak terinstall
                Toast.makeText(this, "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show()

                // Coba WhatsApp Business
                intent.setPackage("com.whatsapp.w4b")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    // Fallback ke browser
                    val browserIntent = Intent(Intent.ACTION_VIEW)
                    browserIntent.data = Uri.parse("https://wa.me/$cleanPhoneNumber?text=${Uri.encode(message)}")
                    startActivity(browserIntent)
                }
            }
        } catch (e: Exception) {
            Log.e("cetakPembayaran", "Error sending WhatsApp: ${e.message}")
            Toast.makeText(this, "Gagal mengirim ke WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cleanPhoneNumber(phoneNumber: String): String {
        var cleanNumber = phoneNumber.replace("[^\\d]".toRegex(), "")

        if (cleanNumber.startsWith("0")) {
            cleanNumber = "62" + cleanNumber.substring(1)
        } else if (!cleanNumber.startsWith("62")) {
            cleanNumber = "62$cleanNumber"
        }

        return cleanNumber
    }

    private fun buildWhatsAppMessage(
        namaPelanggan: String,
        namaLayanan: String,
        hargaLayanan: Int,
        totalBayar: Int,
        metodePembayaran: String,
        listTambahan: ArrayList<ModelTransaksiTambahan>,
        idTransaksi: String,
        tanggal: String
    ): String {
        val sb = StringBuilder()
        sb.append("*STRUK PEMBAYARAN LAUNDRY*\n")
        sb.append("═══════════════════════\n")
        sb.append("ID Transaksi: $idTransaksi\n")
        sb.append("Tanggal: $tanggal\n")
        sb.append("Pelanggan: $namaPelanggan\n")
        sb.append("Layanan: $namaLayanan\n")
        sb.append("Harga Layanan: Rp${String.format("%,d", hargaLayanan).replace(",", ".")},00\n")

        if (listTambahan.isNotEmpty()) {
            sb.append("\n*Layanan Tambahan:*\n")
            for (item in listTambahan) {
                sb.append("• ${item.namatambahan}: ${item.hargatambahan}\n")
            }
        }

        sb.append("\n*TOTAL BAYAR: Rp${String.format("%,d", totalBayar).replace(",", ".")},00*\n")
        sb.append("Metode Pembayaran: $metodePembayaran\n")
        sb.append("═══════════════════════\n")
        sb.append("Terima kasih telah menggunakan layanan kami! 🙏")

        return sb.toString()
    }

    private fun showPrintOptions(
        namaPelanggan: String,
        namaLayanan: String,
        hargaLayanan: Int,
        totalBayar: Int,
        metodePembayaran: String,
        listTambahan: ArrayList<ModelTransaksiTambahan>,
        idTransaksi: String,
        tanggal: String,
        karyawan: String
    ) {
        val options = arrayOf("Cetak ke Printer Bluetooth", "Simpan sebagai PDF")

        AlertDialog.Builder(this)
            .setTitle("Pilih Metode Cetak")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> printToPDF(namaPelanggan, namaLayanan, hargaLayanan, totalBayar, metodePembayaran, listTambahan, idTransaksi, tanggal, karyawan)
                }
            }
            .show()
    }

    private fun connectAndPrint(
        device: BluetoothDevice,
        namaPelanggan: String,
        namaLayanan: String,
        hargaLayanan: Int,
        totalBayar: Int,
        metodePembayaran: String,
        listTambahan: ArrayList<ModelTransaksiTambahan>,
        idTransaksi: String,
        tanggal: String,
        karyawan: String
    ) {
        executorService.execute {
            try {
                val receiptText = buildReceiptText(namaPelanggan, namaLayanan, hargaLayanan, totalBayar, metodePembayaran, listTambahan, idTransaksi, tanggal, karyawan)

                // ESC/POS commands
                val escInit = byteArrayOf(0x1B, 0x40) // Initialize printer
                val escCenter = byteArrayOf(0x1B, 0x61, 0x01) // Center alignment
                val escLeft = byteArrayOf(0x1B, 0x61, 0x00) // Left alignment
                val escBold = byteArrayOf(0x1B, 0x45, 0x01) // Bold on
                val escBoldOff = byteArrayOf(0x1B, 0x45, 0x00) // Bold off
                val escFeed = byteArrayOf(0x0A) // Line feed
                val escCut = byteArrayOf(0x1D, 0x56, 0x00) // Cut paper

                outputStream?.write(escInit)
                outputStream?.write(escCenter)
                outputStream?.write(escBold)
                outputStream?.write("STRUK PEMBAYARAN LAUNDRY\n".toByteArray())
                outputStream?.write(escBoldOff)
                outputStream?.write(escLeft)
                outputStream?.write(receiptText.toByteArray())
                outputStream?.write(escFeed)
                outputStream?.write(escFeed)
                outputStream?.write(escFeed)
                outputStream?.write(escCut)

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Struk berhasil dicetak", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("cetakPembayaran", "Error printing: ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "Gagal mencetak: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                try {
                    outputStream?.close()
                    bluetoothSocket?.close()
                } catch (e: Exception) {
                    Log.e("cetakPembayaran", "Error closing connection: ${e.message}")
                }
            }
        }
    }

    private fun buildReceiptText(
        namaPelanggan: String,
        namaLayanan: String,
        hargaLayanan: Int,
        totalBayar: Int,
        metodePembayaran: String,
        listTambahan: ArrayList<ModelTransaksiTambahan>,
        idTransaksi: String,
        tanggal: String,
        karyawan: String
    ): String {
        val sb = StringBuilder()
        sb.append("================================\n")
        sb.append("ID Transaksi: $idTransaksi\n")
        sb.append("Tanggal: $tanggal\n")
        sb.append("Karyawan: $karyawan\n")
        sb.append("================================\n")
        sb.append("Pelanggan: $namaPelanggan\n")
        sb.append("Layanan: $namaLayanan\n")
        sb.append("Harga: Rp${String.format("%,d", hargaLayanan).replace(",", ".")},00\n")
        sb.append("--------------------------------\n")

        if (listTambahan.isNotEmpty()) {
            sb.append("LAYANAN TAMBAHAN:\n")
            for (item in listTambahan) {
                sb.append("${item.namatambahan}\n")
                sb.append("${item.hargatambahan}\n")
            }
            sb.append("--------------------------------\n")
        }

        sb.append("TOTAL BAYAR:\n")
        sb.append("Rp${String.format("%,d", totalBayar).replace(",", ".")},00\n")
        sb.append("Metode: $metodePembayaran\n")
        sb.append("================================\n")
        sb.append("Terima kasih!\n")
        sb.append("================================\n")

        return sb.toString()
    }

    private fun printToPDF(
        namaPelanggan: String,
        namaLayanan: String,
        hargaLayanan: Int,
        totalBayar: Int,
        metodePembayaran: String,
        listTambahan: ArrayList<ModelTransaksiTambahan>,
        idTransaksi: String,
        tanggal: String,
        karyawan: String
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
                isAntiAlias = true
            }

            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }

            val headerPaint = Paint().apply {
                color = Color.BLACK
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }

            var yPosition = 50f
            val leftMargin = 50f
            val lineHeight = 20f

            canvas.drawText("STRUK PEMBAYARAN LAUNDRY", leftMargin, yPosition, titlePaint)
            yPosition += lineHeight * 2

            canvas.drawLine(leftMargin, yPosition, 545f, yPosition, paint)
            yPosition += lineHeight

            canvas.drawText("ID Transaksi: $idTransaksi", leftMargin, yPosition, paint)
            yPosition += lineHeight
            canvas.drawText("Tanggal: $tanggal", leftMargin, yPosition, paint)
            yPosition += lineHeight
            canvas.drawText("Karyawan: $karyawan", leftMargin, yPosition, paint)
            yPosition += lineHeight * 2

            canvas.drawText("INFORMASI PELANGGAN", leftMargin, yPosition, headerPaint)
            yPosition += lineHeight
            canvas.drawText("Nama: $namaPelanggan", leftMargin, yPosition, paint)
            yPosition += lineHeight * 2

            canvas.drawText("DETAIL LAYANAN", leftMargin, yPosition, headerPaint)
            yPosition += lineHeight
            canvas.drawText("Layanan: $namaLayanan", leftMargin, yPosition, paint)
            yPosition += lineHeight
            canvas.drawText("Harga: Rp${String.format("%,d", hargaLayanan).replace(",", ".")},00", leftMargin, yPosition, paint)
            yPosition += lineHeight * 2

            if (listTambahan.isNotEmpty()) {
                canvas.drawText("LAYANAN TAMBAHAN", leftMargin, yPosition, headerPaint)
                yPosition += lineHeight
                for (item in listTambahan) {
                    canvas.drawText("• ${item.namatambahan}: ${item.hargatambahan}", leftMargin, yPosition, paint)
                    yPosition += lineHeight
                }
                yPosition += lineHeight
            }

            canvas.drawLine(leftMargin, yPosition, 545f, yPosition, paint)
            yPosition += lineHeight
            canvas.drawText("TOTAL BAYAR: Rp${String.format("%,d", totalBayar).replace(",", ".")},00", leftMargin, yPosition, headerPaint)
            yPosition += lineHeight
            canvas.drawText("Metode Pembayaran: $metodePembayaran", leftMargin, yPosition, paint)
            yPosition += lineHeight * 2

            canvas.drawLine(leftMargin, yPosition, 545f, yPosition, paint)
            yPosition += lineHeight
            canvas.drawText("Terima kasih telah menggunakan layanan kami!", leftMargin, yPosition, paint)

            pdfDocument.finishPage(page)

            val fileName = "Struk_${idTransaksi}_${System.currentTimeMillis()}.pdf"
            val file = savePdfToStorage(pdfDocument, fileName)
            pdfDocument.close()

            if (file != null) {
                openPdf(file)
                Toast.makeText(this, "Struk berhasil dibuat dan disimpan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menyimpan struk", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("cetakPembayaran", "Error creating PDF: ${e.message}")
            Toast.makeText(this, "Gagal membuat struk: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePdfToStorage(pdfDocument: PdfDocument, fileName: String): File? {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.close()
            file
        } catch (e: IOException) {
            Log.e("cetakPembayaran", "Error saving PDF: ${e.message}")
            null
        }
    }

    private fun openPdf(file: File) {
        try {
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("cetakPembayaran", "Error opening PDF: ${e.message}")
            Toast.makeText(this, "Tidak ada aplikasi untuk membuka PDF", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            outputStream?.close()
            bluetoothSocket?.close()
            executorService.shutdown()
        } catch (e: Exception) {
            Log.e("cetakPembayaran", "Error closing resources: ${e.message}")
        }
    }
}