package kg.optima.mobile.doc.printersample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object PdfManager {
	
	fun pdfToBitmap(context: Context, pdfFile: File, pageIndex: Int = 0): Bitmap? {
		val scaleFactor: Float = 2f
		
		try {
			val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
			val renderer = PdfRenderer(fileDescriptor)
			
			if (pageIndex >= renderer.pageCount) return null
			
			val page = renderer.openPage(pageIndex)
			
			// Определяем размер с масштабированием
			val targetWidth = (page.width * scaleFactor).toInt()
			val targetHeight = (page.height * scaleFactor).toInt()
			
			val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
			
			// 1. Создаём Canvas и заливаем фон белым
			val canvas = Canvas(bitmap)
			canvas.drawColor(Color.WHITE) // Заполняем фон белым
			
			// 2. Создаём промежуточный bitmap для рендеринга
			val tempBitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
			val tempCanvas = Canvas(tempBitmap)
			tempCanvas.drawColor(Color.TRANSPARENT) // Заполняем прозрачным
			
			// 3. Рендерим PDF в tempBitmap
			page.render(tempBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
			
			// 4. Масштабируем изображение
			val matrix = Matrix()
			matrix.setScale(scaleFactor, scaleFactor)
			
			// 5. Копируем отмасштабированное изображение на Canvas
			canvas.drawBitmap(tempBitmap, matrix, null)
			
			page.close()
			renderer.close()
			fileDescriptor.close()
			
			return bitmap
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return null
	}
	
	
	fun getPdfFileFromRaw(context: Context, rawResId: Int): File {
		val inputStream = context.resources.openRawResource(rawResId)
		val file = File(context.cacheDir, "sample.pdf")
		
		FileOutputStream(file).use { output ->
			inputStream.copyTo(output)
		}
		
		return file
	}
	
	
}