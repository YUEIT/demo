package cn.yue.test.utils

import cn.yue.base.init.UrlEnvironment

/**
 * Description :
 * Created by yue on 2020/10/20
 */
object LocalStorage {

    private const val USER_PERMISSION = "user_permission"
    private const val SERVICE_ENVIRONMENT = "service_environment"

    fun getUserPermission(): Boolean {
        return true
    }

    fun setUserPermission(boolean: Boolean) {

    }

    fun getServiceEnvironment(): UrlEnvironment {

        return UrlEnvironment.RELEASE
    }

    fun setServiceEnvironment(serviceEnvironment: UrlEnvironment) {
    }

}