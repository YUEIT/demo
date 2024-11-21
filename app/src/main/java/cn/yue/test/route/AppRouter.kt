package cn.yue.test.route

import cn.yue.base.photo.preview.ViewMediaActivity
import cn.yue.base.router.RouteMeta
import cn.yue.base.router.RouteType
import cn.yue.base.router.Router
import cn.yue.test.camera.ScanFragment
import cn.yue.test.login.LoginFragment
import cn.yue.test.main.MainActivity
import cn.yue.test.test.TestFragment
import cn.yue.test.test.TestListFragment

object AppRouter {

    fun init() {
        val routes = HashMap<String, RouteMeta>()
        loadInto(routes)
        Router.init(routes)
    }

    private fun loadInto(atlas: HashMap<String, RouteMeta>) {
        atlas[RoutePath.MAIN] = RouteMeta.build(
            RouteType.ACTIVITY,
            MainActivity::class.java,
            RoutePath.MAIN
        )
        atlas[RoutePath.LOGIN] = RouteMeta.build(
            RouteType.FRAGMENT,
            LoginFragment::class.java,
            RoutePath.LOGIN
        )
        atlas[RoutePath.TEST] = RouteMeta.build(
            RouteType.FRAGMENT,
            TestFragment::class.java,
            RoutePath.TEST
        )
        atlas[RoutePath.TEST_LIST] = RouteMeta.build(
            RouteType.FRAGMENT,
            TestListFragment::class.java,
            RoutePath.TEST_LIST
        )
        atlas[RoutePath.VIEW_MEDIA] = RouteMeta.build(
            RouteType.ACTIVITY,
            ViewMediaActivity::class.java,
            RoutePath.VIEW_MEDIA
        )
        atlas[RoutePath.SCAN] = RouteMeta.build(
            RouteType.ACTIVITY,
            ScanFragment::class.java,
            RoutePath.SCAN
        )
    }

}