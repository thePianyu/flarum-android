package cn.duoduo.flarum.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import cn.duoduo.flarum.R
import cn.duoduo.flarum.api.models.Post
import cn.duoduo.flarum.api.models.PostAttributes
import cn.duoduo.flarum.databinding.ActivityDiscussionBinding
import cn.duoduo.flarum.databinding.ActivityMainBinding
import cn.duoduo.flarum.utils.ImageGetter
import cn.duoduo.flarum.viewmodel.DiscussionViewModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class DiscussionActivity : AppCompatActivity() {

    private val model: DiscussionViewModel by viewModels()
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDiscussionBinding = DataBindingUtil.setContentView(this, R.layout.activity_discussion)

        model.fetchDiscussion(intent.getStringExtra("ID")!!)

        model.getDiscussion().observe(this) {
            model.fetchPosts(offset)
        }

        val adapter = Adapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        model.getPosts().observe(this) {
            offset += it.size
            adapter.addData(it)
        }
    }

    private class Adapter: BaseQuickAdapter<Post, Adapter.ViewHolder>(R.layout.item_post) {

        private class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
            val content: TextView = itemView.findViewById(R.id.content)
        }

        override fun convert(holder: ViewHolder, item: Post) {
            val html = HtmlCompat.fromHtml(
                item.attributes.contentHtml,
                HtmlCompat.FROM_HTML_MODE_COMPACT,
                ImageGetter(context, recyclerView.width, holder.content),
                null
            )
            holder.content.text = html
        }
    }

}