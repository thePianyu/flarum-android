package cn.duoduo.flarum.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.duoduo.flarum.api.models.Discussion
import cn.duoduo.flarum.api.models.User
import cn.duoduo.flarum.network.RetrofitClient
import kotlinx.coroutines.launch

class UserSpaceViewModel: ViewModel() {

    private val user: MutableLiveData<User> = MutableLiveData()

    fun getUser(): LiveData<User> = user

    fun fetchUser(id: String) {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.flarumService.getUser(id)
                user.postValue(resp.data!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}