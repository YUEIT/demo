package cn.yue.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import androidx.constraintlayout.widget.ConstraintLayout
import cn.yue.base.R
import cn.yue.base.databinding.LayoutScaffoldBinding

/**
 * Description:
 * Created by yue on 21/11/24
 */
class ScaffoldLayout(context: Context, attributeSet: AttributeSet? = null)
    : ConstraintLayout(context, attributeSet) {

    val binding = LayoutScaffoldBinding.inflate(LayoutInflater.from(context), this, true)

    private var topBarView: TopBarView = TopBarView(context)
    private var bottomBarView: BottomBarView = BottomBarView(context)

    init {
        binding.vTop.addView(topBarView)
        binding.vBottom.addView(bottomBarView)
    }

    fun getTopBar(): TopBarView {
        return topBarView
    }

    fun getBottomBar(): BottomBarView {
        return bottomBarView
    }

    fun customTopBar(view: View?) {
        binding.vTop.removeAllViews()
        binding.vTop.addView(view)
    }

    fun customBottomBar(view: View?) {
        binding.vBottom.removeAllViews()
        binding.vBottom.addView(view)
    }

    fun removeTopBar() {
        binding.vTop.removeAllViews()
    }

    fun removeBottomBar() {
        binding.vBottom.removeAllViews()
    }

    fun setContentView(resId: Int) {
        if (resId != 0) {
            val baseVS = findViewById<ViewStub>(R.id.content)
            baseVS?.layoutResource = resId
            baseVS?.inflate()
        }
    }
}