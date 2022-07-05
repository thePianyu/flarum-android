package cn.duoduo.flarum.ui.main


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
                ImageGetter(context, recyclerView.width - 50, holder.content),
                UrlTagHandler(context)
            )

            if (html is SpannableStringBuilder) {
                val spannableStringBuilder = html as SpannableStringBuilder
                val objs: Array<out URLSpan>? = spannableStringBuilder.getSpans(
                    0, spannableStringBuilder.length,
                    URLSpan::class.java
                )
                if (objs != null) {
                    for (obj in objs) {
                        val start = spannableStringBuilder.getSpanStart(obj)
                        val end = spannableStringBuilder.getSpanEnd(obj)

                        val url = obj.url

                        // 先移除这个Span，再新添加一个自己实现的Span。
                        spannableStringBuilder.removeSpan(obj)
                        val uri = Uri.parse(url);
                        if(!url.startsWith("https://pianyu.org/")){
                            // 外部链接打开默认浏览器访问
                            spannableStringBuilder.setSpan(object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    val defaultBrowser = Intent.makeMainSelectorActivity(
                                        Intent.ACTION_MAIN,
                                        Intent.CATEGORY_APP_BROWSER
                                    )
                                    defaultBrowser.data = uri
                                    context.startActivity(defaultBrowser)
                                }
                            }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        }else{
                            // TODO 内部链接跳转activity
                            spannableStringBuilder.setSpan(object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    Toast.makeText(context, "test url: $url", Toast.LENGTH_SHORT).show()
//                                    val intent = Intent(context, UserSpace::class.java)
//                                    context.startActivity(intent)
                                }
                            }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
                holder.content.text = spannableStringBuilder
                holder.content.movementMethod = LinkMovementMethod.getInstance();
                holder.title.text = item.username

                // 加载头像
                if (!StringUtil.isBlank(item.avatar)) {
                    ImageUtils.loadAvatar(item.avatar, holder.avatar, holder.itemView, context)
                } else {
                    ImageUtils.setDefaultAvatar(holder.avatar, context)
                }
            }
        }

    }

    /**
     * 处理图片
     */
    private class UrlTagHandler(context: Context): Html.TagHandler {

        private var context: Context

        init {
            this.context = context.applicationContext
        }

        override fun handleTag(
            opening: Boolean,
            tag: String?,
            output: Editable?,
            xmlReader: XMLReader?
        ) {
            if (tag != null) {
                // 处理标签<img>
                if (tag.lowercase() == "img") {
                    // 获取长度
                    val len: Int = output!!.length
                    // 获取图片地址
                    val images = output.getSpans(len - 1, len, ImageSpan::class.java)
                    for (img in images) {
                        val imgURL = img.source
                        // 使图片可点击并监听点击事件
                        output.setSpan(
                            ClickableImage(context, imgURL!!),
                            len - 1,
                            len,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        }
        /**
         * 图片点击
         */
        private class ClickableImage(context: Context, url: String) : ClickableSpan() {

            private val url: String
            private val context: Context

            init {
                this.context = context
                this.url = url
            }

            override fun onClick(widget: View) {
                // TODO 进行图片点击之后的处理
                Toast.makeText(context, "Image source: $url", Toast.LENGTH_LONG).show()
            }
        }
    }

}