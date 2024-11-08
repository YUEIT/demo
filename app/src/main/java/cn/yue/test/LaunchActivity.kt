package cn.yue.test

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.router.FRouter
import cn.yue.test.route.RoutePath

class LaunchActivity : BaseFragmentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        val mSplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        mSplashScreen.setKeepOnScreenCondition { true }
        toStart()
    }

    override fun getContentViewLayoutId(): Int {
        return R.layout.activity_launch
    }

    private fun toStart() {
        FRouter.instance.build(RoutePath.TEST).navigation(this)
        finish()
    }
}