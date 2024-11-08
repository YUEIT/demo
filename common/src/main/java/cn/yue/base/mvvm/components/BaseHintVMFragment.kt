package cn.yue.base.mvvm.components

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import cn.yue.base.R
import cn.yue.base.mvvm.BaseViewModel
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.utils.device.NetworkUtils
import cn.yue.base.view.PageStateView
import cn.yue.base.view.load.PageStatus

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BaseHintVMFragment<VM : BaseViewModel> : BaseVMFragment<VM>() {
    private lateinit var stateView: PageStateView

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_hint
    }

    abstract fun getContentLayoutId(): Int

    override fun initView(savedInstanceState: Bundle?) {
        stateView = requireViewById(R.id.stateView)
        stateView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                loadData()
            } else {
                viewModel.loader.setPageStatus(PageStatus.ERROR)
                showShortToast(R.string.app_no_net.getString())
            }
        }
        val contentId = getContentLayoutId()
        if (contentId != 0) {
            val baseVS = findViewById<ViewStub>(R.id.baseVS)
            baseVS?.layoutResource = getContentLayoutId()
            baseVS?.setOnInflateListener { _, inflated -> bindLayout(inflated) }
            baseVS?.inflate()
        }
    }

    open fun bindLayout(inflated: View) {}

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isAvailable()) {
            loadData()
        } else {
            viewModel.loader.setPageStatus(PageStatus.ERROR)
        }
    }

    /**
     * 默认情况下进入页面即显示正常状态，如需先显示加载，重新并改为PageStatus.REFRESH
     */
    open fun loadData() {
        viewModel.loader.setPageStatus(PageStatus.NORMAL)
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.loader.observe(this) { pageStatus, _ ->
            stateView.show(pageStatus)
        }
    }

    fun getPageStateView(): PageStateView {
        return stateView
    }
}