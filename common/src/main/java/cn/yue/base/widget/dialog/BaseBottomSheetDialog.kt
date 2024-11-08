package cn.yue.base.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import cn.yue.base.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.motion.MaterialBackOrchestrator

/**
 * Description:
 * Created by yue on 14/10/2024
 */
open class BaseBottomSheetDialog(context: Context, theme: Int = 0) : AppCompatDialog(context, theme) {
    private var behavior: BottomSheetBehavior<FrameLayout>? = null

    private var container: FrameLayout? = null
    private var bottomSheet: FrameLayout? = null

    private var mCancelable: Boolean = true
    private var canceledOnTouchOutside = true
    private var canceledOnTouchOutsideSet = false
    private var dismissWithAnimation: Boolean = false
    @SuppressLint("RestrictedApi")
    private var backOrchestrator: MaterialBackOrchestrator? = null

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(wrapInBottomSheet(layoutResID, null, null)!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        if (window != null) {
            window.decorView.setPadding(0, 0, 0, 0)
            window.setBackgroundDrawable(ColorDrawable())
        }
    }

    override fun setContentView(view: View) {
        super.setContentView(wrapInBottomSheet(0, view, null)!!)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(wrapInBottomSheet(0, view, params)!!)
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        if (this.mCancelable != cancelable) {
            this.mCancelable = cancelable
            behavior?.isHideable = cancelable
            if (window != null) {
                updateListeningForBackCallbacks()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (behavior?.state == BottomSheetBehavior.STATE_HIDDEN) {
            behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onDetachedFromWindow() {
        backOrchestrator?.stopListeningForBackCallbacks()
    }

    override fun cancel() {
        val behavior = getBehavior()
        if (!dismissWithAnimation || behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            super.cancel()
        } else {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        super.setCanceledOnTouchOutside(cancel)
        if (cancel && !mCancelable) {
            mCancelable = true
        }
        canceledOnTouchOutside = cancel
        canceledOnTouchOutsideSet = true
    }

    /**
     * Set to perform the swipe down animation when dismissing instead of the window animation for the
     * dialog.
     *
     * @param dismissWithAnimation True if swipe down animation should be used when dismissing.
     */
    fun setDismissWithAnimation(dismissWithAnimation: Boolean) {
        this.dismissWithAnimation = dismissWithAnimation
    }

    /**
     * Returns if dismissing will perform the swipe down animation on the bottom sheet, rather than
     * the window animation for the dialog.
     */
    fun getDismissWithAnimation(): Boolean {
        return dismissWithAnimation
    }

    fun getBehavior(): BottomSheetBehavior<FrameLayout> {
        if (behavior == null) {
            // The content hasn't been set, so the behavior doesn't exist yet. Let's create it.
            ensureContainerAndBehavior()
        }
        return behavior!!
    }


    /** Creates the container layout which must exist to find the behavior  */
    @SuppressLint("RestrictedApi")
    private fun ensureContainerAndBehavior(): FrameLayout? {
        if (container == null) {
            container = View.inflate(context, R.layout.dialog_base_bottom_sheet, null) as FrameLayout
            bottomSheet = container!!.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
            behavior = BottomSheetBehavior.from(bottomSheet!!).apply {
                isHideable = mCancelable
                skipCollapsed = true
                addBottomSheetCallback(bottomSheetCallback)
            }
            container!!.post {
                behavior!!.setPeekHeight(container!!.measuredHeight)
            }
            backOrchestrator = MaterialBackOrchestrator(behavior!!, bottomSheet!!)
        }
        return container
    }

    private fun wrapInBottomSheet(
        layoutResId: Int, view: View?, params: ViewGroup.LayoutParams?
    ): View? {
        var contentView = view
        ensureContainerAndBehavior()
        val coordinator = container!!.findViewById<View>(R.id.coordinator) as CoordinatorLayout
        if (layoutResId != 0 && view == null) {
            contentView = layoutInflater.inflate(layoutResId, coordinator, false)
        }

        bottomSheet!!.removeAllViews()
        if (params == null) {
            bottomSheet!!.addView(contentView)
        } else {
            bottomSheet!!.addView(contentView, params)
        }
        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        coordinator.findViewById<View>(R.id.touch_outside)
            .setOnClickListener {
                if (mCancelable && isShowing && shouldWindowCloseOnTouchOutside()) {
                    cancel()
                }
            }
        // Handle accessibility events
        ViewCompat.setAccessibilityDelegate(
            bottomSheet!!,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View, info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    if (mCancelable) {
                        info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS)
                        info.isDismissable = true
                    } else {
                        info.isDismissable = false
                    }
                }

                override fun performAccessibilityAction(
                    host: View,
                    action: Int,
                    args: Bundle?
                ): Boolean {
                    if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && mCancelable) {
                        cancel()
                        return true
                    }
                    return super.performAccessibilityAction(host, action, args)
                }
            })
        bottomSheet!!.setOnTouchListener { _, _ ->
            true
        }
        return container
    }

    @SuppressLint("RestrictedApi")
    private fun updateListeningForBackCallbacks() {
        if (mCancelable) {
            backOrchestrator?.startListeningForBackCallbacks()
        } else {
            backOrchestrator?.stopListeningForBackCallbacks()
        }
    }

    fun shouldWindowCloseOnTouchOutside(): Boolean {
        if (!canceledOnTouchOutsideSet) {
            val a =
                context.obtainStyledAttributes(intArrayOf(android.R.attr.windowCloseOnTouchOutside))
            canceledOnTouchOutside = a.getBoolean(0, true)
            a.recycle()
            canceledOnTouchOutsideSet = true
        }
        return canceledOnTouchOutside
    }

    fun removeDefaultCallback() {
        behavior!!.removeBottomSheetCallback(bottomSheetCallback)
    }

    private val bottomSheetCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(
            bottomSheet: View, @BottomSheetBehavior.State newState: Int
        ) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                cancel()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }
}

