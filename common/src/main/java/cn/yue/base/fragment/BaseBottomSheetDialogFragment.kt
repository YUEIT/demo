package cn.yue.base.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import cn.yue.base.activity.TransitionAnimation
import cn.yue.base.widget.dialog.BaseBottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Description:
 * Created by yue on 14/10/2024
 */
abstract class BaseBottomSheetDialogFragment : BaseDialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : BottomSheetDialog(requireContext(), theme) {
            override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
                if (listener == null) {
                    super.setOnCancelListener(null)
                }
            }

            override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
                if (listener == null) {
                    super.setOnDismissListener(null)
                }
            }

            fun setWeakOnCancelListener(listener: DialogInterface.OnCancelListener?) {
                super.setOnCancelListener(listener)
            }

            fun setWeakOnDismissListener(listener: DialogInterface.OnDismissListener?) {
                super.setOnDismissListener(listener)
            }
        }.apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.setSkipCollapsed(true)
        }
        dialog.setWeakOnCancelListener(onCancelListener)
        dialog.setWeakOnDismissListener(onDismissListener)
        return dialog
    }

    override fun getTransition(): Int {
        return TransitionAnimation.TRANSITION_BOTTOM
    }

    override fun dismiss() {
        try {
            if (tryDismissWithAnimation(false)) {
                return
            }
            super.dismiss()
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun dismissAllowingStateLoss() {
        try {
            if (tryDismissWithAnimation(true)) {
                return
            }
            super.dismissAllowingStateLoss()
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Tries to dismiss the dialog fragment with the bottom sheet animation. Returns true if possible,
     * false otherwise.
     */
    private fun tryDismissWithAnimation(allowingStateLoss: Boolean): Boolean {
        val baseDialog = dialog
        if (baseDialog is BaseBottomSheetDialog) {
            val behavior: BottomSheetBehavior<*> = baseDialog.getBehavior()
            if (behavior.isHideable && baseDialog.getDismissWithAnimation()) {
                dismissWithAnimation(behavior, allowingStateLoss)
                return true
            }
        }

        return false
    }

    private var waitingForDismissAllowingStateLoss = false

    private fun dismissWithAnimation(
        behavior: BottomSheetBehavior<*>, allowingStateLoss: Boolean
    ) {
        waitingForDismissAllowingStateLoss = allowingStateLoss

        if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            dismissAfterAnimation()
        } else {
            val mDialog = dialog
            if (mDialog is BaseBottomSheetDialog) {
                mDialog.removeDefaultCallback()
            }
            behavior.addBottomSheetCallback(BottomSheetDismissCallback())
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    private fun dismissAfterAnimation() {
        if (waitingForDismissAllowingStateLoss) {
            super.dismissAllowingStateLoss()
        } else {
            super.dismiss()
        }
    }

    private inner class BottomSheetDismissCallback : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismissAfterAnimation()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

}