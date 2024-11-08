package cn.yue.base.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.Window
import android.widget.FrameLayout
import cn.yue.base.utils.app.BarUtils

/**
 * Description:
 * Created by yue on 25/10/2024
 */
class BottomBar(context: Context, attributeSet: AttributeSet? = null)
    : FrameLayout(context, attributeSet) {

    private var window: Window? = null

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
        BarUtils.fixSystemBarInsets(this) { v, s, n ->
            navHeight = n
            if (n > 0) {
                setPadding(0, 0, 0, n)
            } else {
                setPadding(0, 0, 0, 0)
            }
        }
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
    }
}
