package kg.optima.mobile.doc.printersample

import android.graphics.Bitmap
import android.graphics.Color

object BitmapManager {
	
	fun convertToMonoBitmap(bitmap: Bitmap): Bitmap {
		val monoBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
		
		var totalBrightness = 0L
		val totalPixels = bitmap.width * bitmap.height
		
		for (y in 0 until bitmap.height) {
			for (x in 0 until bitmap.width) {
				val pixel = bitmap.getPixel(x, y)
				val brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
				totalBrightness += brightness
			}
		}
		
		val threshold = (totalBrightness / totalPixels).toInt()
		
		for (y in 0 until bitmap.height) {
			for (x in 0 until bitmap.width) {
				val pixel = bitmap.getPixel(x, y)
				val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
				monoBitmap.setPixel(x, y, if (gray < threshold) Color.BLACK else Color.WHITE)
			}
		}
		
		return monoBitmap
	}
	
}