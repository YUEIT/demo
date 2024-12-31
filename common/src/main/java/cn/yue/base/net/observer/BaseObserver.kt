package cn.yue.base.net.observer

import cn.yue.base.R
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
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
				exceptionBlock?.invoke(ResultException(ResponseCode.ERROR_CANCEL, e.message?:""))
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
