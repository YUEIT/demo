package cn.yue.base.mvp.components

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import cn.yue.base.R
import cn.yue.base.activity.BaseFragment
import cn.yue.base.mvp.IBaseView
import cn.yue.base.mvp.components.data.Loader
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.utils.device.NetworkUtils
import cn.yue.base.view.PageStateView
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
import cn.yue.base.view.refresh.IRefreshLayout
import cn.yue.base.widget.dialog.WaitDialog

/**
 * Description :
 * Created by yue on 2019/3/7
 */
abstract class BasePullFragment : BaseFragment(), IBaseView {
    private val loader = Loader()
    private var refreshL: IRefreshLayout? = null
    private lateinit var stateView: PageStateView

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull
    }

    override fun initView(savedInstanceState: Bundle?) {
        stateView = requireViewById(R.id.stateView)
        stateView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                refresh()
            } else {
                showShortToast(R.string.app_no_net.getString())
            }
        }
        refreshL = (findViewById<View>(R.id.refreshL) as IRefreshLayout?)
        refreshL?.setOnRefreshListener {
            onLoadRefresh()
        }
        refreshL?.setRefreshEnable(canPullDown())
        if (canPullDown()) {
            stateView.setRefreshTarget(refreshL)
        }
        val baseVS = findViewById<ViewStub>(R.id.baseVS)
        baseVS?.layoutResource = getContentLayoutId()
        baseVS?.setOnInflateListener { _, inflated ->
            bindLayout(inflated)
        }
        baseVS?.inflate()
    }

    open fun bindLayout(inflated: View) {}

    override fun initOther() {
        if (NetworkUtils.isAvailable()) {
            refresh()
        } else {
            changePageStatus(PageStatus.ERROR)
        }
    }

    abstract fun getContentLayoutId(): Int

    //回调继承 BasePullObserver 以适应加载逻辑
    abstract fun loadData()

    open fun canPullDown(): Boolean {
        return true
    }

    /**
     * 刷新
     */
    open fun refresh() {
        if (loader.isLoading()) {
            return
        }
        if (loader.isFirstLoad) {
            onPageRefresh()
        } else {
            onLoadRefresh()
        }
    }

    /**
     * 全局页面刷新
     */
    fun onPageRefresh() {
        if (loader.isLoading()) {
            return
        }
        changePageStatus(PageStatus.REFRESH)
        loadData()
    }

    /**
     * 上拉刷新时
     */
    fun onLoadRefresh() {
        if (loader.isLoading()) {
            return
        }
        changeLoadStatus(LoadStatus.REFRESH)
        loadData()
    }

    private fun showStatusView(status: PageStatus) {
        if (loader.isFirstLoad) {
            stateView.show(status)
        } else {
            if (status == PageStatus.NO_DATA) {
                stateView.show(status)
                loader.isFirstLoad = true
            } else {
                stateView.show(PageStatus.NORMAL)
            }
        }
        if (status == PageStatus.NORMAL) {
            loader.isFirstLoad = false
        }
    }

    override fun changePageStatus(status: PageStatus) {
        showStatusView(loader.setPageStatus(status))
        refreshL?.finishRefreshingState()
    }

    override fun changeLoadStatus(status: LoadStatus) {
        loader.setLoadStatus(status)
        if (status === LoadStatus.REFRESH) {
            refreshL?.startRefresh()
        } else {
            refreshL?.finishRefreshingState()
        }
    }

    private var waitDialog: WaitDialog? = null
    override fun showWaitDialog(title: String?) {
        if (waitDialog == null) {
            waitDialog = WaitDialog(mActivity)
        }
        waitDialog?.show(title)
    }

    override fun dismissWaitDialog() {
        if (waitDialog != null && waitDialog!!.isShowing()) {
            waitDialog?.cancel()
        }
    }

}