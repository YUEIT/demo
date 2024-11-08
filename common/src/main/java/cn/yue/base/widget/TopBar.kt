package cn.yue.base.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.databinding.LayoutTopBarBinding
import cn.yue.base.utils.app.BarUtils


/**
 * Description :
 * Created by yue on 2019/3/8
 */
class TopBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {

    private val binding: LayoutTopBarBinding = LayoutTopBarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val layoutParams = binding.statusBarSpace.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = BarUtils.getFixStatusBarHeight()
        defaultStyle()
    }

    private fun defaultStyle() {
        setBackgroundColor(Color.parseColor("#f5f8ff"))
        binding.leftIV.visibility = View.GONE
        binding.leftTV.visibility = View.GONE
        binding.rightTV.visibility = View.GONE
        binding.rightIV.visibility = View.GONE
        binding.divider.visibility = View.GONE
        binding.centerIV.visibility = View.GONE
    }

    fun setBgColor(@ColorInt color: Int): TopBar {
        setBackgroundColor(color)
        return this
    }

    fun setBarVisibility(visible: Int): TopBar {
        visibility = visible
        return this
    }

    fun setContentVisibility(visibility: Int): TopBar {
        binding.barContentRL.visibility = visibility
        return this
    }

    fun setLeftTextStr(s: String?): TopBar {
        binding.leftTV.visibility = View.VISIBLE
        binding.leftTV.text = s
        return this
    }

    fun setLeftImage(@DrawableRes resId: Int): TopBar {
        binding.leftIV.visibility = View.VISIBLE
        binding.leftIV.setImageResource(resId)
        return this
    }

    fun setLeftImageTint(color: Int): TopBar {
        binding.leftIV.imageTintList = ColorStateList.valueOf(color)
        return this
    }

    fun setLeftClickListener(onClickListener: ((view: View) -> Unit)?): TopBar {
        binding.leftLL.setOnClickListener(onClickListener)
        return this
    }

    fun setCenterTextStr(s: String?): TopBar {
        binding.centerTV.visibility = View.VISIBLE
        binding.centerTV.text = s
        return this
    }

    fun setCenterClickListener(onClickListener: ((view: View) -> Unit)?): TopBar {
        binding.centerLL.setOnClickListener(onClickListener)
        return this
    }

    fun setRightTextStr(s: String?): TopBar {
        binding.rightTV.visibility = View.VISIBLE
        binding.rightTV.text = s
        return this
    }

    fun setRightImage(@DrawableRes resId: Int): TopBar {
        binding.rightIV.visibility = View.VISIBLE
        binding.rightIV.setImageResource(resId)
        return this
    }

    fun setRightCustomView(view: View): TopBar {
        binding.customRightFL.removeAllViews()
        binding.customRightFL.addView(view)
        return this
    }

    fun setRightClickListener(onClickListener: ((view: View) -> Unit)?): TopBar {
        binding.rightLL.setOnClickListener(onClickListener)
        return this
    }

    fun setLeftTextSize(sp: Float): TopBar {
        binding.leftTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setLeftTextColor(@ColorInt color: Int): TopBar {
        binding.leftTV.setTextColor(color)
        return this
    }

    fun setLeftTextFont(resId: Int): TopBar {
        binding.leftTV.typeface = ResourcesCompat.getFont(context, resId)
        return this
    }

    fun setLeftCustomView(view: View): TopBar {
        binding.customLeftFL.removeAllViews()
        binding.customLeftFL.addView(view)
        return this
    }

    fun setCenterTextSize(sp: Float): TopBar {
        binding.centerTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setCenterTextFont(resId: Int): TopBar {
        binding.centerTV.typeface = ResourcesCompat.getFont(context, resId)
        return this
    }

    fun setCenterTextColor(@ColorInt color: Int): TopBar {
        binding.centerTV.setTextColor(color)
        return this
    }

    fun setCenterImage(resId: Int): TopBar {
        if (resId == 0) {
            binding.centerIV.visibility = View.GONE
        } else {
            binding.centerIV.visibility = View.VISIBLE
            binding.centerIV.setImageResource(resId)
        }
        return this
    }

    fun setCenterCustomView(view: View): TopBar {
        binding.customCenterFL.removeAllViews()
        binding.customCenterFL.addView(view)
        return this
    }

    fun setRightTextSize(sp: Float): TopBar {
        binding.rightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setRightTextColor(@ColorInt color: Int): TopBar {
        binding.rightTV.setTextColor(color)
        return this
    }

    fun setRightTextFont(resId: Int): TopBar {
        binding.rightTV.typeface = ResourcesCompat.getFont(context, resId)
        return this
    }

    fun setDividerVisible(visible: Boolean): TopBar {
        binding.divider.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    fun setImmersiveTopBar(): TopBar {
        if (context is BaseFragmentActivity) {
            (context as BaseFragmentActivity).immersiveTopBar()
        }
        return this
    }
}