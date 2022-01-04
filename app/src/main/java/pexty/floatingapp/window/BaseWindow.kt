package pexty.floatingapp.window

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Icon
import android.graphics.drawable.RippleDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import com.pexty.studios.floating.windows.engine.R
import pexty.floatingapp.FloatingObject
import pexty.floatingapp.FloatingPermissions
import pexty.utils.BitmapUtil
import pexty.utils.ColorUtil
import pexty.utils.Utils
import kotlin.math.max
import kotlin.math.min

open class BaseWindow constructor(
    protected val context: Context,
    _width: Int,
    _height: Int,
    _x: Int = 0,
    _y: Int = 0,
    _title: String = "Floating Window",
    protected var _flags: Int = 0
) {
    companion object {
        const val FLAG_WINDOW_NOT_DRAGGABLE = 1 shl 0
    }

    interface OnTouchContentListener {
        fun onTouch(baseWindow: BaseWindow, event: MotionEvent?): Boolean
    }

    interface WindowLifecycleListener {
        fun onOpen(baseWindow: BaseWindow)
        fun onQuit(baseWindow: BaseWindow)
    }

    interface WindowFocusListener {
        fun onFocused(baseWindow: BaseWindow)
        fun onUnfocused(baseWindow: BaseWindow)
    }

    var onTouchContentListener: OnTouchContentListener? = null
    var windowLifecycleListener: WindowLifecycleListener? = null
    var windowFocusListener: WindowFocusListener? = null

    protected val windowCornerRadius = Utils.DPtoPixels(context, 10)

    protected var _isExists = false

    protected var _minWindth = 0
    protected var _minHeight = 0

    protected var _borderColor: Int = Color.WHITE
    protected var _titleColor: Int? = null

    private val titleView = TextView(context).apply {
        layoutParams = TableLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1f
            leftMargin = Utils.DPtoPixels(context, 1).toInt()
            rightMargin = leftMargin
        }

        text = _title
        maxLines = 1
        setEms(3)
        ellipsize = TextUtils.TruncateAt.END
        setTypeface(typeface, Typeface.BOLD)
    }

    private val iconView = ImageView(context).apply {
        scaleType = ImageView.ScaleType.FIT_XY
        layoutParams = TableLayout.LayoutParams(0, 0)
    }

    private val quitButtonView = LinearLayout(context).apply {
        setOnClickListener {
            close()
        }

        background = RippleDrawable(
            ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.argb(150, 255, 255, 255))),
            GradientDrawable().apply {
                setColor(Color.RED)
                cornerRadius = 90f
                setStroke(3, Color.WHITE)
            },
            null
        )
    }

    private val actionBarView = LinearLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = Utils.DPtoPixels(context, 1).toInt()
            bottomMargin = topMargin
            leftMargin = Utils.DPtoPixels(context, 2).toInt()
            rightMargin = leftMargin
        }
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER

        addView(iconView)
        addView(titleView)
        addView(quitButtonView)

        measure(layoutParams.width, layoutParams.height)
    }

    protected val contentContainerView = CardView(context).apply {
        layoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT
        ).apply {
            leftMargin = Utils.DPtoPixels(context, 2).toInt()
            rightMargin = leftMargin
            bottomMargin = leftMargin
        }

        radius = windowCornerRadius
        setCardBackgroundColor(Color.BLACK)

        setOnTouchListener { _, e -> onTouchContentListener != null && onTouchContentListener!!.onTouch(this@BaseWindow, e)}

        addView(Button(context).apply {
            text = "Unfocus window"
            setOnClickListener {
                unfocus()
            }
        })
    }

    private val baseContainerView = LinearLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT)
        orientation = LinearLayout.VERTICAL

        addView(actionBarView)
        addView(contentContainerView)
    }

    private val scaleAnchorView = LinearLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(0, 0)

        setOnTouchListener(object: View.OnTouchListener {
            var downX = 0f
            var downY = 0f

            var startX = 0f
            var startY = 0f

            override fun onTouch(v: View?, e: MotionEvent?): Boolean {
                when (e?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        downX = e.x
                        downY = e.y

                        startX = this@apply.x
                        startY = this@apply.y
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val moveX = e.x - downX
                        val moveY = e.y - downY

                        startX += moveX
                        startY += moveY

                        this@apply.x = startX
                        this@apply.y = startY

                        this@BaseWindow.width = startX.toInt() + this@apply.layoutParams.width
                        this@BaseWindow.height = startY.toInt() + this@apply.layoutParams.height
                    }
                }

                return true
            }
        })

        background = GradientDrawable(
            GradientDrawable.Orientation.BR_TL,
            intArrayOf(Color.WHITE, Color.BLACK)
        ).apply {
            cornerRadii = floatArrayOf(90f, 90f, 20f, 20f, 20f, 20f, 20f, 20f)
            setStroke(5, Color.WHITE, 8f, 10f)
            elevation = 15f
        }
    }

    private val floatingView = CoordinatorLayout(context).apply {
        layoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT
        ).apply {
            setMargins(Utils.DPtoPixels(context, 2).toInt())
        }

        background = GradientDrawable().apply {
            setColor(_borderColor)
            cornerRadius = windowCornerRadius
        }
        elevation = 15f

        addView(baseContainerView)
        addView(scaleAnchorView)
    }

    private val floatingObject = FloatingObject(
        context,
        floatingView,
        0,
        0,
        _flags = FloatingObject.FLAG_LAYOUT_NO_LIMITS
    )

    private val unfocusedFloatingView = ImageView(context).apply {
        layoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT
        ).apply {
            setMargins(Utils.DPtoPixels(context, 2).toInt())
        }

        scaleType = ImageView.ScaleType.FIT_XY

        elevation = 15f
    }

    private val unfocusedFloatingObject = FloatingObject(
        context,
        unfocusedFloatingView,
        0,
        0
    ).apply {
        onTouchListener = object: FloatingObject.OnTouchListener {
            override fun onTouch(floatingObject: FloatingObject, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> WindowManager._unfocusCurrent()
                    MotionEvent.ACTION_UP -> focus()
                }

                return false
            }
        }
    }

    var minWidth: Int
        get() = floatingObject.width
        set(value) {
            _minWindth = min(max(actionBarHeight * 3, value), context.resources.displayMetrics.widthPixels)

            if (floatingObject.width > _minWindth) width = floatingObject.width
        }

    var minHeight: Int
        get() = floatingObject.height
        set(value) {
            _minHeight = min(max(actionBarHeight * 2, value), context.resources.displayMetrics.heightPixels)

            if (floatingObject.height > _minHeight) height = floatingObject.height
        }

    var width: Int
        get() = floatingObject.width
        set(value) {
            focus()

            floatingObject.width = min(max(_minWindth, value), context.resources.displayMetrics.widthPixels)
            scaleAnchorView.x = floatingObject.width - actionBarHeight - Utils.DPtoPixels(context, 4)
        }

    var height: Int
        get() = floatingObject.height
        set(value) {
            focus()

            floatingObject.height = min(max(_minHeight, value), context.resources.displayMetrics.heightPixels)
            scaleAnchorView.y = floatingObject.height - actionBarHeight - Utils.DPtoPixels(context, 4)
        }

    var x: Int
        get() = floatingObject.x
        set(value) {
            focus()

            floatingObject.x = value
        }

    var y: Int
        get() = floatingObject.y
        set(value) {
            focus()

            floatingObject.y = value
        }

    var title: String
        get() = titleView.text.toString()
        set(value) {
            focus()

            titleView.text = value
        }

    var flags: Int
        get() = _flags
        set(value) {
            _flags = value

            applyFlags()
        }

    var iconVisible: Boolean
        get() = iconView.isVisible
        set(value) {
            iconView.isVisible = value
        }

    var titleVisible: Boolean
        get() = titleView.isVisible
        set(value) {
            titleView.isVisible = value
        }

    var quitButtonVisible: Boolean
        get() = quitButtonView.isVisible
        set(value) {
            quitButtonView.isVisible = value
        }

    var borderColor: Int
        get() = _borderColor
        set(value) {
            _borderColor = value
            (floatingView.background as GradientDrawable).setColor(value)
            titleColor = _titleColor
        }

    var titleColor: Int?
        get() = _titleColor
        set(value) {
            _titleColor = value
            titleView.setTextColor(value?: ColorUtil.toVisibleColorByBackground(_borderColor))
        }

    var isFocused: Boolean
        get() = floatingObject.isExists
        set(value) {
            if (value) focus()
            else unfocus()
        }

    init {
        setIconImage(R.mipmap.ic_launcher)

        iconView.layoutParams.apply {
            width = actionBarHeight - Utils.DPtoPixels(context, 4).toInt()
            height = width
        }

        quitButtonView.layoutParams.apply {
            width = actionBarHeight - Utils.DPtoPixels(context, 4).toInt()
            height = width
        }

        scaleAnchorView.layoutParams.apply {
            width = actionBarHeight
            height = actionBarHeight
        }

        minWidth = 0
        minHeight = 0

        width = _width
        height = _height

        x = _x
        y = _y

        applyFlags()
    }

    val isExists: Boolean get() = _isExists
    val actionBarHeight: Int get() = titleView.measuredHeight

    fun hasFlags(__flags: Int): Boolean = (_flags and __flags) != 0

    fun addFlags(__flags: Int) {
        flags = _flags or __flags
    }

    fun removeFlags(__flags: Int) {
        flags = _flags and __flags
    }

    fun setIconImage(resourceId: Int) {
        setIconImage(context.resources.getDrawable(resourceId))
    }

    fun setIconImage(drawable: Drawable) {
        setIconImage(drawable.toBitmap())
    }

    fun setIconImage(_bitmap: Bitmap) {
        val bitmap = BitmapUtil.getRoundedCornerBitmap(_bitmap, Utils.DPtoPixels(context, 90))
        iconView.setImageBitmap(bitmap)
    }

    fun setIconImage(icon: Icon) {
        setIconImage(icon.loadDrawable(context))
    }

    protected open fun applyFlags() {
        if (hasFlags(FLAG_WINDOW_NOT_DRAGGABLE))
            floatingObject.removeFlags(FloatingObject.FLAG_DRAGGABLE)
        else
            floatingObject.addFlags(FloatingObject.FLAG_DRAGGABLE)

        unfocusedFloatingObject.flags = floatingObject.flags
    }

    fun focus() {
        if (!floatingObject.isExists && _isExists) {
            WindowManager._initFocus(this)

            floatingObject.x = unfocusedFloatingObject.x
            floatingObject.y = unfocusedFloatingObject.y

            floatingObject.create()
            unfocusedFloatingObject.destroy()

            windowFocusListener?.onFocused(this)
        }
    }

    fun unfocus() {
        if (floatingObject.isExists) {
            unfocusedFloatingView.setImageBitmap(BitmapUtil.getViewBitmap(floatingView))

            unfocusedFloatingObject.x = floatingObject.x
            unfocusedFloatingObject.y = floatingObject.y

            unfocusedFloatingObject.width = floatingObject.width
            unfocusedFloatingObject.height = floatingObject.height

            floatingObject.destroy()
            unfocusedFloatingObject.create()

            windowFocusListener?.onUnfocused(this)
        }
    }

    fun kill() {
        if (_isExists) {
            _isExists = false

            WindowManager._remove(this)

            windowFocusListener?.onUnfocused(this)

            floatingObject.destroy()
            unfocusedFloatingObject.destroy()
        }
    }

    fun open() {
        if (!_isExists && FloatingPermissions.has(context)) {
            focus()
            floatingObject.create()
            _isExists = true
            windowLifecycleListener?.onOpen(this)
        }
    }

    fun close() {
        windowLifecycleListener?.onQuit(this)?: kill()
    }
}