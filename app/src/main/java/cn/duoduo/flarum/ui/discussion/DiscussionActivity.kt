package cn.duoduo.flarum.ui.discussion


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.duoduo.flarum.R
import cn.duoduo.flarum.adapter.PostsAdapter
import cn.duoduo.flarum.databinding.ActivityDiscussionBinding
import cn.duoduo.flarum.network.RetrofitClient
import cn.duoduo.flarum.viewmodel.DiscussionViewModel
import com.chad.library.adapter.base.BaseQuickAdapter


class DiscussionActivity : AppCompatActivity() {

    private val model: DiscussionViewModel by viewModels()
    private var offset = 0

    private lateinit var replyBottomSheet: ReplyBottomSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDiscussionBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_discussion)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "问答"

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = PostsAdapter()
        adapter.setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn)

        // 加载更多
        adapter.loadMoreModule.setOnLoadMoreListener {
            model.fetchPosts(offset)
        }

        // header
        val view = layoutInflater.inflate(R.layout.item_post_header, binding.recyclerView, false)
        adapter.addHeaderView(view)

        binding.recyclerView.adapter = adapter

        // 拉取内容
        model.fetchDiscussion(intent.getStringExtra("ID")!!)

        model.getDiscussion().observe(this) {
            // 设置标题
            view.findViewById<TextView>(R.id.title_discussion).text = it.attributes.title
            model.fetchPosts(offset)
        }

        model.getPosts().observe(this) {
            offset += it.size
            adapter.addData(it)
            adapter.loadMoreModule.loadMoreComplete()
            if (it.isEmpty())
                adapter.loadMoreModule.loadMoreEnd(true)
        }

        model.getSubmitPost().observe(this) {
            if (it == null) {
                Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show()
                replyBottomSheet.dismiss()
            } else {
                Toast.makeText(this, "发布失败: ${it.message}", Toast.LENGTH_SHORT).show()
            }
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

        // 分享
        binding.fabShare.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${RetrofitClient.BASE_URL}d/${intent.getStringExtra("ID")!!}")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        // 发布Post
        replyBottomSheet = ReplyBottomSheet(object : ReplyBottomSheet.OnClickListener {
            override fun onSubmit(content: String) {
                model.submitPost(content)
            }
        })

        binding.fabReply.setOnClickListener {
            replyBottomSheet.show(supportFragmentManager, ReplyBottomSheet.TAG)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

}