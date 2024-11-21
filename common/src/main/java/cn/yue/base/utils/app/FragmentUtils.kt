package cn.yue.base.utils.app

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Description : Fragment 创建工具类
 * Created by yue on 2020/4/9
 */
object FragmentUtils {
    fun instantiate(context: Context, fname: String,
                    args: Bundle? = null): Fragment {
        if (context is FragmentActivity) {
            val f = context.supportFragmentManager.fragmentFactory.instantiate(context.getClassLoader(), fname)
            if (args != null) {
                args.classLoader = f.javaClass.classLoader
                f.arguments = args
            }
            return f
        }
        throw RuntimeException("context not instanceof FragmentActivity")
    }

    fun <T : Fragment?> instantiate(context: Context, fname: Class<T>,
                                    args: Bundle? = null): T {
        if (context is FragmentActivity) {
            val f = context.supportFragmentManager.fragmentFactory.instantiate(context.getClassLoader(), fname.name)
            if (args != null) {
                args.classLoader = f.javaClass.classLoader
                f.arguments = args
            }
            return f as T
        }
        throw RuntimeException("context not instanceof FragmentActivity")
    }

    fun Fragment.replace(containViewId: Int, fragment: Fragment?) {
        if (null != fragment) {
            childFragmentManager.beginTransaction()
                .replace(containViewId, fragment)
                .commitAllowingStateLoss()
        }
    }

    fun Fragment.replace(containViewId: Int, fragment: Fragment?, tag: String) {
        if (null != fragment) {
            childFragmentManager.beginTransaction()
                .replace(containViewId, fragment, tag)
                .commitAllowingStateLoss()
        }
    }

    fun Fragment.removeFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
    }

    fun Fragment.removeFragment(tag: String) {
        val fragment = childFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            removeFragment(fragment)
        }
    }


    fun Fragment.attachFragment(fragment: Fragment?): Boolean {
        if (fragment != null && fragment.isDetached) {
            childFragmentManager.beginTransaction().attach(fragment).commitAllowingStateLoss()
            return true
        }
        return false
    }

    fun Fragment.attachFragment(tag: String): Boolean {
        val fragment = findFragmentByTag(tag)
        return attachFragment(fragment)
    }

    fun Fragment.isAddFragment(tag: String): Boolean {
        val fragment = findFragmentByTag(tag)
        return fragment != null && fragment.isAdded
    }

    fun Fragment.detachFragment(fragment: Fragment?): Boolean {
        if (fragment != null && fragment.isAdded) {
            childFragmentManager.beginTransaction().detach(fragment).commitAllowingStateLoss()
            return true
        }
        return false
    }

    fun Fragment.detachFragment(tag: String): Boolean {
        val fragment = findFragmentByTag(tag)
        return detachFragment(fragment)
    }

    fun Fragment.findFragmentByTag(tag: String): Fragment? {
        return childFragmentManager.findFragmentByTag(tag)
    }

    fun Fragment.addFragment(containerId: Int, fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .add(containerId, fragment, tag)
            .commitAllowingStateLoss()
    }

    fun Fragment.hideFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .hide(fragment)
            .commitAllowingStateLoss()
    }

    fun Fragment.showFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .show(fragment)
            .commitAllowingStateLoss()
    }
}


