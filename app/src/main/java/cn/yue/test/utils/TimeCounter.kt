package cn.yue.test.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TimeCounter {

    companion object {
        val instance by lazy { TimeCounter() }
    }

    private val countTimeLiveData = MutableLiveData<Long>()
    private var disposable: Disposable? = null

    private fun countTime() {
        Observable.interval(0, 1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }

                override fun onNext(t: Long) {
                    if (countTimeLiveData.hasObservers()) {
                        countTimeLiveData.value = t
                    } else {
                        countTimeLiveData.value = 0
                        disposable?.dispose()
                        disposable = null
                    }
                }

            })
    }

    fun addObserver(owner: LifecycleOwner, observer: CountDownObserver) {
        val initValue = countTimeLiveData.value ?: 0
        observer.initValue = initValue
        countTimeLiveData.observe(owner, observer)
        if (disposable == null || disposable?.isDisposed == true) {
            countTime()
        }
    }

    fun observeForever(observer: CountDownObserver) {
        val initValue = countTimeLiveData.value ?: 0
        observer.initValue = initValue
        countTimeLiveData.observeForever(observer)
        if (disposable == null || disposable?.isDisposed == true) {
            countTime()
        }
    }

    fun removeObserver(observer: androidx.lifecycle.Observer<Long>) {
        countTimeLiveData.removeObserver(observer)
    }

    class CountDownObserver(
        private val observer: ((t: Long) -> Unit)
    ) : androidx.lifecycle.Observer<Long> {

        var initValue: Long = 0L

        /**
         * 秒级时间
         */
        override fun onChanged(t: Long) {
            observer.invoke(t - initValue)
        }
    }
}