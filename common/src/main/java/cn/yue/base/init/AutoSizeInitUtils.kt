package cn.yue.base.init

import android.app.Application


/**
 * Description :
 * Created by yue on 2022/8/5
 */

object AutoSizeInitUtils {

    fun init(application: Application) {
//        AutoSize.checkAndInit(application)
//        AutoSizeConfig.getInstance()
//            .setAutoAdaptStrategy(CustomAutoAdapterStrategy())
    }

//    class CustomAutoAdapterStrategy : DefaultAutoAdaptStrategy() {
//
//        override fun applyAdapt(target: Any?, activity: Activity?) {
//            super.applyAdapt(target, activity)
//            if ((ScreenUtils.screenWidth.toFloat() / ScreenUtils.screenHeight.toFloat()) > 0.75f) {
//                AutoSize.cancelAdapt(activity)
//            }
//        }
//    }
}