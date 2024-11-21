package cn.yue.base.router

import android.os.Parcel
import android.os.Parcelable
import kotlin.reflect.KClass

/**
 * Description : 路由
 * Created by yue on 2019/3/11
 */
class Router() : INavigation(), Parcelable {

    private var mRouterCard = RouterCard(this)
    
    private val routerImpl = RouterImpl()

    fun getRouterCard(): RouterCard {
        return mRouterCard
    }

    fun build(path: String?): RouterCard {
        mRouterCard.setPath(path)
        return mRouterCard
    }

    fun setComponent(componentName: KClass<*>): RouterCard {
        mRouterCard.setComponentName(componentName.qualifiedName)
        return mRouterCard
    }

    override fun bindRouterCard(routerCard: RouterCard): INavigation {
        this.mRouterCard = routerCard
        this.mRouterCard.setNavigationImpl(this)
        return this
    }

    override fun navigation(context: Any, requestCode: Int, toActivity: String?) {
        routerImpl.bindRouterCard(mRouterCard)
        routerImpl.navigation(context, requestCode, toActivity)
    }
    
    constructor(source: Parcel) : this() {
        mRouterCard = source.readParcelable(RouterCard::class.java.classLoader)?: RouterCard()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int){
        dest.writeParcelable(mRouterCard, flags)
    }

    companion object {
        const val TAG = "Router"

        fun init(group: Map<String, RouteMeta>) {
            RouterWarehouse.routes.putAll(group)
        }

        private object RouterHolder {
            val instance = Router()
        }

        @JvmStatic
        val instance: Router
            get() {
                val fRouter = RouterHolder.instance
                fRouter.mRouterCard = RouterCard(fRouter)
                return fRouter
            }

        @JvmField
        val CREATOR: Parcelable.Creator<Router> = object : Parcelable.Creator<Router> {
            override fun createFromParcel(source: Parcel): Router = Router(source)
            override fun newArray(size: Int): Array<Router?> = arrayOfNulls(size)
        }
    }
}