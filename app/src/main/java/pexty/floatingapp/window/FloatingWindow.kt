package pexty.floatingapp.window

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup

open class FloatingWindow constructor(
    context: Context,
    _width: Int,
    _height: Int,
    _x: Int = 0,
    _y: Int = 0,
    _title: String = "Floating Window",
    _flags: Int = 0
): BaseWindow(
    context, _width, _height, _x, _y, _title, _flags
) {
    protected var _contentView: View? = null

    protected fun checkContentView() {
        if (_contentView != null) removeContentView()
    }

    open fun setContentView(__contentView: View?) {
        checkContentView()
        contentContainerView.addView(__contentView)
    }

    open fun setContentView(__contentView: View?, width: Int, height: Int) {
        checkContentView()
        contentContainerView.addView(__contentView, width, height)
    }

    open fun setContentView(__contentView: View?, params: ViewGroup.LayoutParams?) {
        checkContentView()
        contentContainerView.addView(__contentView, params)
    }

    open fun getContentView(): View? = _contentView

    open fun removeContentView() {
        contentContainerView.removeView(_contentView)
        _contentView = null
    }

    open fun setContentBackgroundColor(color: Int) {
        contentContainerView.setCardBackgroundColor(color)
    }

    open fun setContentBackgroundColor(color: ColorStateList) {
        contentContainerView.setCardBackgroundColor(color)
    }
}