package cn.duoduo.flarum.api.models

data class LoginRequest(
    val identification: String,
    val password: String,
    val remember: Int = 1
)
