package cn.duoduo.flarum.api.models

data class Relationships(
    val firstPost: Relationship<RelationshipData>?,
    val tags: Relationship<List<RelationshipData>>?,
    val user: Relationship<RelationshipData>?,
    val posts: Relationship<List<RelationshipData>>?
)
