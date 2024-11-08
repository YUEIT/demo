package cn.yue.base.utils.code

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import java.util.Locale


object LanguageUtils {


    fun forceLanguage(context: Context?): Context? {
        if (context == null) return null
        return try {
            changeLanguage(context, Locale("id"))
        } catch (e: Exception) {
            e.printStackTrace()
            context
        }
    }

    /**
     * 应用多语言切换，重写BaseActivity中的attachBaseContext即可
     *
     * @param context  上下文
     * @param language 语言
     * @return context
     */
    fun changeLanguage(context: Context?, language: Locale): Context? {
        if (context == null) return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createConfigurationContext(context, language)
        } else {
            updateConfiguration(context, language)
        }
    }

    /**
     * 获取系统的Local
     *
     * @return Locale
     */
    fun getSystemLocal(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Locale.getDefault()
        }
    }

    /**
     * Android 7.1 以下通过 updateConfiguration
     *
     * @param context  context
     * @param language 语言
     * @return Context
     */
    private fun updateConfiguration(context: Context, locale: Locale): Context {
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocales(LocaleList(locale))
        } else {
            // updateConfiguration
            configuration.locale = locale
            val dm: DisplayMetrics = resources.displayMetrics
            resources.updateConfiguration(configuration, dm)
        }
        return context
    }

    /**
     * Android 7.1以上通过createConfigurationContext
     * N增加了通过config.setLocales去修改多语言
     *
     * @param context  上下文
     * @param language 语言
     * @return context
     */
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private fun createConfigurationContext(context: Context, language: Locale): Context? {
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        val localeList = LocaleList(language)
        configuration.setLocales(localeList)
        return context.createConfigurationContext(configuration)
    }

    /**
     * 更新Application的Resource local，应用不重启的情况才调用，因为部分会用到application中的context
     * 切记不能走新api createConfigurationContext，亲测
     * @param context context
     * @param locale newLanguage
     */
    fun updateApplicationLocale(context: Context, locale: Locale) {
        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocales(LocaleList(locale))
        } else {
            configuration.setLocale(locale)
        }
        val dm: DisplayMetrics = resources.displayMetrics
        resources.updateConfiguration(configuration, dm)
    }
}