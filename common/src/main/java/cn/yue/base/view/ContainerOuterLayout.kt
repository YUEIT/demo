package cn.yue.base.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import cn.yue.base.R
import cn.yue.base.utils.app.BarUtils

/**
 * Description:
 * Created by yue on 31/10/2024
 */
class ContainerOuterLayout : ConstraintLayout {

    companion object {
        const val FIT_NORMAL = 0
        const val FIT_INSIDE = 1
        const val FIT_OUTSIDE = 2;
    }

    private var fitTop: Int = 0
    private var fitBottom: Int = 0
    private var fitKeyboard: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ContainerOuterLayout, 0, 0)
        fitTop = a.getInt(R.styleable.ContainerOuterLayout_fitTop, FIT_NORMAL)
        fitBottom = a.getInt(R.styleable.ContainerOuterLayout_fitBottom, FIT_NORMAL)
        fitKeyboard = a.getInt(R.styleable.ContainerOuterLayout_fitKeyboard, FIT_NORMAL)
        a.recycle()
    }

    private var window: Window? = null
    private var statusHeight = 0
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
        BarUtils.fixSystemBarInsetsWithKeyboard(window!!, this) { v, s, n, k ->
            statusHeight = s
            navHeight = n
            fitPaddingBar(k)
        }
    }

    private fun fitPaddingBar(keyboardHeight: Int) {
        var paddingTop = 0
        if (fitTop == FIT_OUTSIDE) {
            paddingTop = statusHeight
        } else {
            paddingTop = 0
        }
        var paddingBottom = 0
        if (fitKeyboard == FIT_OUTSIDE) {
            if (fitBottom == FIT_OUTSIDE) {
                if (keyboardHeight > 0) {
                    paddingBottom = keyboardHeight
                } else {
                    paddingBottom = navHeight
                }
            } else {
                if (keyboardHeight > 0) {
                    paddingBottom = keyboardHeight
                } else {
                    paddingBottom = 0
                }
            }
        } else {
            if (fitBottom == FIT_OUTSIDE) {
                paddingBottom = navHeight
            } else {
                paddingBottom = 0
            }
        }
        setPadding(0, paddingTop, 0, paddingBottom)
    }

    fun setWindow(window: Window?) {
        this.window = window
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
    }

}
