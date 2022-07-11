package cn.duoduo.flarum.api.models

data class Discussion(
    val type: String,
    val id: String,
    val attributes: DiscussionAttributes,
    val relationships: Relationships
) {
    var includedFirstPost: PostAttributes? = null
    var tags: MutableList<TagAttributes> = ArrayList() // 相关标签
    var user: UserAttributes? = null
}
