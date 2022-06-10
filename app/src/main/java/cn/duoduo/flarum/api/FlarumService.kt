package cn.duoduo.flarum.api

import cn.duoduo.flarum.api.models.BaseResponse
import cn.duoduo.flarum.api.models.Discussion
import cn.duoduo.flarum.api.models.Post
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FlarumService {

    @GET("/api/discussions")
    suspend fun getDiscussions(
        @Query("page[offset]") offset: Int
    ): BaseResponse<List<Discussion>>

    @GET("/api/discussions/{id}")
    suspend fun getDiscussion(
        @Path("id") id: String
    ): BaseResponse<Discussion>

    @GET("/api/posts")
    suspend fun getPosts(
        @Query("filter[id]") idFilter: String = ""
    ): BaseResponse<List<Post>>

}