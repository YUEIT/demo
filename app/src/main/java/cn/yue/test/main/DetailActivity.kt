package cn.yue.test.main

import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.router.Router
import cn.yue.test.databinding.ActivityDetailBinding
import cn.yue.test.test.TestFragment

/**
 * Description:
 * Created by yue on 20/11/24
 */
class DetailActivity : BaseFragmentActivity() {

    override fun initView() {
        super.initView()
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvBtn.setOnClickListener {
//            DetailDialog().show(supportFragmentManager)
            Router.instance.setComponent(TestFragment::class).navigation(this)
        }
    }
}