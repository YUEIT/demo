package cn.yue.base.utils.code

import android.view.View

/**
 * Description:
 * Created by yue on 31/10/2024
 */
object ViewUtils {

    fun View.setPaddingLeft(padding: Int) {
        this.setPadding(padding, this.paddingTop, this.paddingRight, this.paddingBottom)
    }

    fun View.setPaddingTop(padding: Int) {
        this.setPadding(this.paddingLeft, padding, this.paddingRight, this.paddingBottom)
    }

    fun View.setPaddingRight(padding: Int) {
        this.setPadding(this.paddingLeft, this.paddingTop, padding, this.paddingBottom)
    }

    fun View.setPaddingBottom(padding: Int) {
        this.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, padding)
    }
}