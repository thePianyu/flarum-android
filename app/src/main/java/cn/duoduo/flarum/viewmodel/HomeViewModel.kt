package cn.duoduo.flarum.viewmodel

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.duoduo.flarum.api.models.Discussion
import cn.duoduo.flarum.api.models.PostAttributes
import cn.duoduo.flarum.api.models.TagAttributes
import cn.duoduo.flarum.network.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val discussions: MutableLiveData<List<Discussion>> = MutableLiveData<List<Discussion>>().also {
        it.value = ArrayList()
    }

    fun getDiscussions(): LiveData<List<Discussion>> = discussions

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    fun fetchDiscussions(offset: Int) {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.flarumService.getDiscussions(offset)

                resp.data.forEach {
                    // 获取第一个post的内容
                    val includedPost = resp.included.find { include ->
                        include.type == it.relationships.firstPost!!.data.type && include.id == it.relationships.firstPost!!.data.id
                    }
                    it.includedFirstPost = PostAttributes(
                        includedPost!!.attributes.get("contentHtml") as String,
                        includedPost!!.attributes.get("contentType") as String
                    )

                    // 获取tags
                    it.tags = ArrayList()
                    for (tag in it.relationships.tags!!.data) {
                        val includedTag = resp.included.find { include ->
                            include.type == tag.type && include.id == tag.id
                        }
                        it.tags.add(
                            TagAttributes(
                                includedTag!!.attributes.get("name").toString(),
                                includedTag!!.attributes.get("description").toString()
                            )
                        )
                    }

                    // 获取用户头像
                    val includedUser = resp.included.find { include ->
                        include.type == it.relationships.user!!.data.type && include.id == it.relationships.user.data.id
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val avatar = includedUser!!.attributes.get("avatarUrl")
                        if(avatar != null){
                            it.avatar = avatar as String
                        }
                    }
                }

                discussions.postValue(resp.data!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}