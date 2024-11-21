package cn.yue.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import cn.yue.base.utils.app.BarUtils


/**
 * Description :
 * Created by yue on 2019/3/8
 */
class TopBarView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private var fullScreen = false

    fun setDefaultTitleBar(): TitleBarView {
        removeAllViews()
        val titleBar = TitleBarView(context)
        addView(titleBar, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return titleBar
    }

    fun getTitleBar(): TitleBarView {
        if (childCount <= 0) {
            return TitleBarView(context)
        }
        val child = getChildAt(0)
        if (child is TitleBarView) {
            return child
        }
        return TitleBarView(context)
    }

    fun customTopBar(v: View): TopBarView {
        removeAllViews()
        addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return this
    }

    fun hideTopBar(): TopBarView {
        fullScreen = true
        setPadding(0, 0, 0, 0)
        return this
    }

    fun setBgColor(color: Int): TopBarView {
        setBackgroundColor(color)
        return this
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (fullScreen) {
            setPadding(0, 0, 0, 0)
        } else {
            setPadding(0, BarUtils.getFixStatusBarHeight(), 0, 0)
        }
    }
}