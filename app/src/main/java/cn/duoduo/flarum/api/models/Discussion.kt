package cn.duoduo.flarum.api.models

data class Discussion(
    val type: String,
    val id: String,
    val attributes: DiscussionAttributes,
    val relationships: Relationships
) {
    var firstPostContent: String = "" // 第一个post的正文
    var tags: MutableList<TagAttributes> = ArrayList() // 相关标签
    var avatar: String = ""
}
