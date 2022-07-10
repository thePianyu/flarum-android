package cn.duoduo.flarum.api.models

data class PostRequestData(
    val attributes: PostRequestAttributes,
    val relationships: Relationships
)