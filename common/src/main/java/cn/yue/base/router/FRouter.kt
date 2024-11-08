package cn.yue.base.router

import android.os.Parcel
import android.os.Parcelable

/**
 * Description : 路由
 * Created by yue on 2019/3/11
 */
class FRouter() : INavigation(), Parcelable {

    private var mRouterCard = RouterCard(this)
    
    private val aRouterImpl = ARouterImpl()
    
    private object FRouterHolder {
        val instance = FRouter()
    }

    fun getRouterCard(): RouterCard {
        return mRouterCard
    }

    fun build(path: String?): RouterCard {
        mRouterCard.setPath(path)
        return mRouterCard
    }

    override fun bindRouterCard(routerCard: RouterCard): INavigation {
        this.mRouterCard = routerCard
        this.mRouterCard.setNavigationImpl(this)
        return this
    }

    override fun navigation(context: Any, requestCode: Int, toActivity: String?) {
        aRouterImpl.bindRouterCard(mRouterCard)
        aRouterImpl.navigation(context, requestCode, toActivity)
    }
    
    constructor(source: Parcel) : this() {
        mRouterCard = source.readParcelable(RouterCard::class.java.classLoader)?: RouterCard()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int){
        dest.writeParcelable(mRouterCard, flags)
    }

    companion object {
        const val TAG = "FRouter"

        fun init(group: Map<String, RouteMeta>) {
            RouterWarehouse.routes.putAll(group)
        }

        @JvmStatic
        val instance: FRouter
            get() {
                val fRouter = FRouterHolder.instance
                fRouter.mRouterCard = RouterCard(fRouter)
                return fRouter
            }

        @JvmField
        val CREATOR: Parcelable.Creator<FRouter> = object : Parcelable.Creator<FRouter> {
            override fun createFromParcel(source: Parcel): FRouter = FRouter(source)
            override fun newArray(size: Int): Array<FRouter?> = arrayOfNulls(size)
        }
    }
}