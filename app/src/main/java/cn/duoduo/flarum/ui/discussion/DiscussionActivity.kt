package cn.duoduo.flarum.ui.discussion


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.duoduo.flarum.R
import cn.duoduo.flarum.api.models.Post
import cn.duoduo.flarum.databinding.ActivityDiscussionBinding
import cn.duoduo.flarum.ui.ImageActivity
import cn.duoduo.flarum.utils.ImageGetter
import cn.duoduo.flarum.utils.ImageUtils
import cn.duoduo.flarum.viewmodel.DiscussionViewModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.jsoup.internal.StringUtil
import org.xml.sax.XMLReader


class DiscussionActivity : AppCompatActivity() {

    private val model: DiscussionViewModel by viewModels()
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDiscussionBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_discussion)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "问答"

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

        // fab 动画
        var fabOpened = false
        binding.fabMore.setOnClickListener {
            if (!fabOpened) {
                binding.fabLike.visibility = View.VISIBLE
                binding.fabLike.animate().translationY(-resources.getDimension(R.dimen.dp_60))
                binding.fabReply.visibility = View.VISIBLE
                binding.fabReply.animate().translationY(-resources.getDimension(R.dimen.dp_60) * 2)
                binding.fabShare.visibility = View.VISIBLE
                binding.fabShare.animate().translationY(-resources.getDimension(R.dimen.dp_60) * 3)
            } else {
                binding.fabLike.animate().translationY(0F).withEndAction { binding.fabLike.visibility = View.GONE }
                binding.fabReply.animate().translationY(0F).withEndAction { binding.fabReply.visibility = View.GONE }
                binding.fabShare.animate().translationY(0F).withEndAction { binding.fabShare.visibility = View.GONE }
            }
            fabOpened = !fabOpened
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

}