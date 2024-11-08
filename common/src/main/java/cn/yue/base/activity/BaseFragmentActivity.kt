package cn.yue.base.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import cn.yue.base.R
import cn.yue.base.activity.rx.ILifecycleProvider
import cn.yue.base.activity.rx.RxLifecycleProvider
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.utils.code.LanguageUtils
import cn.yue.base.widget.BottomBar
import cn.yue.base.widget.TopBar
import java.util.UUID

/**
 * Description :
 * Created by yue on 2019/3/11
 */
abstract class BaseFragmentActivity : FragmentActivity() {

    private lateinit var lifecycleProvider: ILifecycleProvider<Lifecycle.Event>
    private var topBar: TopBar? = null
    private var bottomBar: BottomBar? = null
    private var vTop: FrameLayout? = null
    private var content: FrameLayout? = null
    private var vBottom: FrameLayout? = null

    private var resultCode = 0
    private var resultBundle: Bundle? = null
    private var mCurrentFragment: BaseFragment? = null
    private var mCache = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLifecycle(RxLifecycleProvider())
        initView()
    }

    override fun attachBaseContext(newBase: Context?) {
        val baseContext = LanguageUtils.forceLanguage(newBase)
        super.attachBaseContext(baseContext)
    }

    open fun initLifecycle(provider: ILifecycleProvider<Lifecycle.Event>) {
        lifecycleProvider = provider
        lifecycle.addObserver(lifecycleProvider)
    }

    fun getLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return lifecycleProvider
    }

    open fun getContentViewLayoutId(): Int = R.layout.activity_base_layout

    open fun getFragment(): Fragment? = null

    open fun initView() {
        registerOnBackPressed()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        BarUtils.fullScreen(this.window)
        setContentView(getContentViewLayoutId())
        vTop = findViewById(R.id.v_top)
        initTopBar()
        vBottom = findViewById(R.id.v_bottom)
        initBottomBar()
        content = findViewById(R.id.content)
        content?.setBackgroundColor(Color.WHITE)
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

    open fun initTopBar() {
        vTop?.addView(getTopBar().apply { topBar = this })
    }

    open fun initBottomBar() {
        vBottom?.addView(getBottomBar().apply { bottomBar = this })
    }

    fun getTopBar(): TopBar {
        return topBar ?: TopBar(this)
    }

    fun getBottomBar(): BottomBar {
        return bottomBar ?: BottomBar(this)
    }

    fun customTopBar(view: View?) {
        vTop?.removeAllViews()
        vTop?.addView(view)
    }

    fun customBottomBar(view: View?) {
        vBottom?.removeAllViews()
        vBottom?.addView(view)
    }

    fun removeTopBar() {
        vTop?.removeAllViews()
    }

    fun removeBottomBar() {
        vBottom?.removeAllViews()
    }

    fun setContentBackground(@ColorInt color: Int) {
        content?.setBackgroundColor(color)
    }

    fun recreateFragment(fragmentName: String?) {
        replace(getFragment(), null, false)
    }

    fun instantiate(mClass: Class<*>, args: Bundle?): Fragment {
        return Fragment.instantiate(this, mClass.simpleName, args)
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

    fun getCacheValue(key: String): Any? {
        return mCache[key]
    }

    fun putCache(key: String, value: Any) {
        mCache[key] = value
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        setExitAnim()
    }

    open fun setExitAnim() {
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCache.clear()
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

    fun setFragmentResult(resultCode: Int, resultBundle: Bundle?) {
        this.resultCode = resultCode
        this.resultBundle = resultBundle
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val fragments: List<Fragment> = supportFragmentManager.fragments
        for (fragment: Fragment in fragments) {
            if (fragment.isAdded && fragment is BaseFragment && fragment.isVisible) {
                fragment.onNewIntent(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments: List<Fragment> = supportFragmentManager.fragments
        for (fragment: Fragment in fragments) {
            if (fragment.isAdded && fragment is BaseFragment && fragment.isVisible) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        val noPermission = arrayListOf<String>()
        it.forEach { entry ->
            if (!entry.value) {
                noPermission.add(entry.key)
            }
        }
        if (noPermission.isEmpty()) {
            permissionSuccess?.invoke()
        } else {
            permissionFailed?.invoke(noPermission)
        }
    }

    fun launchPermissions(success: () -> Unit,
                          failed: (permission: List<String>) -> Unit,
                          vararg permissions: String) {
        permissionSuccess = success
        permissionFailed = failed
        permissionLauncher.launch(permissions as Array<String>)
    }
  
    private var permissionSuccess: (() -> Unit)? = null
    private var permissionFailed: ((permission: List<String>) -> Unit)? = null
    
    fun registerResultLauncher(callback: ActivityResultCallback<ActivityResult>): WrapperResultLauncher {
        return WrapperResultLauncher(this,
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(), callback)
        )
    }

    private val dispatchTouchListeners = arrayListOf<OnDispatchTouchListener>()

    fun addOnDispatchTouchListener(listener: OnDispatchTouchListener) {
        dispatchTouchListeners.add(listener)
    }

    fun removeOnDispatchTouchListener(listener: OnDispatchTouchListener) {
        dispatchTouchListeners.remove(listener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            dispatchTouchListeners.forEach {
                it.dispatchTouchEvent(ev)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}