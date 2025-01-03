package cn.yue.base.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.view.MotionEvent
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import cn.yue.base.activity.rx.ILifecycleProvider
import cn.yue.base.activity.rx.RxLifecycleProvider
import cn.yue.base.fragment.BaseFragment
import cn.yue.base.router.RouterCard
import cn.yue.base.router.WrapperResultLauncher
import cn.yue.base.utils.code.LanguageUtils

/**
 * Description :
 * Created by yue on 2019/3/11
 */
abstract class BaseFragmentActivity : AppCompatActivity() {

    private lateinit var lifecycleProvider: ILifecycleProvider<Lifecycle.Event>
    private var mCache = HashMap<String, Any>()
    val defaultLauncher = registerResultLauncher {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLifecycle(RxLifecycleProvider())
        initView()
    }

    override fun attachBaseContext(newBase: Context?) {
        val baseContext = LanguageUtils.forceLanguage(newBase)
        super.attachBaseContext(baseContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

    open fun initLifecycle(provider: ILifecycleProvider<Lifecycle.Event>) {
        lifecycleProvider = provider
        lifecycle.addObserver(lifecycleProvider)
    }

    fun getLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return lifecycleProvider
    }

    open fun initView() {
        val transition = intent.getIntExtra(RouterCard.TRANSITION, TransitionAnimation.TRANSITION_RIGHT)
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            if (transition == TransitionAnimation.TRANSITION_CENTER) {
                enterTransition = Fade()
                exitTransition = Fade()
            } else {
                enterTransition = Slide().apply {
                    slideEdge = TransitionAnimation.getEnterGravity(transition)
                }
                exitTransition = Slide().apply {
                    slideEdge = TransitionAnimation.getExitGravity(transition)
                }
            }
        }
        enableEdgeToEdge()
    }

    fun getCacheValue(key: String): Any? {
        return mCache[key]
    }

    fun putCache(key: String, value: Any) {
        mCache[key] = value
    }

    override fun onDestroy() {
        super.onDestroy()
        mCache.clear()
    }

    open fun setFragmentResult(resultCode: Int, resultBundle: Bundle?) {

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
        return WrapperResultLauncher(this, registerForActivityResult(ActivityResultContracts.StartActivityForResult(), callback),)
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