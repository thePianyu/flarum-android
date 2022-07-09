package cn.duoduo.flarum.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.duoduo.flarum.R
import com.bm.library.PhotoView
import com.bumptech.glide.Glide

class ImageActivity : AppCompatActivity() {

    lateinit var photoView: PhotoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        photoView = findViewById(R.id.photoview)
        photoView.enable()
        Glide.with(this)
            .load(intent.getStringExtra("URL"))
            .into(photoView)
        photoView.setOnClickListener {
            finish()
        }

        supportActionBar!!.hide()
    }

}