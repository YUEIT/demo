package cn.yue.base.fragment

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.yue.base.activity.rx.ILifecycleProvider
import cn.yue.base.fragment.vm.BaseViewModel
import cn.yue.base.fragment.vm.ItemViewModel
import java.lang.reflect.ParameterizedType

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BaseVMFragment<VM : BaseViewModel> : BaseFragment() {

    lateinit var viewModel: VM
    val coroutineScope by lazy { lifecycleScope }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!this::viewModel.isInitialized) {
            var viewModel = initViewModel()
            if (viewModel == null) {
                val modelClass: Class<VM>
                val type = javaClass.genericSuperclass
                modelClass = if (type is ParameterizedType) {
                    type.actualTypeArguments[0] as Class<VM>
                } else {
                    //如果没有指定泛型参数，则默认使用BaseViewModel
                    BaseViewModel::class.java as Class<VM>
                }
                viewModel = createViewModel(modelClass)
            }
            this.viewModel = viewModel
        }
        super.onCreate(savedInstanceState)
    }

    override fun initLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return viewModel
    }

    open fun initViewModel(): VM? {
        return null
    }

    open fun createViewModel(cls: Class<VM>): VM {
        return ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[cls]
    }

    fun <T: ItemViewModel> createChildViewMode(cls: Class<T>, parentViewModel: BaseViewModel): T {
        return ViewModelProvider(this, ChildNewInstanceFactory(parentViewModel))[cls]
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.waitEvent singleObserve { s ->
            if (null == s) {
                dismissWaitDialog()
            } else {
                showWaitDialog(s)
            }
        }
        viewModel.finishEvent singleObserve { (resultCode, bundle) ->
            if (resultCode == 0) {
                finishAll()
            } else {
                val intent = Intent()
                bundle?.let {
                    intent.putExtras(it)
                }
                finishAllWithResult(resultCode, intent)
            }
        }
    }

    infix fun <T> LiveData<T>.observe(observer: Observer<T>) {
        try {
            this.observe(this@BaseVMFragment, observer)
        } catch (e : IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    infix fun <T> LiveData<T>.singleObserve(observer: Observer<T>) {
        try {
            this.removeObservers(this@BaseVMFragment)
            this.observe(this@BaseVMFragment, observer)
        } catch (e : IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

    class ChildNewInstanceFactory(private val parentViewModel: BaseViewModel): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return try {
                modelClass.getConstructor(parentViewModel.javaClass).newInstance(parentViewModel)
            } catch (e: java.lang.InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }
    }

    fun isViewModeInitialized(): Boolean {
        return this::viewModel.isInitialized
    }
}