package cn.yue.test

import android.app.Application
import android.content.Context
import cn.yue.test.route.AppRouter


/**
 * Description :
 * Created by yue on 2018/11/14
 */
class AppApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //        InitConstant.setDebug(BuildConfig.DEBUG_MODE)
//        InitConstant.setVersionName(BuildConfig.VERSION_NAME)
    }

    override fun onCreate() {
        super.onCreate()
        AppRouter.init()
    }

}