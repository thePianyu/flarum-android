package cn.duoduo.flarum.api.models

data class Error(
    val status: String,
    val code: String,
    val detail: String
)
