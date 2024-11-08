package cn.yue.test.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import cn.yue.base.mvvm.components.BaseVMFragment
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.utils.code.setOnSingleClickListener
import cn.yue.base.widget.TopBar
import cn.yue.test.R
import cn.yue.test.databinding.FragmentLoginBinding
import cn.yue.test.helper.GlobalEventBus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginFragment : BaseVMFragment<LoginViewModel>() {

    private lateinit var thirdLoginHelper: ThirdLoginHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thirdLoginHelper = ThirdLoginHelper(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setBarVisibility(View.GONE)
        BarUtils.fullScreen(mActivity.window, true)
    }

    private lateinit var binding: FragmentLoginBinding
    override fun initView(savedInstanceState: Bundle?) {
        thirdLoginHelper.init()
        binding = FragmentLoginBinding.bind(requireView())
        binding.loginTV.setOnSingleClickListener {
//            FRouter.instance.build(RoutePath.TEST).navigation(mActivity)
        }
        binding.flContainer.setOnClickListener {
            GlobalEventBus.timeState.update { it + 1 }
        }
    }

    override fun initObserver() {
        super.initObserver()
        GlobalEventBus.timeState
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .syncCollect {
                Log.d("luo", "before: state $it")
            }
    }

    fun <T> Flow<T>.syncCollect(block: (t: T) -> Unit) {
        lifecycleScope.launch {
            this@syncCollect.collect {
                block.invoke(it)
            }
        }
    }

    override fun onFragmentBackPressed(): Boolean {
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        thirdLoginHelper.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
