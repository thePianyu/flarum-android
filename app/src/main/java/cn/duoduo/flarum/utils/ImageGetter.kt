package cn.duoduo.flarum.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class ImageGetter(private val context: Context, private val width: Int, private val textView: TextView) : Html.ImageGetter {

    override fun getDrawable(source: String?): Drawable {

        val drawable = LevelListDrawable()

        Glide
            .with(context)
            .asBitmap()
            .load(source)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val bitmapDrawable = resource.toDrawable(context.resources)
                    drawable.addLevel(1, 1, bitmapDrawable)
                    val scale = resource.height.toDouble() / resource.width.toDouble()
                    val imgHeight = scale * resource.height
                    val imgWidth = width
                    drawable.setBounds(0, 0, imgWidth, imgHeight.toInt())
                    drawable.level = 1

                    textView.invalidate()
                    textView.text = textView.text
                }
                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
        return drawable
    }

}