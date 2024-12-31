package cn.yue.test.test

import androidx.lifecycle.viewModelScope
import cn.yue.base.fragment.vm.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Description:
 * Created by yue on 28/11/24
 */
class TestViewModel: BaseViewModel() {

    private val _state = MutableStateFlow("")

    val stateFlow = _state.asStateFlow()

    val flow = flow<String> {
        emit("sss")
    }

    fun login() {
        viewModelScope.launch {
            _state.update { "" }
        }
    }
}