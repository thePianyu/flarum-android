package cn.duoduo.flarum.adapter

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.duoduo.flarum.R
import cn.duoduo.flarum.api.models.Discussion
import cn.duoduo.flarum.ui.discussion.DiscussionActivity
import cn.duoduo.flarum.utils.ImageUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.jsoup.Jsoup

class DiscussionsAdapter: BaseQuickAdapter<Discussion, DiscussionsAdapter.ViewHolder>(R.layout.item_discussion), LoadMoreModule {

    override fun convert(holder: ViewHolder, item: Discussion) {
        // 标题
        holder.title.text = item.attributes.title

        // 内容
        val doc = Jsoup.parse(item.includedFirstPost!!.contentHtml)
        holder.content.text = doc.body().text()

        // 标签
        holder.tags.removeAllViews()
        for (tag in item.tags) {
            holder.tags.addView(Chip(holder.itemView.context).also {
                it.text = tag.name
            })
        }

        // 头像
        ImageUtils.loadAvatar(item.user!!.avatarUrl, holder.avatar, context)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DiscussionActivity::class.java)
            intent.putExtra("ID", item.id)
            intent.putExtra("discussionTitle", item.attributes.title)
            context.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val content: TextView = itemView.findViewById(R.id.content)
        val tags: ChipGroup = itemView.findViewById(R.id.tags)
        val avatar: ImageView = itemView.findViewById(R.id.avatar)
    }

}