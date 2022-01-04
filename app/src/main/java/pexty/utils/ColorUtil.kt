package pexty.utils

import android.graphics.Color

object ColorUtil {
    fun setAlpha(color: Int, alpha: Int): Long = (alpha shl 24).toLong() or color.toLong()
    fun setAlpha(color: Long, alpha: Int): Long = (alpha shl 24).toLong() or color
    fun setAlpha(color: Int, alpha: Float): Long = (alpha * 255).toLong() shl 24 or color.toLong()
    fun setAlpha(color: Long, alpha: Float): Long = (alpha * 255).toLong() shl 24 or color

    fun removeAlpha(color: Int): Int = Color.red(color) shl 16 or (Color.green(color) shl 8) or Color.blue(color)
    fun removeAlpha(color: Long): Int = (Color.red(color) * 255).toInt() shl 16 or ((Color.green(color) * 255).toInt() shl 8) or (Color.blue(color) * 255).toInt()

    fun invert(color: Int): Int = setAlpha(removeAlpha(color).inv() and 0xffffff, Color.alpha(color)).toInt()
    fun invert(color: Long): Long = setAlpha(removeAlpha(color).inv() and 0xffffff, Color.alpha(color))

    fun toGray(color: Int): Int {
        val L = (Color.red(color).toFloat() * 299f/1000f + Color.green(color).toFloat() * 587f/1000f + Color.blue(color).toFloat() * 114f/1000f).toInt()
        return Color.argb(Color.alpha(color), L, L, L)
    }

    fun toGray(color: Long): Long {
        val L = (Color.red(color) * 299f/1000f + Color.green(color) * 587f/1000f + Color.blue(color) * 114f/1000f) / 255f
        return Color.argb(Color.alpha(color), L, L, L).toLong()
    }

    fun toBlackWhite(color: Int): Int {
        var L = Color.red(toGray(color))
        L = if (L < 127) 0 else 255
        return Color.argb(Color.alpha(color), L, L, L)
    }

    fun toBlackWhite(color: Long): Long {
        var L = Color.red(toGray(color))
        L = if (L < 127) 0f else 1f
        return Color.argb(Color.alpha(color), L, L, L).toLong()
    }

    fun toVisibleColorByBackground(color: Int): Int = toBlackWhite(invert(color))
    fun toVisibleColorByBackground(color: Long): Long = toBlackWhite(invert(color))
}