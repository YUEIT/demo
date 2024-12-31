package cn.yue.base.fragment.data

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
/**
 * Description :
 * Created by yue on 2020/8/8
 */
class LoaderLiveData {
    var isFirstLoad = true
    private val pageStatusLiveData = MutableLiveData<PageStatus>()
    private val loadStatusLiveData = MutableLiveData<LoadStatus>()


    fun setPageStatus(value: PageStatus) {
        if (value == PageStatus.NORMAL) {
            isFirstLoad = false
        } else if (value == PageStatus.NO_DATA) {
            isFirstLoad = true
        }
        pageStatusLiveData.value = value
    }

    fun getPageStatus(): PageStatus? {
        return pageStatusLiveData.value
    }

    fun setLoadStatus(value: LoadStatus) {
        loadStatusLiveData.value = value
    }

    fun getLoadStatus(): LoadStatus? {
        return loadStatusLiveData.value
    }

    fun observe(lifecycleOwner: LifecycleOwner,
                observer: (pageStatus: PageStatus?, loadStatus: LoadStatus?) -> Unit) {
        pageStatusLiveData.removeObservers(lifecycleOwner)
        loadStatusLiveData.removeObservers(lifecycleOwner)
        pageStatusLiveData.observe(lifecycleOwner) {
            observer.invoke(it, loadStatusLiveData.value)
        }
        loadStatusLiveData.observe(lifecycleOwner) {
            observer.invoke(pageStatusLiveData.value, it)
        }
    }

    fun observePage(lifecycleOwner: LifecycleOwner, observer: Observer<in PageStatus>) {
        pageStatusLiveData.observe(lifecycleOwner, observer)
    }

    fun observeLoad(lifecycleOwner: LifecycleOwner, observer: Observer<in LoadStatus>) {
        loadStatusLiveData.observe(lifecycleOwner, observer)
    }

    fun isLoading(): Boolean {
        return getLoadStatus() == LoadStatus.LOAD_MORE
                || getLoadStatus() == LoadStatus.REFRESH
                || getPageStatus() == PageStatus.REFRESH
    }
}