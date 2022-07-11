package cn.duoduo.flarum.api.models

data class User(
    val type: String,
    val id: String,
    val attributes: UserAttributes
)
