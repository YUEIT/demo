package cn.yue.base.fragment.vm

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import cn.yue.base.activity.rx.RxLifecycleTransformer

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class ItemViewModel(private val parentViewModel: BaseViewModel) : BaseViewModel() {

    override fun <T: Any> toBindLifecycle(): RxLifecycleTransformer<T> {
        return parentViewModel.toBindLifecycle()
    }

    override fun <T: Any> toBindLifecycle(e: Lifecycle.Event): RxLifecycleTransformer<T> {
        return parentViewModel.toBindLifecycle(e)
    }

    override fun showWaitDialog(title: String?) {
        parentViewModel.showWaitDialog(title)
    }

    override fun dismissWaitDialog() {
        parentViewModel.dismissWaitDialog()
    }

    override fun finish() {
        parentViewModel.finish()
    }

    override fun finishForResult(resultCode: Int, bundle: Bundle?) {
        parentViewModel.finishForResult(resultCode, bundle)
    }

}