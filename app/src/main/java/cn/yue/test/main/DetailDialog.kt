package cn.yue.test.main

import android.os.Bundle
import android.view.Window
import cn.yue.base.activity.BaseBottomSheetDialogFragment
import cn.yue.base.utils.app.BarUtils
import cn.yue.test.R

/**
 * Description:
 * Created by yue on 20/11/24
 */
class DetailDialog : BaseBottomSheetDialogFragment() {

    override fun getLayoutId(): Int {
        return R.layout.dialog_detail
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun setWindowParams(window: Window) {
        super.setWindowParams(window)
        BarUtils.fullScreenPop(window)
    }
}