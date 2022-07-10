package cn.duoduo.flarum.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.duoduo.flarum.api.models.LoginRequest
import cn.duoduo.flarum.api.models.LoginResponse
import cn.duoduo.flarum.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val loginResp: MutableLiveData<LoginResponse> = MutableLiveData<LoginResponse>()
    private val loginError: MutableLiveData<Throwable> = MutableLiveData<Throwable>()

    fun getLoginResp(): LiveData<LoginResponse> = loginResp
    fun getLoginError(): LiveData<Throwable> = loginError

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.flarumService.login(LoginRequest(username, password))
                loginResp.postValue(resp)
            } catch (e: Exception) {
                e.printStackTrace()
                loginError.postValue(e)
            }
        }
    }

}