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

    private val binding = LayoutTopBarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        defaultStyle()
    }

    private fun defaultStyle() {
        binding.ivLeft.visibility = View.GONE
        binding.tvLeft.visibility = View.GONE
        binding.tvRight.visibility = View.GONE
        binding.ivRight.visibility = View.GONE
        binding.divider.visibility = View.GONE
        binding.ivCenter.visibility = View.GONE
    }

    fun setBgColor(@ColorInt color: Int): TitleBarView {
        setBackgroundColor(color)
        return this
    }

    fun setBarVisibility(visible: Int): TitleBarView {
        visibility = visible
        return this
    }

    fun setLeftTextStr(s: String?): TitleBarView {
        binding.tvLeft.visibility = View.VISIBLE
        binding.tvLeft.text = s
        return this
    }

    fun setLeftImage(@DrawableRes resId: Int): TitleBarView {
        binding.ivLeft.visibility = View.VISIBLE
        binding.ivLeft.setImageResource(resId)
        return this
    }

    fun setLeftImageTint(color: Int): TitleBarView {
        binding.ivLeft.imageTintList = ColorStateList.valueOf(color)
        return this
    }

    fun setLeftClickListener(onClickListener: ((view: View) -> Unit)?): TitleBarView {
        binding.llLeft.setOnClickListener(onClickListener)
        return this
    }

    fun setCenterTextStr(s: String?): TitleBarView {
        binding.tvCenter.visibility = View.VISIBLE
        binding.tvCenter.text = s
        return this
    }

    fun setCenterClickListener(onClickListener: ((view: View) -> Unit)?): TitleBarView {
        binding.llCenter.setOnClickListener(onClickListener)
        return this
    }

    fun setRightTextStr(s: String?): TitleBarView {
        binding.tvRight.visibility = View.VISIBLE
        binding.tvRight.text = s
        return this
    }

    fun setRightImage(@DrawableRes resId: Int): TitleBarView {
        binding.ivRight.visibility = View.VISIBLE
        binding.ivRight.setImageResource(resId)
        return this
    }

    fun setRightClickListener(onClickListener: ((view: View) -> Unit)?): TitleBarView {
        binding.llRight.setOnClickListener(onClickListener)
        return this
    }

    fun setLeftTextSize(sp: Float): TitleBarView {
        binding.tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setLeftTextColor(@ColorInt color: Int): TitleBarView {
        binding.tvLeft.setTextColor(color)
        return this
    }

    fun setLeftTextFont(resId: Int): TitleBarView {
        binding.tvLeft.typeface = ResourcesCompat.getFont(context, resId)
        return this
    }

    fun setCenterTextSize(sp: Float): TitleBarView {
        binding.tvCenter.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setCenterTextFont(resId: Int): TitleBarView {
        binding.tvCenter.typeface = ResourcesCompat.getFont(context, resId)
        return this
    }

    fun setCenterTextColor(@ColorInt color: Int): TitleBarView {
        binding.tvCenter.setTextColor(color)
        return this
    }

    fun setCenterImage(resId: Int): TitleBarView {
        if (resId == 0) {
            binding.ivCenter.visibility = View.GONE
        } else {
            binding.ivCenter.visibility = View.VISIBLE
            binding.ivCenter.setImageResource(resId)
        }
        return this
    }

    fun setRightTextSize(sp: Float): TitleBarView {
        binding.tvRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        return this
    }

    fun setRightTextColor(@ColorInt color: Int): TitleBarView {
        binding.tvRight.setTextColor(color)
        return this
    }

    fun setRightTextFont(resId: Int): TitleBarView {
        binding.tvRight.typeface = ResourcesCompat.getFont(context, resId)
        return this
    }

    fun setDividerVisible(visible: Boolean): TitleBarView {
        binding.divider.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }
}