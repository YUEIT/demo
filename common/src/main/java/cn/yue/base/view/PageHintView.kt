package cn.yue.base.view

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.widget.NestedScrollView
import cn.yue.base.R
import cn.yue.base.view.load.PageStatus
import cn.yue.base.view.refresh.IRefreshLayout

/**
 * Description :
 * Created by yue on 2018/11/13
 */
class PageHintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : NestedScrollView(context, attrs, defStyleAttr) {

    private var noNetView: View? = null
    private var noDataView: View? = null
    private var loadingView: View? = null


    init {
        initView(context)
    }

    private fun initView(context: Context) {
        isFillViewport = true
        isClickable = true
        setDefault(context)
    }

    private fun setDefault(context: Context) {
        loadingView = View.inflate(context, R.layout.layout_page_hint_loading, null)
        noNetView = View.inflate(context, R.layout.layout_page_hint_no_net, null)
        noDataView = View.inflate(context, R.layout.layout_page_hint_no_data, null)
        noNetView?.findViewById<View>(R.id.reloadTV)?.setOnClickListener {
            onReloadListener?.invoke()
        }
        noNetView?.findViewById<View>(R.id.checkNetTV)?.setOnClickListener {
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private var onReloadListener: (() -> Unit)? = null
    fun setOnReloadListener(onReloadListener: (() -> Unit)?) {
        this.onReloadListener = onReloadListener
    }

    fun setNoNetView(noNetView: View?) {
        if (noNetView != null) {
            this.noNetView = noNetView
        }
    }

    fun setNoNetViewById(@LayoutRes layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        setNoNetView(view)
        return view
    }

    fun setNoDataView(noDataView: View?) {
        if (noDataView != null) {
            this.noDataView = noDataView
        }
    }

    fun setNoDataViewById(@LayoutRes layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        setNoDataView(view)
        return view
    }

    fun setLoadingView(loadingView: View?) {
        if (loadingView != null) {
            this.loadingView = loadingView
        }
    }

    fun setLoadingViewById(@LayoutRes layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        setLoadingView(view)
        return view
    }

    fun show(status: PageStatus?) {
        when (status) {
            PageStatus.NORMAL -> showSuccess()
            PageStatus.REFRESH -> showLoading()
            PageStatus.NO_DATA -> showErrorNoData()
            PageStatus.ERROR -> showErrorNet()
            else -> {}
        }
    }

    fun showLoading() {
        loadingView?.let {
            visibility = View.VISIBLE
            removeAllViews()
            addView(it)
            setRefreshEnable(false)
        }
    }

    fun showSuccess() {
        visibility = View.GONE
        setRefreshEnable(true)
    }

    fun showErrorNet() {
        noNetView?.let {
            visibility = View.VISIBLE
            removeAllViews()
            addView(it)
            setRefreshEnable(true)
        }
    }

    fun showErrorNoData() {
        noDataView?.let {
            visibility = View.VISIBLE
            removeAllViews()
            addView(it)
            setRefreshEnable(true)
        }
    }

    private var refreshLayout: IRefreshLayout? = null

    fun setRefreshTarget(refreshLayout: IRefreshLayout?) {
        this.refreshLayout = refreshLayout
    }

    private fun setRefreshEnable(enable: Boolean) {
        refreshLayout?.setRefreshEnable(enable)
    }

    override fun addView(child: View) {
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(child, params)
    }


}