package cn.yue.base.utils.event

import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlin.math.abs


/**
 * Description :
 * Created by yue on 2021/12/30
 */

open class NoStickyLiveData<T> : MutableLiveData<T>(){

    private var version = -1L

    override fun setValue(value: T?) {
        version++
        super.setValue(value)
    }

    override fun getValue(): T? {
        return super.getValue()
    }

    private var lastSetTime = 0L

    fun setOnceValue(value: T) {
        val currentTime = SystemClock.uptimeMillis()
        if (getValue() == value && abs(currentTime - lastSetTime) < 1000) {
            return
        }
        lastSetTime = currentTime
        setValue(value)
    }

    /**
     * 必须在active（start <--> pause）状态，才会收到回调
     * destroy时移除observer
     */
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        this.removeObservers(owner)
        super.observe(owner, LiveDataWrapperObserver(version, observer))
    }

    /**
     * 观察者一直都在，所以销毁时需要主动调用removeObserver
     */
    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(LiveDataWrapperObserver(version, observer))
    }

    private val observerMap = HashMap<LifecycleOwner, Observer<in T>>()

    /**
     * 注册了lifecycle监听，直到收到destroy事件后，主动移除监听
     */
    fun observerActive(owner: LifecycleOwner, observer: Observer<in T>) {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            // ignore
            return
        }
        val oldObserver = observerMap[owner]
        if (oldObserver != null) {
            super.removeObserver(oldObserver)
            observerMap.remove(owner)
        }
        val observerWrapper = LiveDataWrapperObserver(version, observer)
        observerMap[owner] = observerWrapper
        super.observeForever(observerWrapper)
        val lifecycleWrapper = LifecycleBoundObserver(owner, observerWrapper)
        owner.lifecycle.addObserver(lifecycleWrapper)
    }


    private inner class LiveDataWrapperObserver(
        private val bindVersion: Long,
        private val observer: Observer<in T>
    ) : Observer<T> {

        override fun onChanged(t: T) {
            if (bindVersion < version) {
                observer.onChanged(t)
            }
        }
    }

    private inner class LifecycleBoundObserver(private val mOwner: LifecycleOwner,
                                               val observer: Observer<in T>)
        : LifecycleEventObserver {

        override fun onStateChanged(
            source: LifecycleOwner,
            event: Lifecycle.Event
        ) {
            val currentState = mOwner.lifecycle.currentState
            if (currentState == Lifecycle.State.DESTROYED) {
                removeObserver(observer)
                observerMap.remove(mOwner)
                mOwner.lifecycle.removeObserver(this)
                return
            }
        }
    }
}