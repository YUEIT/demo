package cn.yue.base.router


object RouterWarehouse {

    var routes: HashMap<String, RouteMeta> = HashMap()

    fun findRouteMeta(path: String?): RouteMeta? {
        return routes[path]
    }
}