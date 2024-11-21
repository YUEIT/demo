package cn.yue.base.net.observer

import cn.yue.base.R
import cn.yue.base.mvp.IStatusView
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.CancellationException

/**
 * Description :
 * Created by yue on 2023/5/9
 */
open class WrapperObserver<T: Any>(
	private val startBlock: ((d: Disposable) -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val errorBlock: ((e: Throwable) -> Unit)? = null
): SingleObserver<T> {

	override fun onSubscribe(d: Disposable) {
		startBlock?.invoke(d)
	}

	override fun onSuccess(t: T) {
		successBlock?.invoke(t)
	}

	override fun onError(e: Throwable) {
		errorBlock?.invoke(e)
	}
}

open class WrapperNetObserver<T: Any>(
	private val startBlock: ((d: Disposable) -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val exceptionBlock: ((e: ResultException) -> Unit)? = null,
) : WrapperObserver<T>(startBlock, successBlock, null) {

	override fun onError(e: Throwable) {
		super.onError(e)
		when (e) {
			is ResultException -> {
				if (e.code == ResponseCode.ERROR_TOKEN_INVALID
					|| e.code == ResponseCode.ERROR_LOGIN_INVALID) {
					onLoginInvalid()
					return
				}
				exceptionBlock?.invoke(e)
			}
			is CancellationException -> {

			}
			else -> {
				exceptionBlock?.invoke(ResultException(ResponseCode.ERROR_SERVER, e.message?:""))
			}
		}
	}

	private fun onLoginInvalid() {
		ToastUtils.showShortToast(R.string.app_login_fail.getString())

	}

}

class WrapperPullObserver<T: Any>(
	private val iStatusView: IStatusView?,
	private val startBlock: ((d: Disposable) -> Unit)? = null,
	private val successBlock: ((t: T) -> Unit)? = null,
	private val exceptionBlock: ((e: ResultException) -> Unit)? = null,
) : WrapperObserver<T>(startBlock, successBlock, null) {

	override fun onSuccess(t: T) {
		iStatusView?.changePageStatus(PageStatus.NORMAL)
		iStatusView?.changeLoadStatus(LoadStatus.NORMAL)
		successBlock?.invoke(t)
	}

	override fun onError(e: Throwable) {
		super.onError(e)
		when (e) {
			is ResultException -> {
				if (e.code == ResponseCode.ERROR_TOKEN_INVALID
					|| e.code == ResponseCode.ERROR_LOGIN_INVALID) {
					onLoginInvalid()
					return
				}
				onException(e)
			}
			is CancellationException -> {

			}
			else -> {
				onException(ResultException(ResponseCode.ERROR_SERVER, e.message?:""))
			}
		}
	}

	fun onException(e: ResultException) {
		iStatusView?.changePageStatus(PageStatus.ERROR)
		ToastUtils.showShortToast(e.message)
		exceptionBlock?.invoke(e)
	}

	private fun onLoginInvalid() {
		ToastUtils.showShortToast(R.string.app_login_fail.getString())

	}

}
