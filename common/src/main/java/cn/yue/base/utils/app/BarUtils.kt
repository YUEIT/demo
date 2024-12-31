package cn.yue.base.utils.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.view.WindowManager
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import cn.yue.base.utils.Utils
import java.lang.ref.WeakReference

object BarUtils {

    /**
     * 设置状态栏样式
     * @param isFullStatusBar   是否置顶，全屏，布局在状态栏底部
     * @param isDarkIcon    状态栏内的时间等ICON，文字颜色为暗色系
     * @param bgColor       状态栏背景色
     */
    fun fullStatusBar(window: Window, isDarkIcon: Boolean = true) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        try {
            val decorView = window.decorView
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            val decorFitsFlags =
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            val sysUiVis = window.decorView.systemUiVisibility
            decorView.systemUiVisibility = sysUiVis or decorFitsFlags

            val windowInsetsCompat = WindowCompat.getInsetsController(window, decorView)
            windowInsetsCompat.isAppearanceLightStatusBars = isDarkIcon
            windowInsetsCompat.isAppearanceLightNavigationBars = isDarkIcon
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fullScreen(window: Window, isDarkIcon: Boolean = true) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        try {
            val decorView = window.decorView
            window.statusBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isStatusBarContrastEnforced = false
                window.isNavigationBarContrastEnforced = false
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val layoutParams = window.attributes
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            window.navigationBarColor = Color.TRANSPARENT
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            WindowCompat.setDecorFitsSystemWindows(window, false)
            val windowInsetsCompat = WindowCompat.getInsetsController(window, decorView)
            windowInsetsCompat.isAppearanceLightStatusBars = isDarkIcon
            windowInsetsCompat.isAppearanceLightNavigationBars = isDarkIcon

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun fullScreenPop(window: Window, isDarkIcon: Boolean = true) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        fullScreen(window, isDarkIcon)
        val layoutParams = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            layoutParams.fitInsetsTypes = 0
        }
    }

    fun setStatusBarStyle(window: Window, isDarkIcon: Boolean, bgColor: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        try {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            val decorView = window.decorView
            window.statusBarColor = bgColor
            val windowInsetsCompat = WindowCompat.getInsetsController(window, decorView)
            windowInsetsCompat.isAppearanceLightStatusBars = isDarkIcon
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setNavigationBarStyle(window: Window, isDarkIcon: Boolean, bgColor: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        try {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            val decorView = window.decorView
            window.navigationBarColor = bgColor
            val windowInsetsCompat = WindowCompat.getInsetsController(window, decorView)
            windowInsetsCompat.isAppearanceLightNavigationBars = isDarkIcon
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fixStatusBarPadding(view: View) {
        view.setPadding(0, getFixStatusBarHeight(), 0, 0)
    }

    fun fixStatusBarMargin(view: View) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.topMargin = getFixStatusBarHeight()
        view.layoutParams = layoutParams
    }

    fun fixTopBarMargin(view: View) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.topMargin = getFixStatusBarHeight() + DisplayUtils.dip2px(40)
        view.layoutParams = layoutParams
    }

    private class SystemBarViewInfo(
        val hostRef: WeakReference<View>,
        val viewRef: WeakReference<View>,
        val onApplyWindowInsetsListener: OnApplyWindowInsetsListener
    )

    private val barViewInfoList = mutableListOf<SystemBarViewInfo>()

    @SuppressLint("WrongConstant")
    private val onApplyWindowInsetsListener = OnApplyWindowInsetsListener { v, insets ->
//        val windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, v)
//        val statusInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars())
//        val statusHeight = statusInsets.top + statusInsets.bottom
//        val navHeight = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        val iterator = barViewInfoList.iterator()
        while (iterator.hasNext()) {
            val info = iterator.next()
            val host = info.hostRef.get()
            val view = info.viewRef.get()
            if (host == null || view == null) {
                iterator.remove()
                continue
            }

            if (host == v) {
                info.onApplyWindowInsetsListener.onApplyWindowInsets(v, insets)
            }
        }
        insets
    }

    fun fixNavBarMargin(vararg views: View) {
        views.forEach {
            fixSingleNavBarMargin(it)
        }
    }

    private fun fixSingleNavBarMargin(view: View) {
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return
        val rawBottomMargin = lp.bottomMargin
        fixSystemBarInsets(view) { v, s, n ->
            lp.bottomMargin = rawBottomMargin + n
            view.requestLayout()
        }
    }

    fun fixNavBarPadding(vararg views: View) {
        for (view in views) {
            fixSingleNavBarPadding(view)
        }
    }

    private fun fixSingleNavBarPadding(view: View) {
        val rawBottomPadding = view.paddingBottom
        fixSystemBarInsets(view) { v, s, n ->
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                rawBottomPadding + n
            )
        }
    }

    fun fixWindowInsets(view: View, listener: OnApplyWindowInsetsListener) {
        val viewForCalculate: View = getViewForCalculate(view)
        if (view.isAttachedToWindow) {
            val insets = ViewCompat.getRootWindowInsets(view)
            if (insets != null) {
                listener.onApplyWindowInsets(view, insets)
            }
        }
        val hostRef = WeakReference(viewForCalculate)
        val viewRef = WeakReference(view)
        val info = SystemBarViewInfo(hostRef, viewRef, listener)
        barViewInfoList.add(info)
        ViewCompat.setOnApplyWindowInsetsListener(viewForCalculate, onApplyWindowInsetsListener)
    }

    fun fixSystemBarInsets(
        view: View,
        listener: (View, Int, Int) -> Unit
    ) {
        fixWindowInsets(view) { v, insets ->
            val statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val statusHeight = statusInsets.top + statusInsets.bottom
            val navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            listener.invoke(v, statusHeight, navHeight)
            insets
        }
    }

    fun fixSystemBarInsetsWithKeyboard(
        activity: Activity,
        listener: (View, Int, Int, Int) -> Unit
    ) {
        fixSystemBarInsetsWithKeyboard(activity.window, activity.window.decorView, listener)
    }

    fun fixSystemBarInsetsWithKeyboard(
        window: Window,
        view: View,
        listener: (View, Int, Int, Int) -> Unit
    ) {
        val flags = window.attributes.flags
        if ((flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        fixWindowInsets(view) { v, insets ->
            val statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val statusHeight = statusInsets.top + statusInsets.bottom
            val navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            listener.invoke(v, statusHeight, navHeight, imeHeight)
            insets
        }
    }

    /**
     * Dialog下的View在低版本机型中获取到的WindowInsets值有误，
     * 所以尝试去获得Activity的contentView，通过Activity的contentView获取WindowInsets
     */
    @SuppressLint("ContextCast")
    private fun getViewForCalculate(view: View): View {
        return (view.context as? ContextWrapper)?.let {
            return@let (it.baseContext as? Activity)?.findViewById<View>(android.R.id.content)?.rootView
        } ?: view.rootView
    }

    fun getNavigationBarHeight(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val windowManager = Utils.getContext()
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = windowManager.currentWindowMetrics
            // Gets all excluding insets
            val windowInsets = metrics.windowInsets
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            return insets.bottom
        } else {
            val resources = Resources.getSystem()
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }
    }

    private fun getRealNavigationBarHeight(view: View): Int {
        val insets = ViewCompat.getRootWindowInsets(view)
            ?.getInsets(WindowInsetsCompat.Type.navigationBars())
        if (insets == null) {
            val resources = Resources.getSystem()
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }
        return insets.bottom
    }

    @SuppressLint("DiscouragedApi", "InternalInsetResource")
    fun getStatusBarHeight(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val windowManager = Utils.getContext()
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = windowManager.currentWindowMetrics
            // Gets all excluding insets
            val windowInsets = metrics.windowInsets
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            return insets.top + insets.bottom
        } else {
            val resources = Resources.getSystem()
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }
    }

    private fun getRealStatusBarHeight(view: View): Int {
        val insets = ViewCompat.getRootWindowInsets(view)
            ?.getInsets(WindowInsetsCompat.Type.statusBars())
        if (insets == null) {
            val resources = Resources.getSystem()
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }
        return insets.top + insets.bottom
    }

    fun getFixStatusBarHeight(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val realStatusBarHeight = getStatusBarHeight()
            if (realStatusBarHeight < DisplayUtils.dip2px(40)) {
                return DisplayUtils.dip2px(40)
            }
            return realStatusBarHeight
        }
        return 0
    }
}
