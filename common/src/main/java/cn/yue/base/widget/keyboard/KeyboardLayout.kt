package cn.yue.base.widget.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
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
        BarUtils.fixWindowInsets(this) { v, insets ->
            navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            setPadding(0, 0, 0, navHeight)
            insets
        }
        ViewCompat.setWindowInsetsAnimationCallback(this,
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {

                var beginHeight = 0
                var isAnimToOpen = false

                override fun onStart(
                    animation: WindowInsetsAnimationCompat,
                    bounds: WindowInsetsAnimationCompat.BoundsCompat
                ): WindowInsetsAnimationCompat.BoundsCompat {
                    val insets = ViewCompat.getRootWindowInsets(this@KeyboardLayout) ?: return bounds
                    val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                    if (imeHeight > 0) {
                        isAnimToOpen = true
                        keyboardHeight = imeHeight
                        onKeyboardOpen()
                    } else {
                        isAnimToOpen = false
                        onKeyboardClose()
                    }
                    beginHeight = getContainerView().layoutParams.height
                    return bounds
                }

                // Override methods.
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    // Find an IME animation.
                    val imeAnimation = runningAnimations.find {
                        it.typeMask and WindowInsetsCompat.Type.ime() != 0
                    } ?: return insets
                    val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                    if (contentType == TYPE_INPUT) {
                        val mLayoutParams = getContainerView().layoutParams
                        var adjustHeight = 0
                        if (isAnimToOpen) {
                            if (beginHeight > keyboardHeight - navHeight) {
                                val ads = beginHeight - keyboardHeight + navHeight
                                adjustHeight = keyboardHeight - navHeight + (ads * (1 - imeAnimation.interpolatedFraction)).toInt()
                                mLayoutParams.height = adjustHeight
                            } else {
                                if (mLayoutParams.height < imeHeight - navHeight) {
                                    adjustHeight = imeHeight - navHeight
                                    if (adjustHeight < 0) {
                                        adjustHeight = 0
                                    }
                                    mLayoutParams.height = adjustHeight
                                }
                            }
                        } else {
                            adjustHeight = imeHeight - navHeight
                            if (adjustHeight < 0) {
                                adjustHeight = 0
                            }
                            mLayoutParams.height = adjustHeight
                        }
                        getContainerView().layoutParams = mLayoutParams
                    }
                    return insets
                }

                override fun onEnd(animation: WindowInsetsAnimationCompat) {
                    super.onEnd(animation)

                }
            }
        )
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
    }

    fun onKeyboardClose() {
        keyboardOpen = false
        if (contentType == TYPE_EMOTION) {
            getEmotionView()?.visibility = View.VISIBLE
        } else {
            getEmotionView()?.visibility = View.GONE
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
