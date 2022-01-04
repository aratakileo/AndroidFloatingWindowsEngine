package pexty.floatingapp

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.max

class FloatingObject constructor(
    private val context: Context,
    private val floatingView: View,
    _width: Int,
    _height: Int,
    _x: Int = 0,
    _y: Int = 0,
    private var _flags: Int = 0
) {
    companion object {
        const val FLAG_DRAGGABLE = 1 shl 0
        const val FLAG_LAYOUT_NO_LIMITS = 1 shl 1
        const val FLAG_LAYOUT_IN_SCREEN = 1 shl 2
        const val FLAG_ANDROID_SECURE = 1 shl 3
        const val FLAG_ANDROID_FOCUSABLE = 1 shl 4
    }

    interface OnTouchListener {
        fun onTouch(floatingObject: FloatingObject, event: MotionEvent?): Boolean
    }

    interface DragListener {
        fun onStartDragging(floatingObject: FloatingObject, touchX: Float, touchY: Float)
        fun onStopDragging(floatingObject: FloatingObject, touchX: Float, touchY: Float)
    }

    interface DragFilter {
        fun applyDragging(floatingObject: FloatingObject, touchX: Float, touchY: Float, newX: Int, newY: Int): Boolean
    }

    var onTouchListener: OnTouchListener? = null
    var dragFilter: DragFilter? = null
    var dragListener: DragListener? = null

    private var _isExists = false

    private val androidWindowManager = context.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager
    private val floatingLayoutParams = WindowManager.LayoutParams(
        _width,
        _height,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        0,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.START or Gravity.TOP
        this.x = _x
        this.y = _y
    }

    init {
        floatingView.setOnTouchListener(object: View.OnTouchListener {
            var newFloatingLayoutParams: WindowManager.LayoutParams = floatingLayoutParams

            var x = 0
            var y = 0

            var touchX = 0f
            var touchY = 0f

            override fun onTouch(v: View?, e: MotionEvent?): Boolean {
                if (hasFlags(FLAG_DRAGGABLE)) {
                    when (e?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            this.x = newFloatingLayoutParams.x
                            this.y = newFloatingLayoutParams.y
                            touchX = e.rawX
                            touchY = e.rawY

                            dragListener?.onStartDragging(this@FloatingObject, e.rawX, e.rawY)
                        }

                        MotionEvent.ACTION_MOVE -> {
                            newFloatingLayoutParams.x = (this.x + (e.rawX - touchX)).toInt()
                            newFloatingLayoutParams.y = (this.y + (e.rawY - touchY)).toInt()

                            if (dragFilter == null || dragFilter?.applyDragging(this@FloatingObject, e.rawX, e.rawY, newFloatingLayoutParams.x, newFloatingLayoutParams.y) == true) {
                                this@FloatingObject.x = newFloatingLayoutParams.x
                                this@FloatingObject.y = newFloatingLayoutParams.y
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            dragListener?.onStopDragging(this@FloatingObject, e.rawX, e.rawY)
                        }
                    }
                }

                return onTouchListener != null && onTouchListener!!.onTouch(this@FloatingObject, e)
            }
        })

        applyFlags()
    }

    var width: Int
        get() = floatingLayoutParams.width
        set(value) {
            floatingLayoutParams.width = max(0, value)

            androidApplyDisplayChanges()
        }

    var height: Int
        get() = floatingLayoutParams.height
        set(value) {
            floatingLayoutParams.height = max(0, value)

            androidApplyDisplayChanges()
        }

    var x: Int
        get() = floatingLayoutParams.x
        set(value) {
            floatingLayoutParams.x = value

            androidApplyDisplayChanges()
        }

    var y: Int
        get() = floatingLayoutParams.y
        set(value) {
            floatingLayoutParams.y = value

            androidApplyDisplayChanges()
        }

    var flags: Int
        get() = _flags
        set(value) {
            _flags = value

            applyFlags()
        }

    val isExists: Boolean get() = _isExists

    fun hasFlags(__flags: Int): Boolean = (_flags and __flags) != 0

    fun addFlags(__flags: Int) {
        flags = _flags or __flags
    }

    fun removeFlags(__flags: Int) {
        flags = _flags and __flags
    }

    private fun applyFlags() {
        floatingLayoutParams.flags = if (hasFlags(FLAG_ANDROID_SECURE))
            floatingLayoutParams.flags or WindowManager.LayoutParams.FLAG_SECURE
        else
            floatingLayoutParams.flags and WindowManager.LayoutParams.FLAG_SECURE

        floatingLayoutParams.flags = if (hasFlags(FLAG_LAYOUT_IN_SCREEN))
            floatingLayoutParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        else
            floatingLayoutParams.flags and WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        floatingLayoutParams.flags = if (hasFlags(FLAG_LAYOUT_NO_LIMITS))
            floatingLayoutParams.flags or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        else
            floatingLayoutParams.flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        floatingLayoutParams.flags = if (hasFlags(FLAG_ANDROID_FOCUSABLE))
            floatingLayoutParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        else
            floatingLayoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }

    private fun androidApplyDisplayChanges() {
        if (_isExists) androidWindowManager.updateViewLayout(floatingView, floatingLayoutParams)
    }

    fun create() {
        if (!_isExists and FloatingPermissions.has(context)) {
            androidWindowManager.addView(floatingView, floatingLayoutParams)
            _isExists = true
        }
    }

    fun destroy(immediate: Boolean = false) {
        if (_isExists) {
            if (immediate)
                androidWindowManager.removeViewImmediate(floatingView)
            else
                androidWindowManager.removeView(floatingView)
        }

        _isExists = false
    }
}