package cn.yue.base.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import cn.yue.base.databinding.LayoutTopBarBinding

class TitleBarView(context: Context, attributeSet: AttributeSet? = null)
    : FrameLayout(context, attributeSet) {

    private val binding: LayoutTopBarBinding = LayoutTopBarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        defaultStyle()
    }

    private fun defaultStyle() {
        binding.leftIV.visibility = View.GONE
        binding.leftTV.visibility = View.GONE
        binding.rightTV.visibility = View.GONE
        binding.rightIV.visibility = View.GONE
        binding.divider.visibility = View.GONE
        binding.centerIV.visibility = View.GONE
    }

    fun setBgColor(@ColorInt color: Int): TitleBarView {
        setBackgroundColor(color)
        return this
    }

    fun setBarVisibility(visible: Int): TitleBarView {
        visibility = visible
        return this
    }

    fun setContentVisibility(visibility: Int): TitleBarView {
        binding.barContentRL.visibility = visibility
        return this
    }

    fun setLeftTextStr(s: String?): TitleBarView {
        binding.leftTV.visibility = View.VISIBLE
        binding.leftTV.text = s
        return this
    }

    fun setLeftImage(@DrawableRes resId: Int): TitleBarView {
        binding.leftIV.visibility = View.VISIBLE
        binding.leftIV.setImageResource(resId)
        return this
    }

    fun setLeftImageTint(color: Int): TitleBarView {
        binding.leftIV.imageTintList = ColorStateList.valueOf(color)
        return this
    }

    fun setLeftClickListener(onClickListener: ((view: View) -> Unit)?): TitleBarView {
        binding.leftLL.setOnClickListener(onClickListener)
        return this
    }

    fun setCenterTextStr(s: String?): TitleBarView {
        binding.centerTV.visibility = View.VISIBLE
        binding.centerTV.text = s
        return this
    }

    fun setCenterClickListener(onClickListener: ((view: View) -> Unit)?): TitleBarView {
        binding.centerLL.setOnClickListener(onClickListener)
        return this
    }

    fun setRightTextStr(s: String?): TitleBarView {
        binding.rightTV.visibility = View.VISIBLE
        binding.rightTV.text = s
        return this
    }

    fun setRightImage(@DrawableRes resId: Int): TitleBarView {
        binding.rightIV.visibility = View.VISIBLE
        binding.rightIV.setImageResource(resId)
        return this
    }

    fun setRightCustomView(view: View): TitleBarView {
        binding.customRightFL.removeAllViews()
        binding.customRightFL.addView(view)
        return this
    }

    fun setRightClickListener(onClickListener: ((view: View) -> Unit)?): TitleBarView {
        binding.rightLL.setOnClickListener(onClickListener)
        return this
    }

    fun setLeftTextSize(sp: Float): TitleBarView {
        binding.leftTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setLeftTextColor(@ColorInt color: Int): TitleBarView {
        binding.leftTV.setTextColor(color)
        return this
    }

    fun setLeftTextFont(resId: Int): TitleBarView {
        binding.leftTV.typeface = ResourcesCompat.getFont(context, resId)
        return this
    }

    fun setLeftCustomView(view: View): TitleBarView {
        binding.customLeftFL.removeAllViews()
        binding.customLeftFL.addView(view)
        return this
    }

    fun setCenterTextSize(sp: Float): TitleBarView {
        binding.centerTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setCenterTextFont(resId: Int): TitleBarView {
        binding.centerTV.typeface = ResourcesCompat.getFont(context, resId)
        return this
    }

    fun setCenterTextColor(@ColorInt color: Int): TitleBarView {
        binding.centerTV.setTextColor(color)
        return this
    }

    fun setCenterImage(resId: Int): TitleBarView {
        if (resId == 0) {
            binding.centerIV.visibility = View.GONE
        } else {
            binding.centerIV.visibility = View.VISIBLE
            binding.centerIV.setImageResource(resId)
        }
        return this
    }

    fun setCenterCustomView(view: View): TitleBarView {
        binding.customCenterFL.removeAllViews()
        binding.customCenterFL.addView(view)
        return this
    }

    fun setRightTextSize(sp: Float): TitleBarView {
        binding.rightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setRightTextColor(@ColorInt color: Int): TitleBarView {
        binding.rightTV.setTextColor(color)
        return this
    }

    fun setRightTextFont(resId: Int): TitleBarView {
        binding.rightTV.typeface = ResourcesCompat.getFont(context, resId)
        return this
    }

    fun setDividerVisible(visible: Boolean): TitleBarView {
        binding.divider.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }
}