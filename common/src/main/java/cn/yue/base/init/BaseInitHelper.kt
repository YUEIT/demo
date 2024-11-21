package cn.yue.base.init

import android.app.Application
import android.content.Context
import cn.yue.base.utils.Utils
import cn.yue.base.utils.debug.LogUtils

/**
 * Description:
 * Created by yue on 18/11/2024
 */
class BaseInitHelper {

    companion object {
        val instance by lazy { BaseInitHelper() }
    }

    fun init(context: Context) {
        Utils.init(context)
        AutoSizeInitUtils.init(context as Application)
        LogUtils.setDebug(InitConstant.isDebug())
        NotificationUtils.initChannel()
        Utils.initAfterAuth()
    }
}