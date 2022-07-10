package cn.duoduo.flarum.api.models

data class Relationships(
    val firstPost: Relationship<RelationshipData>? = null,
    val tags: Relationship<List<RelationshipData>>? = null,
    val user: Relationship<RelationshipData>? = null,
    val posts: Relationship<List<RelationshipData>>? = null,
    val discussion: Relationship<RelationshipData>? = null
)
