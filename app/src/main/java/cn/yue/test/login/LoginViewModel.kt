package cn.yue.test.login

import androidx.lifecycle.viewModelScope
import cn.yue.base.mvvm.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Description:
 * Created by Luo Biao on 8/5/24
 */
class LoginViewModel: BaseViewModel() {

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