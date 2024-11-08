package cn.yue.base.activity

import androidx.fragment.app.Fragment
import cn.yue.base.activity.TransitionAnimation.getStopEnterAnim
import cn.yue.base.activity.TransitionAnimation.getStopExitAnim
import cn.yue.base.router.RouterCard
import cn.yue.base.utils.app.FragmentUtils

/**
 * Description :
 * Created by yue on 2019/3/11
 */
open class CommonActivity : BaseFragmentActivity() {
    
    private var transition = 0 //入场动画
    
    override fun getFragment(): Fragment? {
        val routerCard = intent?.extras?.getParcelable<RouterCard>(RouterCard.TAG)?: return null
        val className = intent.getStringExtra(RouterCard.CLASS_NAME)?: return null
        transition = routerCard.getTransition()
        val fragment = FragmentUtils.instantiate(this, className)
        fragment.arguments = intent.extras
        return fragment
    }

    override fun setExitAnim() {
        overridePendingTransition(getStopEnterAnim(transition), getStopExitAnim(transition))
    }
}