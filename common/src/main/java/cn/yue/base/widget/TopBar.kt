package cn.yue.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.view.TitleBar


/**
 * Description :
 * Created by yue on 2019/3/8
 */
class TopBar(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    fun setDefaultTitleBar(): TitleBar {
        removeAllViews()
        val titleBar = TitleBar(context)
        addView(titleBar, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return titleBar
    }

    fun getTitleBar(): TitleBar {
        if (childCount <= 0) {
            return TitleBar(context)
        }
        val child = getChildAt(0)
        if (child is TitleBar) {
            return child
        }
        return TitleBar(context)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setPadding(0, BarUtils.getFixStatusBarHeight(), 0, 0)
    }
}