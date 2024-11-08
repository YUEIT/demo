package cn.yue.base.mvvm.components

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.yue.base.R
import cn.yue.base.mvp.components.BaseFooter
import cn.yue.base.mvvm.ListViewModel
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.utils.device.NetworkUtils
import cn.yue.base.view.PageStateView
import cn.yue.base.view.load.IFooter
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
import cn.yue.base.view.refresh.IRefreshLayout

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BaseListVMFragment<VM : ListViewModel<*, *>, S> : BaseVMFragment<VM>() {
    private var adapter: RecyclerView.Adapter<*>? = null
    private lateinit var refreshL: IRefreshLayout
    private lateinit var stateView: PageStateView
    protected lateinit var footer: IFooter

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull_page
    }

    override fun initView(savedInstanceState: Bundle?) {
        stateView = requireViewById(R.id.stateView)
        stateView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                if (autoRefresh()) {
                    viewModel.loadRefresh()
                }
            } else {
                showShortToast(R.string.app_no_net.getString())
            }
        }
        refreshL = findViewById<View>(R.id.refreshL) as IRefreshLayout
        refreshL.setRefreshEnable(canPullDown())
        refreshL.setOnRefreshListener {
            viewModel.loadRefresh()
        }
        if (canPullDown()) {
            stateView.setRefreshTarget(refreshL)
        }
        val baseRV = requireViewById<RecyclerView>(R.id.baseRV)
        refreshL.setTargetView(baseRV)
        initRecyclerView(baseRV)
        addOnScrollListener(baseRV)
    }

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isAvailable()) {
            if (autoRefresh()) {
                viewModel.loadRefresh()
            }
        } else {
            viewModel.loader.setPageStatus(PageStatus.ERROR)
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.dataLiveData.observe(this, Observer { list ->
            setData(list as ArrayList<S>)
        })
        viewModel.loader.observe(this) { pageStatus, loadStatus ->
            stateView.show(pageStatus)
            if (loadStatus == LoadStatus.REFRESH) {
                refreshL.startRefresh()
            } else {
                refreshL.finishRefreshingState()
            }
            footer.showStatusView(loadStatus)
            refreshL.showLoadMoreEnd(loadStatus == LoadStatus.END)
        }
    }

    open fun autoRefresh(): Boolean {
        return true
    }

    open fun canPullDown(): Boolean {
        return true
    }

    open fun initFooter(): IFooter {
        val baseFooter = BaseFooter(mActivity)
        baseFooter.setOnReloadListener {
            viewModel.loadMoreData()
        }
        return baseFooter
    }

    open fun initRecyclerView(baseRV: RecyclerView) {
        baseRV.layoutManager = getLayoutManager()
        baseRV.adapter = initAdapter().also { adapter = it }
        initFooter().also { footer = it }
    }

    private fun addOnScrollListener(baseRV: RecyclerView) {
        baseRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    return
                }
                when (val layoutManager = recyclerView.layoutManager) {
                    is GridLayoutManager -> {
                        val endPosition = layoutManager.findLastVisibleItemPosition()
                        val spanCount = layoutManager.spanCount
                        viewModel.scrollToLoadMore(endPosition, spanCount)
                    }

                    is StaggeredGridLayoutManager -> {
                        val lastSpan = layoutManager.findLastVisibleItemPositions(null)
                        var endPosition = 0
                        for (position in lastSpan) {
                            if (position > endPosition) {
                                endPosition = position
                            }
                        }
                        val spanCount = layoutManager.spanCount
                        viewModel.scrollToLoadMore(endPosition, spanCount)
                    }

//                    is VirtualLayoutManager -> {
//                        val endPosition = layoutManager.findLastVisibleItemPosition()
//                        viewModel.scrollToLoadMore(endPosition, 0)
//                    }

                    is LinearLayoutManager -> {
                        val endPosition = layoutManager.findLastVisibleItemPosition()
                        viewModel.scrollToLoadMore(endPosition, 0)
                    }
                }
            }
        })
    }

    abstract fun initAdapter(): RecyclerView.Adapter<*>?

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        this.adapter = adapter
    }

    open fun getAdapter(): RecyclerView.Adapter<*>? {
        return adapter
    }

    open fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(mActivity)

    abstract fun setData(list: MutableList<S>)

    fun getPageStateView(): PageStateView {
        return stateView
    }
}