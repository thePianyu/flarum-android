package cn.duoduo.flarum.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.duoduo.flarum.R
import cn.duoduo.flarum.databinding.HomeFragmentBinding
import cn.duoduo.flarum.api.models.Discussion
import cn.duoduo.flarum.ui.discussion.DiscussionActivity
import cn.duoduo.flarum.utils.ImageUtils
import cn.duoduo.flarum.viewmodel.HomeViewModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.jsoup.Jsoup


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var binding: HomeFragmentBinding
    private var offset = 0

    private val model by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = Adapter()
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(divider)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        adapter.loadMoreModule.setOnLoadMoreListener {
            model.fetchDiscussions(offset)
        }

        model.getDiscussions().observe(viewLifecycleOwner) {
            adapter.loadMoreModule.loadMoreComplete()
            adapter.addData(it)
            offset += it.size
        }
        model.fetchDiscussions(offset)
    }

    private class Adapter: BaseQuickAdapter<Discussion, Adapter.ViewHolder>(R.layout.item_discussion), LoadMoreModule {

        override fun convert(holder: ViewHolder, item: Discussion) {
            // 标题
            holder.title.text = item.attributes.title

            // 内容
            val doc = Jsoup.parse(item.firstPostContent)
            holder.content.text = doc.body().text()

            // 标签
            holder.tags.removeAllViews()
            for (tag in item.tags) {
                holder.tags.addView(Chip(holder.itemView.context).also {
                    it.text = tag.name
                })
            }

            // 头像
            ImageUtils.loadAvatar(item.avatar, holder.avatar, holder.itemView, context)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, DiscussionActivity::class.java)
                intent.putExtra("ID", item.id)
                intent.putExtra("discussionTitle", item.attributes.title)
                context.startActivity(intent)
            }
        }

        private class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.title)
            val content: TextView = itemView.findViewById(R.id.content)
            val tags: ChipGroup = itemView.findViewById(R.id.tags)
            val avatar: ImageView = itemView.findViewById(R.id.avatar)
        }

    }


}