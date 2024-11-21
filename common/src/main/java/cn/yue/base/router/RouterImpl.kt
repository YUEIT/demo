package cn.yue.base.router

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.fragment.app.Fragment
import cn.yue.base.R
import cn.yue.base.activity.BaseDialogFragment
import cn.yue.base.activity.BaseFragment
import cn.yue.base.activity.CommonActivity
import cn.yue.base.activity.WrapperResultLauncher
import cn.yue.base.mvvm.BaseViewModel
import cn.yue.base.mvvm.data.RouterModel
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
			is BaseViewModel -> {
				context.navigation(RouterModel(mRouterCard, requestCode, toActivity))
				return
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
		val className = getDestination()
		if (className.isEmpty()) {
			ToastUtils.showShortToast(R.string.app_find_not_page.getString())
			return
		}
		val intent = Intent()
		intent.putExtras(mRouterCard.getExtras())
		intent.flags = mRouterCard.getFlags()
		intent.setClassName(context, className)
		if (requestCode <= 0 || context !is Activity) {
			context.startActivity(intent)
		} else {
			context.startActivityForResult(intent, requestCode)
		}
		if (context is Activity) {
			context.overridePendingTransition(mRouterCard.getRealEnterAnim(), mRouterCard.getRealExitAnim())
		}
	}
	
	@SuppressLint("WrongConstant")
	private fun jumpToFragment(context: Context, toActivity: String? = null, requestCode: Int) {
		val className = getDestination()
		if (className.isEmpty()) {
			ToastUtils.showShortToast(R.string.app_find_not_page.getString())
			return
		}
		val intent = Intent()
		intent.putExtra(RouterCard.TAG, mRouterCard)
		intent.putExtras(mRouterCard.getExtras())
		intent.putExtra(RouterCard.CLASS_NAME, className)
		intent.flags = mRouterCard.getFlags()
		if (toActivity == null) {
			intent.setClass(context, CommonActivity::class.java)
		} else {
			intent.setClassName(context, toActivity)
		}
		if (requestCode <= 0 || context !is Activity) {
			context.startActivity(intent)
		} else {
			context.startActivityForResult(intent, requestCode)
		}
		if (context is Activity) {
			context.overridePendingTransition(mRouterCard.getRealEnterAnim(), mRouterCard.getRealExitAnim())
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
		val className = getDestination()
		if (className.isEmpty()) {
			ToastUtils.showShortToast(R.string.app_find_not_page.getString())
			return
		}
		val intent = Intent()
		intent.setClassName(context, className)
		intent.putExtras(mRouterCard.getExtras())
		intent.flags = mRouterCard.getFlags()
		launcher.launcher.launch(intent)
		if (context is Activity) {
			context.overridePendingTransition(mRouterCard.getRealEnterAnim(), mRouterCard.getRealExitAnim())
		}
	}
	
	@SuppressLint("WrongConstant")
	private fun launchToFragment(context: Context, toActivity: String? = null, launcher: WrapperResultLauncher) {
		val intent = Intent()
		intent.putExtra(RouterCard.TAG, mRouterCard)
		intent.putExtras(mRouterCard.getExtras())
		intent.flags = mRouterCard.getFlags()
		if (toActivity == null) {
			intent.setClass(context, CommonActivity::class.java)
		} else {
			intent.setClassName(context, toActivity)
		}
		launcher.launcher.launch(intent)
		if (context is Activity) {
			context.overridePendingTransition(mRouterCard.getRealEnterAnim(), mRouterCard.getRealExitAnim())
		}
	}
}