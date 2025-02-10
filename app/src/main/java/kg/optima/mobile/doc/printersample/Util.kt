package kg.optima.mobile.doc.printersample

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

fun logd(text : String){
	Log.d("PRINTER_TEST", text)
}

fun loge(e : Exception){
	Log.e("PRINTER_TEST", e.stackTraceToString())
}

fun Context.toast(text : String){
	Toast.makeText(this, "text", Toast.LENGTH_SHORT).show()
}

inline fun Context.checkBTConnectPermission(notGranted : () -> Unit) {
	if (ContextCompat.checkSelfPermission(
			this,
			android.Manifest.permission.BLUETOOTH_CONNECT
		) != PackageManager.PERMISSION_GRANTED
	) {
		logd("BLUETOOTH_CONNECT permission not granted")
		notGranted.invoke()
	}
}