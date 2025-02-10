package kg.optima.mobile.doc.printersample

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {
	
	private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
	private val printer by lazy {
		BluetoothPrinter(this)
	}
	
	@SuppressLint("MissingInflatedId")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		val button: Button = findViewById(R.id.button)
		val reconnectBtn: Button = findViewById(R.id.reconnect_btn)
		val editText: EditText = findViewById(R.id.edit_text)
		val printBitmap : Button = findViewById(R.id.bitmap)
		
		if (bluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth не поддерживается", Toast.LENGTH_LONG).show()
			return
		}
		
		
		val deviceMac = printer.searchPrinter()
		
		if (deviceMac == null) {
			logd("device not found")
		} else {
			printer.tryConnect(deviceMac)
			
		}
		
		printBitmap.setOnClickListener {
			val file = PdfManager.getPdfFileFromRaw(this, R.raw.receipt)
			val pdf = PdfManager.pdfToBitmap(this, file) ?: throw NullPointerException("suka net takogo")
			if (printer.isConnect) {
				printer.printBitmap(pdf)
			} else {
				toast("printer not connected")
			}
		}
		
		reconnectBtn.setOnClickListener {
			val reconnectMac = printer.searchPrinter()
			if (reconnectMac == null) {
				logd("device not found")
			} else {
				printer.tryConnect(reconnectMac)
				toast("trying connect")
			}
			
		}
		
		button.setOnClickListener {
			if (printer.isConnect) {
				printer.printText(editText.text.toString())
			} else {
				toast("printer not connected")
			}
		}
		
	}
	
	override fun onDestroy() {
		super.onDestroy()
		printer.disconnect()
	}
	
}

