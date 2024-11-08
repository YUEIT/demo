package cn.yue.base.mvvm

import cn.yue.base.mvp.IStatusView
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class PullViewModel : BaseViewModel(), IStatusView {

    /**
     * 刷新
     */
    fun refresh() {
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
        loader.setPageStatus(PageStatus.REFRESH)
        loadData()
    }

    /**
     * 上拉刷新时
     */
    fun onLoadRefresh() {
        if (loader.isLoading()) {
            return
        }
        loader.setLoadStatus(LoadStatus.REFRESH)
        loadData()
    }

    abstract fun loadData()

    override fun changePageStatus(status: PageStatus) {
        loader.setPageStatus(status)
    }

    override fun changeLoadStatus(status: LoadStatus) {
        loader.setLoadStatus(status)
    }
}