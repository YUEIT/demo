package cn.yue.base.net.observer

import cn.yue.base.mvp.IStatusView
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus

/**
 * Description :
 * Created by yue on 2019/4/1
 */
abstract class BasePullObserver<T: Any>(private val iStatusView: IStatusView?) : BaseNetObserver<T>() {

    override fun onSuccess(t: T) {
        iStatusView?.changePageStatus(PageStatus.NORMAL)
        iStatusView?.changeLoadStatus(LoadStatus.NORMAL)
    }

    override fun onException(e: ResultException) {
        when(e.code) {
            ResponseCode.ERROR_CANCEL -> {
                iStatusView?.changeLoadStatus(LoadStatus.NORMAL)
            }
            else -> {
                iStatusView?.changePageStatus(PageStatus.ERROR)
                showShortToast(e.message)
            }
        }
        iStatusView?.changeLoadStatus(LoadStatus.NORMAL)
    }

}