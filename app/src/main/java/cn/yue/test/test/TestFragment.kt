package cn.yue.test.test

import android.os.Bundle
import cn.yue.base.activity.BaseFragment
import cn.yue.base.mvp.photo.PhotoHelper
import cn.yue.base.router.Route
import cn.yue.base.utils.app.BarUtils
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestBinding

/**
 * Description:
 * Created by yue on 31/10/2024
 */
@Route(path = "/app/test")
class TestFragment : BaseFragment() {
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
        binding.flTest.setOnClickListener {
            photoHelper.openSystemAlbum()
        }
    }

//    override fun initObserver() {
//        super.initObserver()
//        GlobalEventBus.timeState
//            .drop(1)
//            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
//            .syncCollect {
//                Log.d("luo", "test timeState: $it")
//            }
//    }
//
//    fun <T> Flow<T>.syncCollect(block: (t: T) -> Unit) {
//        lifecycleScope.launch {
//            this@syncCollect.collect {
//                block.invoke(it)
//            }
//        }
//    }
}