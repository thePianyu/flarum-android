package cn.duoduo.flarum.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.text.Html
import androidx.core.app.ActivityCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget

class ImageGetter(private val context: Context): Html.ImageGetter {

    override fun getDrawable(source: String?): Drawable {
        val drawable = LevelListDrawable()
        return drawable
    }

}