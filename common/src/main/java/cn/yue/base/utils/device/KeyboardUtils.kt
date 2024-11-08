package cn.yue.base.utils.device

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import cn.yue.base.utils.Utils
import cn.yue.base.utils.app.BarUtils
import kotlin.math.abs

object KeyboardUtils {

    /**
     * 避免输入法面板遮挡
     *
     * 在manifest.xml中activity中设置
     *
     * android:windowSoftInputMode="adjustPan"
     */

    /**
     * 动态隐藏软键盘
     *
     * @param activity activity
     */
    @JvmStatic
    fun hideSoftInput(activity: Activity) {
        var view = activity.currentFocus
        if (view == null) view = View(activity)
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 动态隐藏软键盘
     *
     * @param context 上下文
     * @param view    视图
     */
    @JvmStatic
    fun hideSoftInput(view: View?) {
        if (view == null) {
            return
        }
        val imm = Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 动态隐藏软键盘
     *
     * @param context 上下文
     * @param view    视图
     */
    @JvmStatic
    fun hideSoftInput(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * 点击屏幕空白区域隐藏软键盘
     *
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
     *
     * 需重写dispatchTouchEvent
     *
     * 参照以下注释代码
     */
    @JvmStatic
    fun clickBlankArea2HideSoftInput() {
        Log.d("tips", "U should copy the following code.")
        /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }
    */
    }

    /**
     * 动态显示软键盘
     *
     * @param edit 输入框
     */
    @JvmStatic
    fun showSoftInput(edit: EditText?) {
        if (edit == null) {
            return
        }
        edit.isFocusable = true
        edit.isFocusableInTouchMode = true
        edit.requestFocus()
        val imm = Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        imm.showSoftInput(edit, 0)
    }

    /**
     * 切换键盘显示与否状态
     */
    @JvmStatic
    fun toggleSoftInput() {
        val imm = Utils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                ?: return
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    @JvmStatic
    fun isFullScreen(activity: Activity): Boolean {
        return activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
    }

    private const val TAG_ON_GLOBAL_LAYOUT_LISTENER = -8

    fun registerSoftInputChangedListener(
        activity: ComponentActivity,
        block: (height : Int) -> Unit
    ) {
        registerSoftInputChangedListener(activity.lifecycle, activity.window, TAG_ON_GLOBAL_LAYOUT_LISTENER, block)
    }

    fun registerSoftInputChangedListener(
        activity: ComponentActivity,
        tagInt: Int,
        block: (height : Int) -> Unit
    ) {
        registerSoftInputChangedListener(activity.lifecycle, activity.window, tagInt, block)
    }

    fun registerSoftInputChangedListener(
        lifecycle: Lifecycle?,
        window: Window,
        tagInt: Int,
        block: (height : Int) -> Unit
    ) {
        lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                unregisterSoftInputChangedListener(window)
            }
        })
        val flags = window.attributes.flags
        if ((flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        val contentView = window.findViewById<FrameLayout>(R.id.content)
        val decorViewInvisibleHeightPre = intArrayOf(getDecorViewInvisibleHeight(window.decorView, contentView))
        val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val height: Int = getDecorViewInvisibleHeight(window.decorView, contentView)
            if (decorViewInvisibleHeightPre[0] != height) {
                block.invoke(height)
                decorViewInvisibleHeightPre[0] = height
            }
        }
        contentView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
        contentView.setTag(tagInt, onGlobalLayoutListener)
    }


    /**
     * Unregister soft input changed listener.
     *
     * @param window The window.
     */
    fun unregisterSoftInputChangedListener(window: Window) {
        unregisterSoftInputChangedListener(
            window,
            TAG_ON_GLOBAL_LAYOUT_LISTENER
        )
    }

    /**
     * Unregister soft input changed listener.
     *
     * @param window The window.
     */
    fun unregisterSoftInputChangedListener(window: Window, tagInt: Int) {
        val contentView = window.findViewById<View>(R.id.content) ?: return
        val tag = contentView.getTag(tagInt)
        if (tag is OnGlobalLayoutListener) {
            contentView.viewTreeObserver.removeOnGlobalLayoutListener(tag as OnGlobalLayoutListener)
            //这里会发生内存泄漏 如果不设置为null
            contentView.setTag(tagInt, null)
        }
    }

    private var sDecorViewDelta = 0

    private fun getDecorViewInvisibleHeight(decorView: View, contentView: View): Int {

        val outRect = Rect()
        decorView.getWindowVisibleDisplayFrame(outRect)
        val delta = abs((decorView.bottom - outRect.bottom).toDouble()).toInt()
        if (delta <= BarUtils.getNavigationBarHeight() + BarUtils.getStatusBarHeight()) {
            sDecorViewDelta = delta
            return 0
        }
        return if (decorView.bottom == contentView.bottom) {
            delta
        } else {
            delta - sDecorViewDelta
        }
    }

}