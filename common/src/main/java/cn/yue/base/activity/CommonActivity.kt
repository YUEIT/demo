package cn.yue.base.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cn.yue.base.R
import cn.yue.base.activity.TransitionAnimation.getStopEnterAnim
import cn.yue.base.activity.TransitionAnimation.getStopExitAnim
import cn.yue.base.router.RouterCard
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.utils.app.FragmentUtils
import java.util.UUID

/**
 * Description :
 * Created by yue on 2019/3/11
 */
open class CommonActivity : BaseFragmentActivity() {
    
    private var transition = 0 //入场动画
    private var content: FrameLayout? = null
    private var mCurrentFragment: BaseFragment? = null
    private var resultCode = 0
    private var resultBundle: Bundle? = null

    open fun setContentView() {
        setContentView(R.layout.activity_base_layout)
    }

    override fun initView() {
        super.initView()
        registerOnBackPressed()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        BarUtils.fullScreen(this.window)
        setContentView()
        content = findViewById(R.id.content)
        supportFragmentManager.addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener {
            mCurrentFragment = getNowFragment()
            if (mCurrentFragment != null && resultCode == Activity.RESULT_OK) {
                mCurrentFragment?.onFragmentResult(resultCode, resultBundle)
            }
            resultCode = Activity.RESULT_CANCELED
            resultBundle = null
        })
        replace(getFragment(), null, false)
    }

    open fun getFragment(): Fragment? {
        val routerCard = intent?.extras?.getParcelable<RouterCard>(RouterCard.TAG)?: return null
        val className = intent.getStringExtra(RouterCard.CLASS_NAME)?: return null
        transition = routerCard.getTransition()
        val fragment = FragmentUtils.instantiate(this, className)
        fragment.arguments = intent.extras
        return fragment
    }

    fun replace(fragment: Fragment?, tag: String?, canBack: Boolean) {
        var mTag = tag
        if (null == fragment) {
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        //        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
        if (TextUtils.isEmpty(mTag)) {
            mTag = UUID.randomUUID().toString()
        }
        transaction.replace(R.id.content, fragment, mTag)
        if (canBack) {
            transaction.addToBackStack(mTag)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun getNowFragment(): BaseFragment? {
        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        return if (fragment != null && fragment is BaseFragment) {
            fragment
        } else null
    }

    fun getCurrentFragment(): BaseFragment? {
        return mCurrentFragment
    }

    fun setCurrentFragment(fragment: BaseFragment) {
        this.mCurrentFragment = fragment
    }

    open fun registerOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mCurrentFragment = getNowFragment()
                if (mCurrentFragment != null && mCurrentFragment!!.onFragmentBackPressed()) {
                    return
                }
                if (supportFragmentManager.backStackEntryCount == 0 && resultCode != Activity.RESULT_CANCELED) {
                    var data: Intent? = null
                    if (resultBundle != null) {
                        data = Intent()
                        data.putExtras(resultBundle!!)
                    }
                    setResult(resultCode, data)
                }
                setExitAnim()
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    fun setContentBackground(@ColorInt color: Int) {
        content?.setBackgroundColor(color)
    }

    fun recreateFragment() {
        replace(getFragment(), null, false)
    }

    override fun setFragmentResult(resultCode: Int, resultBundle: Bundle?) {
        this.resultCode = resultCode
        this.resultBundle = resultBundle
    }

    override fun setExitAnim() {
        overridePendingTransition(getStopEnterAnim(transition), getStopExitAnim(transition))
    }
}