package cn.yue.base.router

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import kotlin.reflect.KClass

/**
 * Description :
 * Created by yue on 2023/7/21
 */
class WrapperResultLauncher(
	val context: Any,
	val launcher: ActivityResultLauncher<Intent>,
) {
	fun launch(
		path: String,
		toActivity: String? = null,
		block: ((routerCard: RouterCard) -> Unit)? = null
	) {
		val route = Router.instance.build(path)
		block?.invoke(route)
		route.navigation(this, 0, toActivity)
	}

	fun launch(
		componentName: KClass<*>,
		toActivity: String? = null,
		block: ((routerCard: RouterCard) -> Unit)? = null
	) {
		val route = Router.instance.setComponent(componentName)
		block?.invoke(route)
		route.navigation(this, 0, toActivity)
	}

}

