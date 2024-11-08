package cn.yue.base.net.wrapper

import com.google.gson.annotations.SerializedName

open class BaseUnityListBean<T>(
        @SerializedName(value="list")
        var mList: MutableList<T>? = null,
        @SerializedName(value = "total", alternate = ["count"])
        var mTotal: Int = 0,//总数。
        @SerializedName(value = "pageCount")
        var mPageCount: Int = 0,    //页数
        @SerializedName(value = "pageSize")
        var mPageSize: Int = 0,    //每页数量
        @SerializedName(value = "pageNo")
        var mPageNo: Int = 0,    //当前页面号
     ) {

    fun isDataEmpty(): Boolean = getRealList().isNullOrEmpty()

    fun getRealList(): MutableList<T>? {
        return mList
    }

    fun getRealPageSize(): Int {
        return if (mPageSize == 0) {
            getRealList()?.size ?: 0
        } else {
            mPageSize
        }
    }

    fun getRealTotal(): Int {
        return mTotal
    }

    fun getRealPageNo(): Int {
        return mPageNo
    }
}
