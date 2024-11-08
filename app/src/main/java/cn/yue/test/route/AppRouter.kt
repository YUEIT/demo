package cn.yue.test.route

import cn.yue.base.router.FRouter
import cn.yue.base.router.RouteMeta
import cn.yue.base.router.RouteType
import cn.yue.test.login.LoginFragment
import cn.yue.test.test.TestFragment
import cn.yue.test.test.TestListFragment

object AppRouter {

    fun init() {
        val routes = HashMap<String, RouteMeta>()
        loadInto(routes)
        FRouter.init(routes)
    }

    private fun loadInto(atlas: HashMap<String, RouteMeta>) {
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
    }

}