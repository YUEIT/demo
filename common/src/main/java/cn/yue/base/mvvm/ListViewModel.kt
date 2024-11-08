package cn.yue.base.mvvm

import cn.yue.base.mvvm.data.MutableListLiveData
import cn.yue.base.net.ResultException
import cn.yue.base.net.observer.BaseNetObserver
import cn.yue.base.net.observer.WrapperObserver
import cn.yue.base.net.wrapper.IListModel
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.disposables.Disposable

abstract class ListViewModel<P : IListModel<S>, S> : BaseViewModel() {

    private var pageNt: Int = 1
    private var lastNt: Int = 1
    //当接口返回总数时，为返回数量；接口未返回数量，为统计数量；
    var total = 0
    var dataLiveData = MutableListLiveData<S>()
    protected val dataList = ArrayList<S>()

    open fun initPageNt(): Int {
        return 1
    }

    open fun initPageSize(): Int {
        return 20
    }

    /**
     * 刷新
     */
    open fun refresh(isPageRefreshAnim: Boolean = loader.isFirstLoad) {
        if (loader.isLoading()) {
            return
        }
        if (isPageRefreshAnim) {
            loader.setPageStatus(PageStatus.REFRESH)
        } else {
            loader.setLoadStatus(LoadStatus.REFRESH)
        }
        pageNt = initPageNt()
        loadData()
    }

    /**
     * 上拉刷新时
     */
    fun loadRefresh() {
        if (loader.isLoading()) {
            return
        }
        if (loader.isFirstLoad) {
            loader.setPageStatus(PageStatus.REFRESH)
        } else {
            loader.setLoadStatus(LoadStatus.REFRESH)
        }
        pageNt = initPageNt()
        loadData()
    }

    /**
     * 下拉加载时
     */
    fun loadMoreData() {
        if (loader.getPageStatus() == PageStatus.NORMAL
            && loader.getLoadStatus() == LoadStatus.NORMAL) {
            loader.setLoadStatus(LoadStatus.LOAD_MORE)
            loadData()
        }
    }

    private fun loadData() {
        doLoadData(pageNt)
    }

    abstract fun doLoadData(nt: Int)

    fun Single<P>.defaultSubscribe() {
        this.compose(PageTransformer())
            .subscribe(WrapperObserver())
    }

    inner class PageTransformer : SingleTransformer<P, P> {
        override fun apply(upstream: Single<P>): SingleSource<P> {
            val pageObserver = getPageObserver()
            return upstream
                .compose(toBindLifecycle())
                .doOnSubscribe { pageObserver.onSubscribe(it) }
                .doOnSuccess { pageObserver.onSuccess(it) }
                .doOnError { pageObserver.onError(it) }
        }
    }

    inner class PageDelegateObserver(val observer: WrapperObserver<P>? = null)
        : WrapperObserver<P>() {

        private val pageObserver = getPageObserver()

        override fun onSubscribe(d: Disposable) {
            super.onSubscribe(d)
            pageObserver.onSubscribe(d)
            observer?.onSubscribe(d)
        }

        override fun onError(e: Throwable) {
            super.onError(e)
            pageObserver.onError(e)
            observer?.onError(e)
        }
        
        override fun onSuccess(t: P) {
            pageObserver.onSuccess(t)
            observer?.onSuccess(t)
        }
    }

    open inner class PageObserver: BaseNetObserver<P>() {

        override fun onSuccess(p: P) {
            val isRefresh = (loader.getPageStatus() === PageStatus.REFRESH
                    || loader.getLoadStatus() === LoadStatus.REFRESH)
            if (p.getList().isEmpty()) {
                if (pageNt > initPageNt()) {
                    //end
                    loader.setPageStatus(PageStatus.NORMAL)
                    loader.setLoadStatus(LoadStatus.END)
                } else {
                    //empty
                    total = 0
                    dataList.clear()
                    dataLiveData.setValue(dataList)
                    loader.setPageStatus(PageStatus.NO_DATA)
                    loader.setLoadStatus(LoadStatus.NORMAL)
                }
            } else {
                //success
                if (isRefresh) {
                    total = 0
                    dataList.clear()
                }
                loader.setPageStatus(PageStatus.NORMAL)
                if (p.getList().size < p.getPageSize()) {
                    loader.setLoadStatus(LoadStatus.END)
                } else {
                    loader.setLoadStatus(LoadStatus.NORMAL)
                }
                pageNt += 1
                if (p.getTotal() > 0) {
                    total = p.getTotal()
                } else {
                    total += p.getList().size
                }
                lastNt = pageNt
                dataList.addAll(p.getList())
                dataLiveData.setValue(dataList)
            }
        }

        override fun onException(e: ResultException) {
            pageNt = lastNt
            if (loader.isFirstLoad) {
                loader.setPageStatus(PageStatus.ERROR)
                loader.setLoadStatus(LoadStatus.ERROR)
            } else {
                loader.setLoadStatus(LoadStatus.ERROR)
            }
            showShortToast(e.message)
        }
    }


    open fun getPageObserver(): BaseNetObserver<P> {
        return PageObserver()
    }

    fun scrollToLoadMore(lastPosition: Int, spanCount: Int) {
        if (dataList.size <= 0) {
            return
        }
        if (lastPosition >= dataList.size - spanCount - 1) {
            loadMoreData()
        }
    }
}
