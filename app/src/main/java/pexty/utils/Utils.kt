package pexty.utils

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity

object Utils {
    const val ORIENTATION_SQUARE = 0
    const val ORIENTATION_PORTRAIT = 1
    const val ORIENTATION_LANDSCAPE = 2

    fun getAndroidStatusBarHeight(context: Context): Int = Rect().apply {
        (context as AppCompatActivity).window.decorView.getWindowVisibleDisplayFrame(this)
    }.top

    fun getScreenOrientation(context: Context): Int {
        val displayMetrics = context.applicationContext.resources.displayMetrics

        return when {
            displayMetrics.heightPixels > displayMetrics.widthPixels -> ORIENTATION_PORTRAIT
            displayMetrics.heightPixels < displayMetrics.widthPixels -> ORIENTATION_LANDSCAPE
            else -> ORIENTATION_SQUARE
        }
    }

    fun DPtoPixels(context: Context, value: Int): Float = context.resources.displayMetrics.density * value
}