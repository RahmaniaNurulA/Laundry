package com.rahma.laundry

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Method
import java.util.UUID

class BluetoothPrinter(private val context: Context) {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var connectionJob: Job? = null
    private var isConnectionInProgress = false

    // Multiple UUID untuk kompatibilitas lebih baik
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val ALTERNATIVE_UUID = UUID.fromString("00001102-0000-1000-8000-00805F9B34FB")
    private val THIRD_UUID = UUID.fromString("00001103-0000-1000-8000-00805F9B34FB")

    companion object {
        private const val TAG = "BluetoothPrinter"
        private const val CONNECTION_TIMEOUT_MS = 15000L
        private const val SOCKET_TIMEOUT_MS = 10000L
    }

    init {
        try {
            bluetoothAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                bluetoothManager.adapter
            } else {
                @Suppress("DEPRECATION")
                BluetoothAdapter.getDefaultAdapter()
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error initializing BluetoothAdapter", e)
        }
    }

    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null
    }

    fun isBluetoothEnabled(): Boolean {
        return try {
            bluetoothAdapter?.isEnabled == true
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error checking Bluetooth status", e)
            false
        }
    }

    fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            val bluetoothPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED

            val bluetoothAdminPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED

            bluetoothPermission && bluetoothAdminPermission
        }
    }

    fun getPairedDevices(): List<BluetoothDevice> {
        return try {
            if (!checkPermissions()) {
                android.util.Log.w(TAG, "Missing Bluetooth permissions")
                emptyList()
            } else {
                bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
            }
        } catch (e: SecurityException) {
            android.util.Log.e(TAG, "Security exception getting paired devices", e)
            emptyList()
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error getting paired devices", e)
            emptyList()
        }
    }

    // Method untuk connect menggunakan MAC address
    fun connectToPrinter(macAddress: String, callback: (Boolean, String) -> Unit) {
        if (isConnectionInProgress) {
            callback(false, "Koneksi sedang berlangsung, harap tunggu")
            return
        }

        if (!checkPermissions()) {
            callback(false, "Permission Bluetooth tidak diberikan")
            return
        }

        if (!isBluetoothEnabled()) {
            callback(false, "Bluetooth tidak aktif")
            return
        }

        // Validasi format MAC address
        if (!isValidMacAddress(macAddress)) {
            callback(false, "Format MAC address tidak valid: $macAddress")
            return
        }

        try {
            val device = bluetoothAdapter?.getRemoteDevice(macAddress)
            device?.let {
                connectToPrinter(it, callback)
            } ?: callback(false, "Tidak dapat membuat device dari MAC address: $macAddress")
        } catch (e: IllegalArgumentException) {
            callback(false, "MAC address tidak valid: $macAddress - ${e.message}")
        } catch (e: Exception) {
            callback(false, "Error saat membuat device dari MAC: ${e.message}")
        }
    }

    // Overload method untuk tetap support BluetoothDevice
    fun connectToPrinter(device: BluetoothDevice, callback: (Boolean, String) -> Unit) {
        if (isConnectionInProgress) {
            callback(false, "Koneksi sedang berlangsung, harap tunggu")
            return
        }

        if (!checkPermissions()) {
            callback(false, "Permission Bluetooth tidak diberikan")
            return
        }

        if (!isBluetoothEnabled()) {
            callback(false, "Bluetooth tidak aktif")
            return
        }

        // Cancel koneksi sebelumnya jika ada
        connectionJob?.cancel()
        isConnectionInProgress = true

        connectionJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                // Tutup koneksi sebelumnya dengan delay
                disconnect()
                delay(1000) // Wait untuk memastikan socket benar-benar tertutup

                // Hentikan discovery untuk menghindari interferensi
                try {
                    if (checkPermissions()) {
                        bluetoothAdapter?.cancelDiscovery()
                        delay(500) // Wait untuk discovery berhenti
                    }
                } catch (e: SecurityException) {
                    android.util.Log.w(TAG, "Permission denied for cancelDiscovery", e)
                }

                android.util.Log.d(TAG, "Attempting connection to ${getDeviceName(device)}")

                var connected = false
                var lastError = ""
                val connectionMethods = listOf(
                    ConnectionMethod("Standard Socket", 0),
                    ConnectionMethod("Reflection Method (Channel 1)", 1),
                    ConnectionMethod("Reflection Method (Channel 4)", 2),
                    ConnectionMethod("Alternative UUID", 3),
                    ConnectionMethod("Third UUID", 4)
                )

                for (method in connectionMethods) {
                    if (connected || !isActive) break

                    try {
                        android.util.Log.d(TAG, "Trying ${method.name}...")

                        bluetoothSocket = when (method.type) {
                            0 -> { // Standard socket
                                if (checkPermissions()) {
                                    device.createRfcommSocketToServiceRecord(SPP_UUID)
                                } else null
                            }
                            1 -> { // Reflection method 1
                                if (checkPermissions()) {
                                    val createMethod: Method = device.javaClass.getMethod(
                                        "createRfcommSocket",
                                        Int::class.javaPrimitiveType
                                    )
                                    createMethod.invoke(device, 1) as BluetoothSocket
                                } else null
                            }
                            2 -> { // Reflection method 2 - different channel
                                if (checkPermissions()) {
                                    val createMethod: Method = device.javaClass.getMethod(
                                        "createRfcommSocket",
                                        Int::class.javaPrimitiveType
                                    )
                                    createMethod.invoke(device, 4) as BluetoothSocket
                                } else null
                            }
                            3 -> { // Alternative UUID
                                if (checkPermissions()) {
                                    device.createRfcommSocketToServiceRecord(ALTERNATIVE_UUID)
                                } else null
                            }
                            4 -> { // Third UUID
                                if (checkPermissions()) {
                                    device.createRfcommSocketToServiceRecord(THIRD_UUID)
                                } else null
                            }
                            else -> null
                        }

                        // Attempt connection dengan timeout handling
                        bluetoothSocket?.let { socket ->
                            try {
                                // Koneksi dengan timeout menggunakan coroutine
                                withTimeout(SOCKET_TIMEOUT_MS) {
                                    socket.connect()
                                }

                                // Verify connection
                                if (socket.isConnected) {
                                    outputStream = socket.outputStream
                                    inputStream = socket.inputStream

                                    // Test dengan mengirim data sederhana
                                    val testSuccess = withContext(Dispatchers.IO) {
                                        try {
                                            outputStream?.write(ESCPOSCommands.INIT)
                                            outputStream?.flush()
                                            delay(300) // Wait for response
                                            true
                                        } catch (e: Exception) {
                                            android.util.Log.w(TAG, "Test write failed: ${e.message}")
                                            false
                                        }
                                    }

                                    if (testSuccess) {
                                        connected = true
                                        android.util.Log.d(TAG, "${method.name} connected successfully")
                                    } else {
                                        throw IOException("Test write failed - printer may not be responding")
                                    }
                                } else {
                                    throw IOException("Socket not connected after connect() call")
                                }
                            } catch (e: TimeoutCancellationException) {
                                lastError = "${method.name}: Connection timeout"
                                android.util.Log.w(TAG, lastError)
                                safeCloseSocket()
                            } catch (e: IOException) {
                                lastError = "${method.name}: ${e.message}"
                                android.util.Log.w(TAG, lastError)
                                safeCloseSocket()
                            }
                        } ?: run {
                            lastError = "${method.name}: Failed to create socket"
                        }

                        if (!connected) {
                            delay(500) // Wait before next attempt
                        }

                    } catch (e: SecurityException) {
                        lastError = "${method.name}: Security exception - ${e.message}"
                        android.util.Log.w(TAG, lastError)
                        safeCloseSocket()
                    } catch (e: Exception) {
                        lastError = "${method.name}: ${e.message}"
                        android.util.Log.w(TAG, lastError)
                        safeCloseSocket()
                    }
                }

                // Callback dengan hasil
                withContext(Dispatchers.Main) {
                    if (connected) {
                        callback(true, "Berhasil terhubung ke ${getDeviceName(device)}")
                    } else {
                        callback(false, "Semua metode koneksi gagal. Error terakhir: $lastError")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.util.Log.e(TAG, "Unexpected error during connection", e)
                    callback(false, "Error tidak terduga: ${e.message}")
                }
            } finally {
                isConnectionInProgress = false
                if (!isConnected()) {
                    safeCloseSocket()
                }
            }
        }
    }

    // Data class untuk connection method
    private data class ConnectionMethod(val name: String, val type: Int)

    // Safe socket closing method
    private fun safeCloseSocket() {
        try {
            inputStream?.close()
        } catch (e: Exception) {
            android.util.Log.w(TAG, "Error closing input stream", e)
        }

        try {
            outputStream?.close()
        } catch (e: Exception) {
            android.util.Log.w(TAG, "Error closing output stream", e)
        }

        try {
            bluetoothSocket?.close()
        } catch (e: Exception) {
            android.util.Log.w(TAG, "Error closing bluetooth socket", e)
        }

        inputStream = null
        outputStream = null
        bluetoothSocket = null
    }

    // Method untuk koneksi dengan timeout menggunakan MAC address
    fun connectWithTimeout(
        macAddress: String,
        timeoutMs: Long = CONNECTION_TIMEOUT_MS,
        callback: (Boolean, String) -> Unit
    ) {
        val timeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(timeoutMs)
            connectionJob?.let { job ->
                if (job.isActive) {
                    job.cancel()
                    disconnect()
                    callback(false, "Koneksi timeout setelah ${timeoutMs/1000} detik")
                }
            }
        }

        connectToPrinter(macAddress) { success, message ->
            timeoutJob.cancel() // Cancel timeout jika koneksi berhasil/gagal
            callback(success, message)
        }
    }

    // Overload untuk BluetoothDevice
    fun connectWithTimeout(
        device: BluetoothDevice,
        timeoutMs: Long = CONNECTION_TIMEOUT_MS,
        callback: (Boolean, String) -> Unit
    ) {
        val timeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(timeoutMs)
            connectionJob?.let { job ->
                if (job.isActive) {
                    job.cancel()
                    disconnect()
                    callback(false, "Koneksi timeout setelah ${timeoutMs/1000} detik")
                }
            }
        }

        connectToPrinter(device) { success, message ->
            timeoutJob.cancel() // Cancel timeout jika koneksi berhasil/gagal
            callback(success, message)
        }
    }

    // Utility method untuk validasi MAC address
    private fun isValidMacAddress(macAddress: String): Boolean {
        return try {
            val macPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$"
            macAddress.matches(macPattern.toRegex())
        } catch (e: Exception) {
            false
        }
    }

    // Method untuk mendapatkan device dari MAC address
    fun getDeviceFromMac(macAddress: String): BluetoothDevice? {
        return try {
            if (!checkPermissions()) {
                android.util.Log.w(TAG, "Missing permissions to get device")
                return null
            }

            if (!isValidMacAddress(macAddress)) {
                android.util.Log.w(TAG, "Invalid MAC address format: $macAddress")
                return null
            }

            bluetoothAdapter?.getRemoteDevice(macAddress)
        } catch (e: IllegalArgumentException) {
            android.util.Log.e(TAG, "Invalid MAC address: $macAddress", e)
            null
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error getting device from MAC", e)
            null
        }
    }

    // Method untuk mencari device berdasarkan nama atau MAC
    fun findDevice(identifier: String): BluetoothDevice? {
        return try {
            if (!checkPermissions()) {
                android.util.Log.w(TAG, "Missing permissions to find device")
                return null
            }

            val pairedDevices = getPairedDevices()

            // Cari berdasarkan MAC address terlebih dahulu
            if (isValidMacAddress(identifier)) {
                return pairedDevices.find { it.address.equals(identifier, ignoreCase = true) }
                    ?: bluetoothAdapter?.getRemoteDevice(identifier)
            }

            // Jika bukan MAC address, cari berdasarkan nama
            pairedDevices.find { device ->
                try {
                    device.name?.contains(identifier, ignoreCase = true) == true
                } catch (e: SecurityException) {
                    false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error finding device", e)
            null
        }
    }


    // Data class untuk informasi device
    data class DeviceInfo(
        val device: BluetoothDevice,
        val name: String,
        val address: String,
        val bondState: Int,
        val type: Int
    ) {
        fun getTypeString(): String {
            return when (type) {
                BluetoothDevice.DEVICE_TYPE_CLASSIC -> "Classic"
                BluetoothDevice.DEVICE_TYPE_LE -> "Low Energy"
                BluetoothDevice.DEVICE_TYPE_DUAL -> "Dual Mode"
                else -> "Unknown"
            }
        }

        fun getBondStateString(): String {
            return when (bondState) {
                BluetoothDevice.BOND_BONDED -> "Paired"
                BluetoothDevice.BOND_BONDING -> "Pairing"
                BluetoothDevice.BOND_NONE -> "Not Paired"
                else -> "Unknown"
            }
        }
    }

    private fun getDeviceName(device: BluetoothDevice): String {
        return try {
            if (checkPermissions()) {
                device.name ?: device.address ?: "Unknown Device"
            } else {
                device.address ?: "Unknown Device"
            }
        } catch (e: SecurityException) {
            android.util.Log.w(TAG, "Security exception getting device name", e)
            device.address ?: "Unknown Device"
        } catch (e: Exception) {
            android.util.Log.w(TAG, "Error getting device name", e)
            "Unknown Device"
        }
    }

    fun disconnect() {
        connectionJob?.cancel()
        isConnectionInProgress = false
        safeCloseSocket()
        android.util.Log.d(TAG, "Disconnected from printer")
    }

    // IMPROVED PRINT METHOD dengan connection check
    fun printText(text: String, maxRetries: Int = 2): Boolean {
        if (!isConnected()) {
            android.util.Log.w(TAG, "Cannot print - not connected")
            return false
        }

        var attempt = 0
        while (attempt < maxRetries) {
            try {
                val stream = outputStream
                if (stream != null) {
                    stream.write(text.toByteArray(Charsets.UTF_8))
                    stream.flush()
                    android.util.Log.d(TAG, "Text sent successfully on attempt ${attempt + 1}")
                    return true
                }
            } catch (e: IOException) {
                android.util.Log.e(TAG, "Error printing text (attempt ${attempt + 1}): ${e.message}")
                // Jika IOException, kemungkinan koneksi terputus
                if (e.message?.contains("socket", true) == true ||
                    e.message?.contains("closed", true) == true ||
                    e.message?.contains("broken pipe", true) == true) {
                    android.util.Log.w(TAG, "Socket appears to be closed, stopping retries")
                    break
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Unexpected error printing text (attempt ${attempt + 1}): ${e.message}")
            }

            attempt++
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(1000) // Wait before retry
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }
        return false
    }

    // IMPROVED SEND COMMAND dengan better error handling
    fun sendCommand(command: ByteArray, maxRetries: Int = 2): Boolean {
        if (!isConnected()) {
            android.util.Log.w(TAG, "Cannot send command - not connected")
            return false
        }

        var attempt = 0
        while (attempt < maxRetries) {
            try {
                val stream = outputStream
                if (stream != null) {
                    stream.write(command)
                    stream.flush()
                    android.util.Log.d(TAG, "Command sent successfully on attempt ${attempt + 1}")
                    return true
                }
            } catch (e: IOException) {
                android.util.Log.e(TAG, "Error sending command (attempt ${attempt + 1}): ${e.message}")
                // Jika IOException, kemungkinan koneksi terputus
                if (e.message?.contains("socket", true) == true ||
                    e.message?.contains("closed", true) == true ||
                    e.message?.contains("broken pipe", true) == true) {
                    android.util.Log.w(TAG, "Socket appears to be closed, stopping retries")
                    break
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Unexpected error sending command (attempt ${attempt + 1}): ${e.message}")
            }

            attempt++
            if (attempt < maxRetries) {
                try {
                    Thread.sleep(500) // Wait before retry
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }
        return false
    }

    fun isConnected(): Boolean {
        return try {
            val socket = bluetoothSocket
            val stream = outputStream
            socket?.isConnected == true && stream != null && !isConnectionInProgress
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error checking connection", e)
            false
        }
    }

    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }

    // Method helper untuk test koneksi yang lebih reliable
    fun testConnection(): Boolean {
        return try {
            if (isConnected()) {
                // Test dengan command sederhana dan tunggu response
                val success = sendCommand(ESCPOSCommands.INIT)
                if (success) {
                    try {
                        Thread.sleep(200) // Give printer time to process
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error testing connection", e)
            false
        }
    }

    // Method untuk print dengan format yang lebih baik
    fun printFormattedText(
        text: String,
        alignment: ByteArray = ESCPOSCommands.ALIGN_LEFT,
        textSize: ByteArray = ESCPOSCommands.TEXT_NORMAL,
        bold: Boolean = false,
        underline: Boolean = false,
        feedLines: Int = 1
    ): Boolean {
        if (!isConnected()) {
            android.util.Log.w(TAG, "Cannot print - not connected")
            return false
        }

        return try {
            // Set alignment
            sendCommand(alignment)

            // Set text size
            sendCommand(textSize)

            // Set bold
            if (bold) sendCommand(ESCPOSCommands.BOLD_ON)

            // Set underline
            if (underline) sendCommand(ESCPOSCommands.UNDERLINE_ON)

            // Print text
            val success = printText(text)

            // Reset formatting
            if (bold) sendCommand(ESCPOSCommands.BOLD_OFF)
            if (underline) sendCommand(ESCPOSCommands.UNDERLINE_OFF)
            sendCommand(ESCPOSCommands.TEXT_NORMAL)

            // Feed lines
            repeat(feedLines) {
                sendCommand(ESCPOSCommands.LF)
            }

            success
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error printing formatted text", e)
            false
        }
    }

    // ESC/POS Commands
    object ESCPOSCommands {
        val INIT = byteArrayOf(0x1B, 0x40)
        val LF = byteArrayOf(0x0A)
        val CR = byteArrayOf(0x0D)
        val CUT_PAPER = byteArrayOf(0x1D, 0x56, 0x42, 0x00)
        val FEED_LINE = byteArrayOf(0x1B, 0x64, 0x02) // Feed 2 lines
        val PARTIAL_CUT = byteArrayOf(0x1D, 0x56, 0x01)
        val FULL_CUT = byteArrayOf(0x1D, 0x56, 0x00)

        val ALIGN_LEFT = byteArrayOf(0x1B, 0x61, 0x00)
        val ALIGN_CENTER = byteArrayOf(0x1B, 0x61, 0x01)
        val ALIGN_RIGHT = byteArrayOf(0x1B, 0x61, 0x02)

        val TEXT_NORMAL = byteArrayOf(0x1B, 0x21, 0x00)
        val TEXT_DOUBLE_WIDTH = byteArrayOf(0x1B, 0x21, 0x20)
        val TEXT_DOUBLE_HEIGHT = byteArrayOf(0x1B, 0x21, 0x10)
        val TEXT_DOUBLE_SIZE = byteArrayOf(0x1B, 0x21, 0x30)

        val BOLD_ON = byteArrayOf(0x1B, 0x45, 0x01)
        val BOLD_OFF = byteArrayOf(0x1B, 0x45, 0x00)
        val UNDERLINE_ON = byteArrayOf(0x1B, 0x2D, 0x01)
        val UNDERLINE_OFF = byteArrayOf(0x1B, 0x2D, 0x00)

        // Character spacing
        val CHAR_SPACING_0 = byteArrayOf(0x1B, 0x20, 0x00)
        val CHAR_SPACING_1 = byteArrayOf(0x1B, 0x20, 0x01)

        // Line spacing
        val LINE_SPACING_DEFAULT = byteArrayOf(0x1B, 0x32)
        val LINE_SPACING_N = byteArrayOf(0x1B, 0x33, 0x20) // n = 32

        fun createSeparatorLine(char: String = "-", length: Int = 32): String {
            return char.repeat(length) + "\n"
        }

        fun setCharSpacing(spacing: Int): ByteArray {
            return byteArrayOf(0x1B, 0x20, spacing.toByte())
        }

        fun setLineSpacing(spacing: Int): ByteArray {
            return byteArrayOf(0x1B, 0x33, spacing.toByte())
        }

        fun feedLines(lines: Int): ByteArray {
            return byteArrayOf(0x1B, 0x64, lines.toByte())
        }
    }
}