package cn.yue.base.view

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.PARENT_ID
import cn.yue.base.R
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.utils.device.KeyboardUtils

/**
 * Description:
 * Created by yue on 31/10/2024
 */
class ContainerInnerLayout : ConstraintLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        createTagView()
    }

    private var window: Window? = null
    private var statusHeight = 0
    private var navHeight = 0

    private fun createTagView() {
        val topView = View(context)
        topView.id = R.id.id_top_view
        topView.setBackgroundColor(Color.RED)
        val topLayoutParams = LayoutParams(1, 1)
        topLayoutParams.topToTop = PARENT_ID
        topLayoutParams.startToStart = PARENT_ID
        topLayoutParams.endToEnd = PARENT_ID
        addView(topView, topLayoutParams)

        val bottomView = View(context)
        bottomView.id = R.id.id_bottom_view
        val bottomLayoutParams = LayoutParams(1, 1)
        bottomLayoutParams.bottomToBottom = PARENT_ID
        bottomLayoutParams.startToStart = PARENT_ID
        bottomLayoutParams.endToEnd = PARENT_ID
        addView(bottomView, bottomLayoutParams)

        val keyboardBottomView = View(context)
        keyboardBottomView.id = R.id.id_keyboard_bottom_view
        val keyboardBottomLayoutParams = LayoutParams(1, 1)
        keyboardBottomLayoutParams.bottomToBottom = PARENT_ID
        keyboardBottomLayoutParams.startToStart = PARENT_ID
        keyboardBottomLayoutParams.endToEnd = PARENT_ID
        addView(keyboardBottomView, keyboardBottomLayoutParams)
    }

    private fun initView() {
        if (isInEditMode) {
            return
        }
        if (window == null) {
            if (context is Activity) {
                val context = context as Activity
                window = context.window
            }
        }
        if (window == null) {
            return
        }
        BarUtils.fixSystemBarInsetsWithKeyboard(window!!, this) { v, s, n, k ->
            statusHeight = s
            navHeight = n
            fitChildMarginBar(k)
        }
    }

    private fun fitChildMarginBar(keyboardHeight: Int) {
        for (i in 0..<childCount) {
            val childView = getChildAt(i)
            if (childView.id == R.id.id_top_view) {
                val layoutParams = childView.layoutParams as LayoutParams
                layoutParams.height = statusHeight
                childView.layoutParams = layoutParams
            } else if (childView.id == R.id.id_bottom_view) {
                val layoutParams = childView.layoutParams as LayoutParams
                layoutParams.height = navHeight
                childView.layoutParams = layoutParams
            } else if (childView.id == R.id.id_keyboard_bottom_view) {
                val layoutParams = childView.layoutParams as LayoutParams
                if (keyboardHeight > 0) {
                    layoutParams.height = keyboardHeight
                } else {
                    layoutParams.height = navHeight
                }
                childView.layoutParams = layoutParams
            }
        }
        requestLayout()
    }

    fun setWindow(window: Window?) {
        this.window = window
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (window != null) {
            KeyboardUtils.unregisterSoftInputChangedListener(window!!, getTagInt())
        }
    }

    private fun getTagInt(): Int {
        val hashCode = hashCode()
        if ((hashCode ushr 24) < 2) {
            return -hashCode
        }
        return hashCode
    }

    override fun generateLayoutParams(attrs: AttributeSet): ConstraintLayout.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): ConstraintLayout.LayoutParams {
        return LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    class LayoutParams : ConstraintLayout.LayoutParams {

        companion object {
            const val FIT_NORMAL = 0
            const val FIT_INSIDE = 1
            const val FIT_OUTSIDE = 2;
        }

        var fitTop: Int = 0
        var fitBottom: Int = 0
        var fitKeyboard: Int = 0

        constructor(source: ConstraintLayout.LayoutParams?) : super(source)

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.ContainerInnerLayout_Layout)
            fitTop = a.getInt(R.styleable.ContainerInnerLayout_Layout_fitTop, FIT_NORMAL)
            fitBottom = a.getInt(R.styleable.ContainerInnerLayout_Layout_fitBottom, FIT_NORMAL)
            fitKeyboard = a.getInt(R.styleable.ContainerInnerLayout_Layout_fitKeyboard, FIT_NORMAL)
            a.recycle()
            if (fitTop == FIT_INSIDE) {
                height = 0
                topToTop = PARENT_ID
                bottomToBottom = R.id.id_top_view
                topToBottom = UNSET
                bottomToTop = UNSET
            } else if (fitTop == FIT_OUTSIDE) {
                topToTop = UNSET
                topToBottom = R.id.id_top_view
            }
            if (fitKeyboard == FIT_NORMAL) {
                if (fitBottom == FIT_INSIDE) {
                    height = 0
                    bottomToBottom = PARENT_ID
                    topToTop = R.id.id_bottom_view
                    topToBottom = UNSET
                    bottomToTop = UNSET
                } else if (fitBottom == FIT_OUTSIDE) {
                    bottomToBottom = UNSET
                    bottomToTop = R.id.id_bottom_view
                }
            } else if (fitKeyboard == FIT_INSIDE) {
                if (fitBottom == FIT_INSIDE) {
                    height = 0
                    bottomToBottom = PARENT_ID
                    topToTop = R.id.id_keyboard_bottom_view
                    topToBottom = UNSET
                    bottomToTop = UNSET
                } else {
                    height = 0
                    bottomToTop = R.id.id_bottom_view
                    topToTop = R.id.id_keyboard_bottom_view
                    topToBottom = UNSET
                    bottomToBottom = UNSET
                }
            } else if (fitKeyboard == FIT_OUTSIDE) {
                bottomToTop = R.id.id_keyboard_bottom_view
                bottomToBottom = UNSET
            }
        }

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: ViewGroup.LayoutParams?) : super(source)
    }
}
