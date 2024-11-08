package cn.yue.base.utils.device

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import cn.yue.base.utils.Utils
import cn.yue.base.utils.app.BarUtils

object ScreenUtils {

    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return 屏幕宽px
     */
    @JvmStatic
    val screenWidth: Int
        get() {
            val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val metrics = windowManager.currentWindowMetrics
                // Gets all excluding insets
                val windowInsets = metrics.windowInsets
                val insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.navigationBars()
                            or WindowInsets.Type.displayCutout()
                )
                val insetsWidth: Int = insets.right + insets.left
                val bounds: Rect = metrics.bounds
                return  bounds.width() - insetsWidth
            } else {
                val dm = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(dm)
                return dm.widthPixels
            }
        }

    /**
     * 获取屏幕的高度（单位：px）
     * 包括状态栏，不包括导航栏
     * @return 屏幕高px
     */
    @JvmStatic
    val screenHeight: Int
        get() {
            val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val metrics = windowManager.currentWindowMetrics
                // Gets all excluding insets
                val windowInsets = metrics.windowInsets
                val insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.navigationBars()
                            or WindowInsets.Type.displayCutout()
                )
                val insetsHeight: Int = insets.top + insets.bottom
                val bounds: Rect = metrics.bounds
                return bounds.height() - insetsHeight
            } else {
                val dm = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(dm)
                if (Math.abs(fixScreenHeight - dm.heightPixels) < 10) {
                    return dm.heightPixels
                }
                if (fixScreenHeight > 0) {
                    return fixScreenHeight
                }
                return dm.heightPixels + BarUtils.getFixStatusBarHeight()
            }
        }

    /**
     * 获取屏幕的高度（单位：px）
     * 不包括状态栏，不包括导航栏
     * @return 屏幕高px
     */
    @JvmStatic
    val innerScreenHeight: Int
        get() {
            val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val metrics = windowManager.currentWindowMetrics
                // Gets all excluding insets
                val windowInsets = metrics.windowInsets
                val insets = windowInsets.getInsetsIgnoringVisibility(
                    WindowInsets.Type.navigationBars()
                            or WindowInsets.Type.displayCutout()
                            or WindowInsets.Type.statusBars()
                )
                val insetsHeight: Int = insets.top + insets.bottom
                val bounds: Rect = metrics.bounds
                return bounds.height() - insetsHeight
            } else {
                val dm = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(dm)
                if (Math.abs(fixScreenHeight - dm.heightPixels) < 10) {
                    return dm.heightPixels
                }
                if (fixScreenHeight > 0) {
                    return fixScreenHeight - BarUtils.getFixStatusBarHeight()
                }
                return dm.heightPixels
            }
        }

    private var fixScreenHeight: Int = 0

    fun setFixScreenHeight(height: Int) {
        this.fixScreenHeight = height
    }

    val fullScreenHeight: Int
        get() {
            val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val metrics = windowManager.currentWindowMetrics
                // Gets all excluding insets
                val bounds: Rect = metrics.bounds
                return bounds.height()
            } else {
                val dm = DisplayMetrics()
                windowManager.defaultDisplay.getRealMetrics(dm)
                return dm.heightPixels
            }
        }

    @JvmStatic
    fun setLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    @JvmStatic
    fun setPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    val isLandscape: Boolean
        get() = Utils.getContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isPortrait: Boolean
        get() = Utils.getContext().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

}