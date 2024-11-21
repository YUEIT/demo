package cn.yue.base.init

import android.content.Context
import androidx.startup.Initializer

/**
 * Description:
 * Created by yue on 18/11/2024
 */
class BaseInitializer: Initializer<BaseInitHelper> {
    override fun create(context: Context): BaseInitHelper {
        return BaseInitHelper.instance.apply {
            init(context)
        }
    }
    override fun dependencies(): List<Class<out Initializer<*>>> {
        // No dependencies on other libraries.
        return emptyList()
    }
}