package cn.yue.test.helper

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Description:
 * Created by yue on 31/10/2024
 */
object GlobalEventBus {

    val timeState = MutableStateFlow(0)
}