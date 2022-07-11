package cn.duoduo.flarum.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.duoduo.flarum.api.models.*
import cn.duoduo.flarum.network.RetrofitClient
import kotlinx.coroutines.launch

class DiscussionViewModel : ViewModel() {

    private val discussion: MutableLiveData<Discussion> = MutableLiveData<Discussion>()
    private val submitPost: MutableLiveData<Throwable?> = MutableLiveData<Throwable?>()

    fun getDiscussion(): LiveData<Discussion> = discussion

    fun getSubmitPost(): LiveData<Throwable?> = submitPost

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
                if (size < 0) size = 0

                if (offset + size > discussion.value!!.relationships.posts!!.data.size) return@launch

                for (post in discussion.value!!.relationships.posts!!.data.subList(
                    offset, size + offset
                )) {
                    includedPosts.append(post.id).append(",")
                }

                val resp = RetrofitClient.flarumService.getPosts(includedPosts.toString())

                for (post in resp.data) {
                    val includedUser = resp.included.find {
                        it.type == "users" && post.relationships.user!!.data.id == it.id
                    }
                    post.user = UserAttributes(
                        (includedUser!!.attributes["username"] ?: "") as String,
                        (includedUser!!.attributes["displayName"] ?: "") as String,
                        (includedUser!!.attributes["avatarUrl"] ?: "") as String,
                        (includedUser!!.attributes["joinTime"] ?: "") as String,
                        (includedUser!!.attributes["lastSeenAt"] ?: "") as String,
                    )
                    post.userId = includedUser.id
                }

                posts.postValue(resp.data!!.filter {
                    it.attributes.contentType == "comment"
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun submitPost(content: String) {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.flarumService.submitPost(
                    BaseRequest(
                        PostRequestData(
                            PostRequestAttributes(content),
                            Relationships(
                                discussion = Relationship(RelationshipData("discussions", discussion.value?.id ?: ""))
                            )
                        )
                    )
                )
                posts.postValue(ArrayList<Post>().also {
                    val post = resp.data
                    val includedUser = resp.included.find { include ->
                        include.type == "users" && post.relationships.user!!.data.id == include.id
                    }
                    post.user = UserAttributes(
                        (includedUser!!.attributes["username"] ?: "") as String,
                        (includedUser.attributes["displayName"] ?: "") as String,
                        (includedUser.attributes["avatarUrl"] ?: "") as String,
                        (includedUser.attributes["joinTime"] ?: "") as String,
                        (includedUser.attributes["lastSeenAt"] ?: "") as String,
                    )
                    it.add(post)
                })
                submitPost.postValue(null)
            } catch (e: Exception) {
                e.printStackTrace()
                submitPost.postValue(e)
            }
        }
    }

}