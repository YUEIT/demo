package cn.yue.base.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.Window
import android.widget.FrameLayout
import cn.yue.base.R
import cn.yue.base.utils.app.BarUtils

/**
 * Description:
 * Created by yue on 25/10/2024
 */
class BottomLineView : FrameLayout {
    private var keyboardEnable = false
    private var fullOffset = 0
    private var keyboardOpenOffset = 0
    private var window: Window? = null

    constructor(context: Context?) : super(context!!)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BottomLineView, 0, 0)
        fullOffset = a.getDimensionPixelSize(R.styleable.BottomLineView_fullOffset, 0)
        keyboardOpenOffset = a.getDimensionPixelSize(R.styleable.BottomLineView_fullOffset, 0)
        keyboardEnable = a.getBoolean(R.styleable.BottomLineView_keyboardEnable, false)
        a.recycle()
    }

    private var navHeight = 0

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
        if (keyboardEnable) {
            BarUtils.fixSystemBarInsetsWithKeyboard(window!!, this) { v, s, n, k ->
                navHeight = n
                if (k > 0) {
                    setPadding(0, 0, 0, k + keyboardOpenOffset)
                } else {
                    if (n > 0) {
                        setPadding(0, 0, 0, n)
                    } else {
                        setPadding(0, 0, 0, fullOffset)
                    }
                }
            }
        } else {
            BarUtils.fixSystemBarInsets(this) { v, s, n ->
                navHeight = n
                if (n > 0) {
                    setPadding(0, 0, 0, n)
                } else {
                    setPadding(0, 0, 0, fullOffset)
                }
            }
        }
    }

    fun setWindow(window: Window?) {
        this.window = window
    }

    fun setFullOffset(offset: Int) {
        this.fullOffset = offset
    }

    fun setKeyboardOpenOffset(offset: Int) {
        this.keyboardOpenOffset = offset
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}
