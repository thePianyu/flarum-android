package cn.duoduo.flarum.ui.discussion

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.core.text.HtmlCompat
import cn.duoduo.flarum.R
import cn.duoduo.flarum.api.models.Post
import cn.duoduo.flarum.ui.ImageActivity
import cn.duoduo.flarum.utils.ImageGetter
import cn.duoduo.flarum.utils.ImageUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.jsoup.internal.StringUtil
import org.xml.sax.XMLReader


class Adapter : BaseQuickAdapter<Post, Adapter.ViewHolder>(R.layout.item_post) {

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
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

        if (html !is SpannableStringBuilder) return

        val spans: Array<out URLSpan>? = html.getSpans(
            0, html.length,
            URLSpan::class.java
        )
        if (spans != null) {
            for (span in spans) {
                val start = html.getSpanStart(span)
                val end = html.getSpanEnd(span)

                val url = span.url

                // 先移除这个Span，再新添加一个自己实现的Span。
                html.removeSpan(span)
                val uri = Uri.parse(url)
                if (!url.startsWith("https://pianyu.org/")) {
                    // 外部链接打开默认浏览器访问
                    html.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val defaultBrowser = Intent.makeMainSelectorActivity(
                                Intent.ACTION_MAIN,
                                Intent.CATEGORY_APP_BROWSER
                            )
                            defaultBrowser.data = uri
                            context.startActivity(defaultBrowser)
                        }
                    }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                } else {
                    // TODO 内部链接跳转activity
                    html.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            Toast.makeText(context, "test url: $url", Toast.LENGTH_SHORT).show()
                            //                                    val intent = Intent(context, UserSpace::class.java)
                            //                                    context.startActivity(intent)
                        }
                    }, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                }
            }
        }

        holder.content.text = html
        holder.content.movementMethod = LinkMovementMethod.getInstance()
        holder.title.text = item.username

        // 加载头像
        if (!StringUtil.isBlank(item.avatar)) {
            ImageUtils.loadAvatar(item.avatar, holder.avatar, holder.itemView, context)
        } else {
            ImageUtils.setDefaultAvatar(holder.avatar, context)
        }

    }

}

/**
 * 处理图片
 */
private class UrlTagHandler(val context: Context) : Html.TagHandler {

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
    private class ClickableImage(private val context: Context, private val url: String) : ClickableSpan() {


        override fun onClick(widget: View) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra("URL", url)
            context.startActivity(intent)
        }
    }
}