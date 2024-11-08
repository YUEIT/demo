package cn.yue.base.router

class RouteMeta {

    var type: RouteType? = null

    var destination: Class<*>? = null

    var path: String? = null

    companion object {

        fun build(type: RouteType, destination: Class<*>, path: String): RouteMeta {
            return RouteMeta().apply {
                this.type = type
                this.destination = destination
                this.path = path
            }
        }
    }
}