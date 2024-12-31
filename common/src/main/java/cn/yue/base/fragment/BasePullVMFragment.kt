package cn.yue.base.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.Observer
import cn.yue.base.R
import cn.yue.base.fragment.vm.PullViewModel
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.utils.device.NetworkUtils
import cn.yue.base.view.PageStateView
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
import cn.yue.base.view.refresh.IRefreshLayout

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BasePullVMFragment<VM : PullViewModel> : BaseVMFragment<VM>() {
    private lateinit var refreshL: IRefreshLayout
    private lateinit var stateView: PageStateView

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull
    }

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
        refreshL = findViewById<View>(R.id.refreshL) as IRefreshLayout
        refreshL.setOnRefreshListener {
            viewModel.onLoadRefresh()
        }
        refreshL.setRefreshEnable(canPullDown())
        if (canPullDown()) {
            stateView.setRefreshTarget(refreshL)
        }
        val contentId = getContentLayoutId()
        if (contentId != 0) {
            val baseVS = findViewById<ViewStub>(R.id.baseVS)
            baseVS?.layoutResource = getContentLayoutId()
            baseVS?.setOnInflateListener { _, inflated ->
                bindLayout(inflated)
            }
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

    fun loadData() {
        viewModel.refresh()
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.loader.observePage(this, Observer { pageStatus ->
            stateView.show(pageStatus)
        })
        viewModel.loader.observeLoad(this, Observer { loadStatus ->
            if (loadStatus === LoadStatus.REFRESH) {
                refreshL.startRefresh()
            } else {
                refreshL.finishRefreshingState()
            }
        })
    }

    abstract fun getContentLayoutId(): Int

    open fun canPullDown(): Boolean {
        return true
    }

    fun getPageStateView(): PageStateView {
        return stateView
    }
}