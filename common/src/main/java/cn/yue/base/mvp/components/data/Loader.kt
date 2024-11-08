package cn.yue.base.mvp.components.data

import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus

class Loader {

    var isFirstLoad: Boolean
    var pageStatus: PageStatus
    var loadStatus: LoadStatus

    init {
        pageStatus = PageStatus.NORMAL
        loadStatus = LoadStatus.NORMAL
        isFirstLoad = true
    }

    fun setLoadStatus(loadStatus: LoadStatus): LoadStatus {
        this.loadStatus = loadStatus
        return loadStatus
    }

    fun setPageStatus(pageStatus: PageStatus): PageStatus {
        if (pageStatus == PageStatus.NORMAL) {
            isFirstLoad = false
        } else if (pageStatus == PageStatus.NO_DATA) {
            isFirstLoad = true
        }
        this.pageStatus = pageStatus
        return pageStatus
    }

    fun isLoading(): Boolean {
        return loadStatus == LoadStatus.LOAD_MORE
                || loadStatus == LoadStatus.REFRESH
                || pageStatus == PageStatus.REFRESH
    }
}