package cn.duoduo.flarum.api.models

data class UserAttributes(
    val username: String,
    val displayName: String,
    val avatarUrl: String?,
    val joinTime: String,
    val lastSeenAt: String
)
