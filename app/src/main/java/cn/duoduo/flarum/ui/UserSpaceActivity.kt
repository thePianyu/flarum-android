package cn.duoduo.flarum.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import cn.duoduo.flarum.R
import cn.duoduo.flarum.databinding.ActivityMainBinding
import cn.duoduo.flarum.databinding.ActivityUserSpaceBinding
import cn.duoduo.flarum.utils.ImageUtils
import cn.duoduo.flarum.viewmodel.UserSpaceViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation

class UserSpaceActivity : AppCompatActivity() {

    private val model: UserSpaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityUserSpaceBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_space)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        model.getUser().observe(this) {
            binding.toolbarLayout.title = it.attributes.displayName
            Glide.with(this)
                .load(it.attributes.avatarUrl ?: R.drawable.default_avatar_akkarin)
                .circleCrop()
                .error(R.drawable.default_avatar_akkarin)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(3, 2)))
                .into(binding.avatar)
        }
        model.fetchUser(intent.getStringExtra("ID") ?: "-1")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

}