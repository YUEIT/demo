package cn.yue.base.widget.keyboard

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import cn.yue.base.R
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.utils.app.DisplayUtils
import cn.yue.base.utils.code.SPUtils
import cn.yue.base.utils.device.KeyboardUtils


/**
 * Description :
 * Created by yue on 2018/11/14
 */
class KeyboardLayout(context: Context, attrs: AttributeSet?)
    : ConstraintLayout(context, attrs) {

    companion object {
        const val TYPE_INPUT = 0
        const val TYPE_EMOTION = 1
    }

    private var keyboardOpen = false
    private var contentType = 0
    private var keyboardHeight = 0
    private var navHeight = 0


    private fun initView() {
        if (isInEditMode) {
            return
        }
        BarUtils.fixSystemBarInsetsWithKeyboard(getContext() as Activity) { _, s, n, k ->
            navHeight = n
            if (k > 0) {
                keyboardHeight = k
                onKeyboardOpen()
            } else {
                onKeyboardClose()
            }
        }
        getSwitchView()?.setOnClickListener {
            toggleContentShow()
        }
        keyboardHeight = SPUtils.getInstance().getInt("keyboardHeight")
        if (keyboardHeight <= 0) {
            keyboardHeight = DisplayUtils.dip2px(300)
        }
    }

    fun getContainerView(): View {
        return findViewById(R.id.id_keyboard_container)
    }

    fun getEmotionView(): View? {
        return findViewById(R.id.id_keyboard_emotion)
    }

    fun getInputView(): EditText? {
        return findViewById(R.id.id_keyboard_input)
    }

    fun getSwitchView(): View? {
        return findViewById(R.id.id_keyboard_switch)
    }

    fun onKeyboardOpen() {
        keyboardOpen = true
        contentType = TYPE_INPUT
        getEmotionView()?.visibility = View.GONE
        val mLayoutParams = getContainerView().layoutParams
        mLayoutParams.height = keyboardHeight
        getContainerView().layoutParams = mLayoutParams
    }

    fun onKeyboardClose() {
        keyboardOpen = false
        if (contentType == TYPE_EMOTION) {
            getEmotionView()?.visibility = View.VISIBLE
        } else {
            getEmotionView()?.visibility = View.GONE
            val mLayoutParams = getContainerView().layoutParams
            mLayoutParams.height = navHeight
            getContainerView().layoutParams = mLayoutParams
        }
    }

    fun toggleContentShow() {
        if (contentType == TYPE_INPUT) {
            contentType = TYPE_EMOTION
            if (keyboardOpen) {
                KeyboardUtils.hideSoftInput(getInputView())
            } else {
                val mLayoutParams = getContainerView().layoutParams
                mLayoutParams.height = keyboardHeight
                getContainerView().layoutParams = mLayoutParams
                getEmotionView()?.visibility = View.VISIBLE
            }
        } else {
            contentType = TYPE_INPUT
            KeyboardUtils.showSoftInput(getInputView())
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        SPUtils.getInstance().put("keyboardHeight", keyboardHeight)
    }
}
