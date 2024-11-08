package cn.yue.base.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import cn.yue.base.R
import cn.yue.base.activity.rx.ILifecycleProvider
import cn.yue.base.activity.rx.RxLifecycleProvider
import cn.yue.base.mvp.IWaitView
import cn.yue.base.widget.TopBar
import cn.yue.base.widget.dialog.WaitDialog


abstract class BaseFragment : Fragment(), IWaitView {

    private lateinit var lifecycleProvider: ILifecycleProvider<Lifecycle.Event>
    private var cacheView: View? = null
    lateinit var mActivity: BaseFragmentActivity
    lateinit var topBar: TopBar
    var mHandler = Handler(Looper.getMainLooper())

    /**
     * 获取布局
     */
    abstract fun getLayoutId(): Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseFragmentActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleProvider = initLifecycleProvider()
        lifecycle.addObserver(lifecycleProvider)
    }

    open fun initLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return RxLifecycleProvider()
    }

    fun getLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return lifecycleProvider
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isClickable = true
        view.isFocusable = true
        if (!hasCache) {
            initView(savedInstanceState)
        }
        try {
            //destroy后会移除liveData观察者，恢复后重新添加
            initObserver()
        } catch (e: IllegalArgumentException) {
            //不能重复添加
        }
        if (!hasCache) {
            initOther()
        }
    }

    open fun initOther() {}

    open fun initObserver() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (cacheView == null || !needCache()) {//如果view没有被初始化或者不需要缓存的情况下，重新初始化控件
            topBar = mActivity.getTopBar()
            initTopBar(topBar)
            cacheView = if (getLayoutId() == 0) {
                null
            } else {
                inflater.inflate(getLayoutId(), container, false)
            }
            hasCache = false
        } else {
            hasCache = true
            val v = cacheView?.parent
            if (v != null && v is ViewGroup) {
                v.removeView(cacheView)
            }
        }
        return cacheView
    }

    /**
     * true 避免当前Fragment被replace后回退回来重走onCreateView，导致重复初始化View和数据
     */
    open fun needCache(): Boolean {
        return true
    }

    private var hasCache: Boolean = false

    abstract fun initView(savedInstanceState: Bundle?)

    open fun initTopBar(topBar: TopBar) {
        if (parentFragment == null) {
            topBar.visibility = View.VISIBLE
            topBar.setLeftImage(R.drawable.app_icon_back)
            topBar.setLeftImageTint("#663db8".toColorInt())
            topBar.setLeftClickListener { finishAll() }
        }
    }

    fun customTopBar(view: View) {
        mActivity.customTopBar(view)
    }

    fun hideTopBar() {
        mActivity.getTopBar().visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissWaitDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(lifecycleProvider)
    }

    fun isActive(): Boolean {
        return lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
    }

    fun clearCacheView() {
        cacheView = null
    }

    open fun onFragmentBackPressed(): Boolean {
        return false
    }

    @JvmOverloads
    fun setFragmentBackResult(resultCode: Int, data: Bundle? = null) {
        mActivity.setFragmentResult(resultCode, data)
    }

    open fun onNewIntent(intent: Intent) {
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment != null && fragment.isAdded && fragment is BaseFragment && fragment.isVisible) {
                fragment.onNewIntent(intent)
            }
        }
    }

    open fun onFragmentResult(resultCode: Int, resultBundle: Bundle?) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment != null && fragment.isAdded && fragment.isVisible && fragment.userVisibleHint) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    fun registerResultLauncher(callback: ActivityResultCallback<ActivityResult>): WrapperResultLauncher {
        return WrapperResultLauncher(
            this,
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(), callback)
        )
    }

    //--------------------------------------------------------------------------------------------------------------
    fun finishFragment() {
        mActivity.onBackPressed()
    }

    fun finishFragmentWithResult() {
        setFragmentBackResult(Activity.RESULT_OK)
        mActivity.onBackPressed()
    }

    fun finishFragmentWithResult(data: Bundle) {
        setFragmentBackResult(Activity.RESULT_OK, data)
        mActivity.onBackPressed()
    }

    fun finishAll() {
        mActivity.supportFinishAfterTransition()
        mActivity.overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    @JvmOverloads
    fun finishAllWithResult(resultCode: Int, data: Intent? = null) {
        mActivity.setResult(resultCode, data)
        finishAll()
    }

    fun finishAllWithResult(data: Bundle) {
        val intent = Intent()
        intent.putExtras(data)
        finishAllWithResult(Activity.RESULT_OK, intent)
    }

    //--------------------------------------------------------------------------------------------------------------

    fun <T : View> requireViewById(resId: Int): T {
        var view: T? = null
        if (cacheView != null) {
            view = cacheView!!.findViewById<T>(resId)
        }
        if (view == null) {
            throw NullPointerException("no found view with ${resId.toString()} in " + this)
        }
        return view
    }

    fun <T : View> findViewById(resId: Int): T? {
        var view: T? = null
        if (cacheView != null) {
            view = cacheView!!.findViewById<T>(resId)
        }
        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun replace(containViewId: Int, fragment: Fragment?) {
        if (null != fragment) {
            childFragmentManager.beginTransaction()
                .replace(containViewId, fragment)
                .commitAllowingStateLoss()
        }
    }

    fun replace(containViewId: Int, fragment: Fragment?, tag: String) {
        if (null != fragment) {
            childFragmentManager.beginTransaction()
                .replace(containViewId, fragment, tag)
                .commitAllowingStateLoss()
        }
    }

    fun removeFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
    }

    fun removeFragment(tag: String) {
        val fragment = childFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            removeFragment(fragment)
        }
    }


    fun attachFragment(fragment: Fragment?): Boolean {
        if (fragment != null && fragment.isDetached) {
            childFragmentManager.beginTransaction().attach(fragment).commitAllowingStateLoss()
            return true
        }
        return false
    }

    fun attachFragment(tag: String): Boolean {
        val fragment = findFragmentByTag(tag)
        return attachFragment(fragment)
    }

    fun isAddFragment(tag: String): Boolean {
        val fragment = findFragmentByTag(tag)
        return fragment != null && fragment.isAdded
    }

    fun detachFragment(fragment: Fragment?): Boolean {
        if (fragment != null && fragment.isAdded) {
            childFragmentManager.beginTransaction().detach(fragment).commitAllowingStateLoss()
            return true
        }
        return false
    }

    fun detachFragment(tag: String): Boolean {
        val fragment = findFragmentByTag(tag)
        return detachFragment(fragment)
    }

    fun findFragmentByTag(tag: String): Fragment? {
        return childFragmentManager.findFragmentByTag(tag)
    }

    fun addFragment(containerId: Int, fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .add(containerId, fragment, tag)
            .commitAllowingStateLoss()
    }

    fun hideFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .hide(fragment)
            .commitAllowingStateLoss()
    }

    fun showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .show(fragment)
            .commitAllowingStateLoss()
    }

    private var waitDialog: WaitDialog? = null

    override fun showWaitDialog(title: String?) {
        if (waitDialog == null) {
            waitDialog = WaitDialog(mActivity)
        }
        if (waitDialog?.isShowing() == false) {
            waitDialog?.show(title)
        }
    }

    override fun dismissWaitDialog() {
        if (waitDialog != null && waitDialog!!.isShowing()) {
            waitDialog?.cancel()
        }
    }
}