package cn.duoduo.flarum.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.duoduo.flarum.api.models.Discussion
import cn.duoduo.flarum.api.models.Post
import cn.duoduo.flarum.network.RetrofitClient
import kotlinx.coroutines.launch

class DiscussionViewModel : ViewModel() {

    private val discussion: MutableLiveData<Discussion> = MutableLiveData<Discussion>()

    fun getDiscussion(): LiveData<Discussion> = discussion

    fun fetchDiscussion(id: String) {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.flarumService.getDiscussion(id)
                discussion.postValue(resp.data!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val posts: MutableLiveData<List<Post>> = MutableLiveData<List<Post>>()

    fun getPosts(): LiveData<List<Post>> = posts

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    fun fetchPosts(offset: Int) {
        viewModelScope.launch {
            try {
                val includedPosts = StringBuilder()

                var size = 20
                if (size + offset > discussion.value!!.relationships.posts!!.data.size)
                    size = discussion.value!!.relationships.posts!!.data.size - offset

                for (post in discussion.value!!.relationships.posts!!.data.subList(
                    offset, size + offset
                )) {
                    includedPosts.append(post.id).append(",")
                }

                val resp = RetrofitClient.flarumService.getPosts(includedPosts.toString())

                for(post in resp.data){
                    val user = resp.included.find {
                        it.type == "users" && post.relationships.user!!.data.id == it.id
                    }
                    post.username = user!!.attributes.get("username").toString()
                    post.avatar = user!!.attributes.get("avatarUrl").toString()
                }

                posts.postValue(resp.data!!.filter {
                    it.attributes.contentType == "comment"
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

}