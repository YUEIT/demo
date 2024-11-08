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

/**
 * Description :
 * Created by yue on 2019/3/8
 */
abstract class BaseHintFragment : BaseFragment(), IBaseView {
    var loader = Loader()
    private lateinit var stateView: PageStateView

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_hint
    }

    override fun initView(savedInstanceState: Bundle?) {
        stateView = requireViewById(R.id.stateView)
        stateView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                loadData()
            } else {
                showShortToast(R.string.app_no_net.getString())
            }
        }
        val baseVS = findViewById<ViewStub>(R.id.baseVS)
        baseVS?.layoutResource = getContentLayoutId()
        baseVS?.setOnInflateListener { _, inflated -> bindLayout(inflated) }
        baseVS?.inflate()
    }

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isAvailable()) {
            loadData()
        } else {
            changePageStatus(PageStatus.ERROR)
        }
    }

    /**
     * 默认情况下进入页面即显示正常状态，如需先显示加载，重新并改为PageStatus.REFRESH
     */
    open fun loadData() {
        changePageStatus(PageStatus.NORMAL)
    }

    abstract fun getContentLayoutId(): Int

    open fun bindLayout(inflated: View) {}

    fun getPageStateView(): PageStateView {
        return stateView
    }

    override fun changePageStatus(status: PageStatus) {
        stateView.show(loader.setPageStatus(status))
    }

    override fun changeLoadStatus(status: LoadStatus) {

    }

    override fun showWaitDialog(title: String?) {
        super.showWaitDialog(title)
    }

    override fun dismissWaitDialog() {
        super.dismissWaitDialog()
    }
}