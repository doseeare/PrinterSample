package kg.optima.mobile.doc.printersample

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.ContextCompat
import java.io.InputStream
import java.io.OutputStream
import kotlin.experimental.or

class BluetoothPrinter(private val context: Context) {
	private val bluetoothAdapter: BluetoothAdapter =
		(context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
	private var bluetoothSocket: BluetoothSocket? = null
	private var outputStream: OutputStream? = null
	private var inputStream: InputStream? = null
	
	var isConnect: Boolean = false
	
	fun searchPrinter(): String? {
		context.checkBTConnectPermission {
			return null
		}
		
		val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
		val printerDevice = pairedDevices.firstOrNull {
			it.name.contains(
				"Printer",
				ignoreCase = true
			)
		}
		return printerDevice?.address
	}
	
	fun tryConnect(macAddress: String): Boolean {
		if (ContextCompat.checkSelfPermission(
				context,
				android.Manifest.permission.BLUETOOTH_CONNECT
			) != PackageManager.PERMISSION_GRANTED
		) {
			logd("BLUETOOTH_CONNECT permission not granted")
			isConnect = false
			return false
		}
		
		val device: BluetoothDevice =
			bluetoothAdapter.getRemoteDevice(macAddress) ?: return false
		
		device.fetchUuidsWithSdp()
		
		device.uuids?.forEach {
			try {
				bluetoothSocket = device.createRfcommSocketToServiceRecord(it.uuid)
				bluetoothSocket?.connect()
				logd("bluetoothSocket connected")
				return@forEach
			} catch (e: Exception) {
				logd("bluetoothSocket connected attempt failed")
				loge(e)
			}
		}
		
		outputStream = bluetoothSocket?.outputStream
		inputStream = bluetoothSocket?.inputStream
		isConnect = true
		return true
	}
	
	fun printText(text: String) {
		try {
			val escPosText = text.toByteArray(Charsets.UTF_8)
			outputStream?.write(escPosText)
			outputStream?.write(byteArrayOf(0x1B, 0x74, 0x11))
			outputStream?.write(0x0A)
			outputStream?.write(0x0A)
			outputStream?.write(0x0A)
			outputStream?.write(10)
			outputStream?.flush()
		} catch (e: Exception) {
			loge(e)
		}
	}
	
	fun printBitmap(bitmap: Bitmap) {
		try {
			val bwBitmap = BitmapManager.convertToMonoBitmap(bitmap)
			
			
			
			val widthBytes = (bwBitmap.width + 7) / 8
			val height = bwBitmap.height
			
			val command = byteArrayOf(
				0x1D,
				0x76,
				0x30,
				0x00,
				(widthBytes and 0xFF).toByte(),
				(widthBytes shr 8).toByte(),
				(height and 0xFF).toByte(),
				(height shr 8).toByte()
			)
			outputStream?.write(command)
			
			for (y in 0 until height) {
				val rowBytes = ByteArray(widthBytes)
				for (x in 0 until bwBitmap.width) {
					if (bwBitmap.getPixel(x, y) == Color.BLACK) {
						rowBytes[x / 8] = rowBytes[x / 8] or (0x80 shr (x % 8)).toByte()
					}
				}
				outputStream?.write(rowBytes)
			}
			
			outputStream?.write(byteArrayOf(0x0A))
			outputStream?.write(byteArrayOf(0x0A))
			outputStream?.write(byteArrayOf(0x0A))
			outputStream?.flush()
		} catch (e: Exception) {
			loge(e)
		}
	}
	
	fun disconnect() {
		try {
			outputStream?.close()
			bluetoothSocket?.close()
		} catch (e: Exception) {
			loge(e)
		}
	}
	
}
