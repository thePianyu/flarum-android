package cn.duoduo.flarum.api.models

data class Post(
    val type: String,
    val id: String,
    val attributes: PostAttributes,
    val relationships: Relationships
){
    var username: String = ""
    var avatar: String = ""
}
