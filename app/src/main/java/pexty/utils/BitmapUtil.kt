package pexty.utils

import android.graphics.*
import android.view.View

object BitmapUtil {
    fun getRoundedCornerBitmap(bitmap: Bitmap, cornerRadius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val paint = Paint().apply {
            isAntiAlias = true
            color = 0xff424242.toInt()
        }
        val canvas = Canvas(output).apply {
            drawARGB(0, 0, 0, 0)
            drawRoundRect(RectF(rect), cornerRadius, cornerRadius, paint)
        }

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    fun getViewBitmap(v: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)

        Canvas(returnedBitmap).apply {
            v.background?.draw(this)
            v.draw(this)
        }

        return returnedBitmap
    }
}