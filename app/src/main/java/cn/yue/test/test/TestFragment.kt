package cn.yue.test.test

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import cn.yue.base.photo.helper.PhotoHelper
import cn.yue.base.fragment.BaseVMFragment
import cn.yue.base.router.Route
import cn.yue.base.utils.app.BarUtils
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestBinding
import cn.yue.test.helper.GlobalEventBus
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Description:
 * Created by yue on 31/10/2024
 */
@Route(path = "/app/test")
class TestFragment : BaseVMFragment<TestViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_test
    }

    override fun needScaffold(): Boolean {
        return false
    }

    private val photoHelper = PhotoHelper(this).apply {
        setOnSelectedListener { helper, list ->
//            helper.cropPhoto(false, 1, 1)
            helper.startPreview(list!![0])
        }
        setOnCroppedListener { helper, cropImage ->
            helper.startPreview(cropImage)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        val binding = FragmentTestBinding.bind(requireView())
        BarUtils.fixStatusBarMargin(binding.flTest)
        binding.vTest.setOnClickListener {
            GlobalEventBus.timeState.update { it + 1 }
        }
    }

    override fun initObserver() {
        super.initObserver()
        lifecycleScope.launch {
            launch {
                GlobalEventBus.timeState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                    .collect {
                        Log.d("luo", "test timeState1: $it")
                    }
            }
            launch {
                GlobalEventBus.timeState
                    .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                    .collect {
                        Log.d("luo", "test timeState2: $it")
                    }
            }
        }
    }
}