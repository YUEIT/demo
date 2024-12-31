package cn.yue.base.utils.device

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import cn.yue.base.utils.Utils

/**
 * Description:
 * Created by yue on 25/11/24
 */
object ThemeUtils {

    fun applyNightTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (Utils.getContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager)
                .setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun applyDayTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (Utils.getContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager)
                .setApplicationNightMode(UiModeManager.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun applyFollowSys() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (Utils.getContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager)
                .setApplicationNightMode(UiModeManager.MODE_NIGHT_AUTO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }


    fun isNightTheme(): Boolean {
        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()
        return if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            true
        } else if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            false
        } else {
            isSystemNight(Utils.getContext())
        }
    }

    fun isLightTheme(): Boolean {
        return !isNightTheme()
    }

    fun isSystemNight(context: Context): Boolean {
        val state = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return state == Configuration.UI_MODE_NIGHT_YES
    }
}