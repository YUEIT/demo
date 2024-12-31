package cn.yue.test

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.activity.TransitionAnimation
import cn.yue.base.router.Router
import cn.yue.test.main.MainActivity

class LaunchActivity : BaseFragmentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        val mSplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        mSplashScreen.setKeepOnScreenCondition { true }
//        val content: View = findViewById(android.R.id.content)
//        content.viewTreeObserver.addOnPreDrawListener(
//            object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    content.viewTreeObserver.removeOnPreDrawListener(this)
//                    toStart()
//                    return true
//                }
//            }
//        )
        toStart()
    }

    override fun initView() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_launch)
    }

    private fun toStart() {
        Router.instance.setComponent(MainActivity::class)
            .withTransition(TransitionAnimation.TRANSITION_CENTER)
            .navigation(this)
        finishAfterTransition()
    }
}