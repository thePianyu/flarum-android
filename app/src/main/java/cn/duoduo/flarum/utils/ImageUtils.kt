package cn.duoduo.flarum.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import cn.duoduo.flarum.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

object ImageUtils {
    /**
     * 设置默认头像
     */
    fun setDefaultAvatar(view: ImageView, context: Context) {
        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(
            context.resources,
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.default_avatar_akkarin
            )
        )
        circularBitmapDrawable.cornerRadius = 1000f
        view.setImageDrawable(circularBitmapDrawable)
    }

    /**
     * glide加载头像
     */
    fun loadAvatar(url: String?, view: ImageView, parent: View, context: Context){

        if(url.isNullOrEmpty()){
            setDefaultAvatar(view, context)
        }

        Glide.with(parent)
            .load(url)
            .circleCrop()
            .override(100, 100)
            .addListener(object : RequestListener<Drawable?> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    setDefaultAvatar(view, context)
                    return true
                }
            })
            .into(view)
    }
}