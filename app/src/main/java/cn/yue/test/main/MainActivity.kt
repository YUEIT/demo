package cn.yue.test.main

import androidx.core.view.WindowInsetsCompat
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.utils.code.setOnSingleClickListener
import cn.yue.test.LaunchActivity
import cn.yue.test.databinding.ActivityMainBinding

/**
 * Description:
 * Created by yue on 18/11/24
 */
class MainActivity: BaseFragmentActivity() {

    override fun initView() {
        super.initView()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BarUtils.fixWindowInsets(window.decorView) { v, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.vRoot.setPadding(0, systemInsets.top, 0, systemInsets.bottom)
            insets
        }
        binding.tv0.setOnSingleClickListener {
            finish()
            defaultLauncher.launch(LaunchActivity::class)
//            ToastUtils.showLongToast("what!!!")
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            ThemeUtils.applyNightTheme()
        }
    }
}