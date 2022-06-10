package cn.duoduo.flarum.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.duoduo.flarum.R
import cn.duoduo.flarum.databinding.ActivityMainBinding
import cn.duoduo.flarum.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {

            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> HomeFragment.newInstance()
                    1 -> MyFragment.newInstance()
                    else -> throw IllegalArgumentException()
                }
            }

        }

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> binding.viewPager.currentItem = 0
                R.id.my -> binding.viewPager.currentItem = 1
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNav.menu[position].isChecked = true
            }
        })

    }

}