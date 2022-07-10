package cn.duoduo.flarum.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cn.duoduo.flarum.R
import cn.duoduo.flarum.databinding.MyFragmentBinding
import cn.duoduo.flarum.ui.LoginActivity
import cn.duoduo.flarum.viewmodel.MyViewModel

class MyFragment : Fragment() {

    companion object {
        fun newInstance() = MyFragment()
    }

    private lateinit var binding: MyFragmentBinding
    private val model by lazy {
        ViewModelProvider(this).get(MyViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.my_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogin.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}