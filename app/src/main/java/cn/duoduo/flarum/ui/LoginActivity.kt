package cn.duoduo.flarum.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.duoduo.flarum.R
import cn.duoduo.flarum.databinding.ActivityLoginBinding
import cn.duoduo.flarum.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val model: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.fabLogin.setOnClickListener {
            model.login(binding.editUsername.text.toString(), binding.editPassword.text.toString())
        }

        model.getLoginResp().observe(this) {
            Toast.makeText(this, resources.getString(R.string.login_success), Toast.LENGTH_SHORT).show()

            val edit = this.getSharedPreferences("Flarum", Context.MODE_PRIVATE).edit()
            edit.putString("TOKEN", it.token)
            edit.apply()

            finish()
        }

        model.getLoginError().observe(this) {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.login)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}