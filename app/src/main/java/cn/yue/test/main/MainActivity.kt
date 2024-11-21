package cn.yue.test.main

import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.router.Router
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.utils.code.setOnSingleClickListener
import cn.yue.test.databinding.ActivityMainBinding

/**
 * Description:
 * Created by yue on 18/11/24
 */
class MainActivity: BaseFragmentActivity() {

    override fun initView() {
        BarUtils.fullStatusBar(window)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BarUtils.fixSystemBarInsets(window.decorView) { _, s, n ->
            binding.vRoot.setPadding(0, s, 0, n)
        }
        binding.tv0.setOnSingleClickListener {
            Router.instance.setComponent(DetailActivity::class).navigation(this)
        }
//        lifecycleScope.launch(Dispatchers.Main) {
//            // The block passed to repeatOnLifecycle is executed when the lifecycle
//            // is at least STARTED and is cancelled when the lifecycle is STOPPED.
//            // It automatically restarts the block when the lifecycle is STARTED again.
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                // Safely collect from WindowInfoTracker when the lifecycle is STARTED
//                // and stops collection when the lifecycle is STOPPED
//                WindowInfoTracker.getOrCreate(this@MainActivity)
//                    .windowLayoutInfo(this@MainActivity)
//                    .collect { layoutInfo ->
//                        // do something
//                        Log.d("luo", "initView: $layoutInfo")
//                        layoutInfo.displayFeatures.firstNotNullOfOrNull {
//                            it is FoldingFeature
//                        }?.let {
//                            ( it as FoldingFeature).state
//                        }
//                    }
//            }
//        }
    }
}