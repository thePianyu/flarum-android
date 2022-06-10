package cn.duoduo.flarum.api.models

data class BaseResponse<T>(
    val links: Links,
    val data: T,
    val included: List<Include>
)
