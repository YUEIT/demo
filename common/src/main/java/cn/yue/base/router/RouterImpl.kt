package cn.yue.base.router

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import cn.yue.base.R
import cn.yue.base.activity.CommonActivity
import cn.yue.base.fragment.BaseDialogFragment
import cn.yue.base.fragment.BaseFragment
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils

/**
 * Description :
 * Created by yue on 2023/7/21
 */
class RouterImpl: INavigation() {
	
	private lateinit var mRouterCard: RouterCard
	
	override fun bindRouterCard(routerCard: RouterCard): INavigation {
		mRouterCard = routerCard
		return this
	}
	
	private fun getRouteType(): RouteType {
		if (TextUtils.isEmpty(mRouterCard.getPath())) {
			val componentName = mRouterCard.getComponentName() ?: return RouteType.UNKNOWN
			val routeMeta = RouterWarehouse.findRouteMeta(componentName)
			if (routeMeta?.type != null) {
				return routeMeta.type!!
			}
			try {
				val clazz = Class.forName(componentName)
				val type = if (Activity::class.java.isAssignableFrom(clazz)) {
					RouteType.ACTIVITY
				} else if (Fragment::class.java.isAssignableFrom(clazz)) {
					RouteType.FRAGMENT
				} else {
					RouteType.UNKNOWN
				}
				if (routeMeta != null) {
					routeMeta.type = type
				} else {
					RouterWarehouse.routes[componentName] = RouteMeta().apply {
						this.type = type
						this.destination = clazz
					}
				}
				return type
			} catch (e : ClassNotFoundException) {
				return RouteType.UNKNOWN
			}
		}
		val routeMeta = RouterWarehouse.findRouteMeta(mRouterCard.getPath())
		return routeMeta?.type ?: RouteType.UNKNOWN
	}

	private fun getDestination(): String {
		if (TextUtils.isEmpty(mRouterCard.getPath())) {
			val componentName = mRouterCard.getComponentName() ?: ""
			val routeMeta = RouterWarehouse.findRouteMeta(componentName)
			if (routeMeta?.destination != null) {
				return routeMeta.destination!!.name
			}
			try {
				val clazz = Class.forName(componentName)
				if (routeMeta != null) {
					routeMeta.destination = clazz
				} else {
					RouterWarehouse.routes[componentName] = RouteMeta().apply {
						this.destination = clazz
					}
				}
				return componentName
			} catch (e : ClassNotFoundException) {
				return ""
			}
		}
		val routeMeta = RouterWarehouse.findRouteMeta(mRouterCard.getPath())
		return routeMeta?.destination?.name ?: ""
	}
	
	override fun navigation(context: Any, requestCode: Int, toActivity: String?) {
		val realContext: Context
		when (context) {
			is Context -> {
				realContext = context
			}
			is BaseFragment -> {
				realContext = context.mActivity
			}
			is BaseDialogFragment -> {
				realContext = context.mActivity
			}
			is WrapperResultLauncher -> {
				launch(context, toActivity)
				return
			}
			else -> {
				ToastUtils.showShortToast(R.string.app_find_not_page.getString())
				return
			}
		}
		when (getRouteType()) {
			RouteType.ACTIVITY -> {
				jumpToActivity(realContext, requestCode)
			}
			RouteType.FRAGMENT -> {
				jumpToFragment(realContext, toActivity, requestCode)
			}
			else -> {
				ToastUtils.showShortToast(R.string.app_find_not_page.getString())
			}
		}
	}

	@SuppressLint("WrongConstant")
	private fun jumpToActivity(context: Context, requestCode: Int) {
		try {
			val className = getDestination()
			if (className.isEmpty()) {
				ToastUtils.showShortToast(R.string.app_find_not_page.getString())
				return
			}
			val intent = Intent()
			intent.putExtras(mRouterCard.getExtras())
			intent.putExtra(RouterCard.TRANSITION, mRouterCard.getTransition())
			intent.flags = mRouterCard.getFlags()
			intent.setClassName(context, className)
			if (context !is Activity) {
				context.startActivity(intent)
				return
			}
			val transitionAnimation = ActivityOptions.makeSceneTransitionAnimation(context).toBundle()
			if (requestCode <= 0) {
				context.startActivity(intent, transitionAnimation)
			} else {
				context.startActivityForResult(intent, requestCode, transitionAnimation)
			}
		} catch (e : Exception) {
			e.printStackTrace()
		}

	}
	
	@SuppressLint("WrongConstant")
	private fun jumpToFragment(context: Context, toActivity: String? = null, requestCode: Int) {
		try {
			val className = getDestination()
			if (className.isEmpty()) {
				ToastUtils.showShortToast(R.string.app_find_not_page.getString())
				return
			}
			val intent = Intent()
			intent.putExtras(mRouterCard.getExtras())
			intent.putExtra(RouterCard.CLASS_NAME, className)
			intent.putExtra(RouterCard.TRANSITION, mRouterCard.getTransition())
			intent.flags = mRouterCard.getFlags()
			if (toActivity == null) {
				intent.setClass(context, CommonActivity::class.java)
			} else {
				intent.setClassName(context, toActivity)
			}
			if (context !is Activity) {
				context.startActivity(intent)
				return
			}
			val transitionAnimation = ActivityOptions.makeSceneTransitionAnimation(context).toBundle()
			if (requestCode <= 0) {
				context.startActivity(intent, transitionAnimation)
			} else {
				context.startActivityForResult(intent, requestCode, transitionAnimation)
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	private fun launch(
        launcher: WrapperResultLauncher,
        toActivity: String?
	) {
		val realContext = when (launcher.context) {
			is Context -> {
				launcher.context
			}
			is BaseFragment -> {
				launcher.context.mActivity
			}
			is BaseDialogFragment -> {
				launcher.context.mActivity
			}
			else -> {
				return
			}
		}
		when (getRouteType()) {
			RouteType.ACTIVITY -> {
				launchToActivity(realContext, launcher)
			}
			RouteType.FRAGMENT -> {
				launchToFragment(realContext, toActivity, launcher)
			}
			else -> {
				ToastUtils.showShortToast(R.string.app_find_not_page.getString())
			}
		}
	}
	
	@SuppressLint("WrongConstant")
	private fun launchToActivity(context: Context, launcher: WrapperResultLauncher) {
		try {
			val className = getDestination()
			if (className.isEmpty()) {
				ToastUtils.showShortToast(R.string.app_find_not_page.getString())
				return
			}
			val intent = Intent()
			intent.setClassName(context, className)
			intent.putExtras(mRouterCard.getExtras())
			intent.putExtra(RouterCard.CLASS_NAME, className)
			intent.putExtra(RouterCard.TRANSITION, mRouterCard.getTransition())
			intent.flags = mRouterCard.getFlags()
			if (context is Activity) {
				val transitionAnimation = ActivityOptionsCompat.makeSceneTransitionAnimation(context)
				launcher.launcher.launch(intent, transitionAnimation)
			} else {
				launcher.launcher.launch(intent)
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	@SuppressLint("WrongConstant")
	private fun launchToFragment(context: Context, toActivity: String? = null, launcher: WrapperResultLauncher) {
		try {
			val className = getDestination()
			if (className.isEmpty()) {
				ToastUtils.showShortToast(R.string.app_find_not_page.getString())
				return
			}
			val intent = Intent()
			intent.putExtras(mRouterCard.getExtras())
			intent.putExtra(RouterCard.CLASS_NAME, className)
			intent.putExtra(RouterCard.TRANSITION, mRouterCard.getTransition())
			intent.flags = mRouterCard.getFlags()
			if (toActivity == null) {
				intent.setClass(context, CommonActivity::class.java)
			} else {
				intent.setClassName(context, toActivity)
			}
			if (context is Activity) {
				val transitionAnimation =
					ActivityOptionsCompat.makeSceneTransitionAnimation(context)
				launcher.launcher.launch(intent, transitionAnimation)
			} else {
				launcher.launcher.launch(intent)
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}