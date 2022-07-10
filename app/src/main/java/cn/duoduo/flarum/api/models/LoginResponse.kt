package cn.duoduo.flarum.api.models

data class LoginResponse(
    val token: String,
    val userId: Int,
    val errors: List<Error>?
)
