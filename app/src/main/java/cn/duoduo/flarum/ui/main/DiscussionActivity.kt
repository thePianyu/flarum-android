package cn.duoduo.flarum.ui.main


import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.duoduo.flarum.R
import cn.duoduo.flarum.api.models.Post
import cn.duoduo.flarum.databinding.ActivityDiscussionBinding
import cn.duoduo.flarum.utils.ImageGetter
import cn.duoduo.flarum.utils.ImageUtils
import cn.duoduo.flarum.viewmodel.DiscussionViewModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.jsoup.internal.StringUtil


class DiscussionActivity : AppCompatActivity() {

    private val model: DiscussionViewModel by viewModels()
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDiscussionBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_discussion)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = Adapter()
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn)
        // 设置标题
        val view = layoutInflater.inflate(R.layout.item_post_header, binding.recyclerView, false)
        val title = intent.getStringExtra("discussionTitle")!!
        view.findViewById<TextView>(R.id.title_discussion).text = title
        adapter.addHeaderView(view)

        binding.recyclerView.adapter = adapter

        // 拉取内容
        model.fetchDiscussion(intent.getStringExtra("ID")!!)

        model.getDiscussion().observe(this) {
            model.fetchPosts(offset)
        }

        model.getPosts().observe(this) {
            offset += it.size
            adapter.addData(it)
        }

        // 分割线
        val itemDivider =
            DividerItemDecoration(binding.recyclerView.context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDivider)

        // 返回按钮
        val bakBtn = findViewById<ImageView>(R.id.btn_left_back)
        bakBtn.setOnClickListener {
            this.finish()
        }

        // 编辑、点赞、收藏、分享
        val iconLike = findViewById<ImageView>(R.id.icon_like)
        val iconBookmark = findViewById<ImageView>(R.id.icon_bookmark)
        val iconShare = findViewById<ImageView>(R.id.icon_share)
        val iconEdit = findViewById<ImageView>(R.id.icon_edit_replay)

        iconEdit.setImageResource(R.drawable.icon_edit)
        bakBtn.setImageResource(R.drawable.icon_left_back)
        iconLike.setImageResource(R.drawable.icon_like)
        iconBookmark.setImageResource(R.drawable.icon_bookmark)
        iconShare.setImageResource(R.drawable.icon_share)

    }

    private class Adapter : BaseQuickAdapter<Post, Adapter.ViewHolder>(R.layout.item_post) {

        private class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
            val content: TextView = itemView.findViewById(R.id.content)
            val avatar: ImageView = itemView.findViewById(R.id.avatar)
            val title: TextView = itemView.findViewById(R.id.title)
        }

        override fun convert(holder: ViewHolder, item: Post) {
            val html = HtmlCompat.fromHtml(
                item.attributes.contentHtml,
                HtmlCompat.FROM_HTML_MODE_COMPACT,
                ImageGetter(context, recyclerView.width, holder.content),
                null
            )
            holder.content.text = html
            holder.title.text = item.username

            // 加载头像
            if (!StringUtil.isBlank(item.avatar)) {
                ImageUtils.loadAvatar(item.avatar, holder.avatar, holder.itemView, context)
            } else {
                ImageUtils.setDefaultAvatar(holder.avatar, context)
            }
            Linkify.addLinks(holder.content, Linkify.WEB_URLS)
        }

    }

}