package cn.duoduo.flarum.api.models

data class Include(
    val type: String,
    val id: String,
    val attributes: HashMap<String, Any>
)
