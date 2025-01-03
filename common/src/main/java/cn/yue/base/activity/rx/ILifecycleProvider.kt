package cn.yue.base.activity.rx

import androidx.lifecycle.LifecycleObserver
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.core.Observable

/**
 * Description :
 * Created by yue on 2019/6/17
 */
interface ILifecycleProvider<E : Any> : LifecycleObserver {

    @CheckReturnValue
    fun lifecycle(): Observable<E>

    @CheckReturnValue
    fun <T : Any> toBindLifecycle(): RxLifecycleTransformer<T>

    @CheckReturnValue
    fun <T : Any> toBindLifecycle(e: E): RxLifecycleTransformer<T>
}