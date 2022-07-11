package cn.duoduo.flarum.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.duoduo.flarum.R
import cn.duoduo.flarum.adapter.DiscussionsAdapter
import cn.duoduo.flarum.databinding.HomeFragmentBinding
import cn.duoduo.flarum.viewmodel.HomeViewModel


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var binding: HomeFragmentBinding
    private var offset = 0

    private val model by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = DiscussionsAdapter()
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(divider)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        adapter.loadMoreModule.setOnLoadMoreListener {
            model.fetchDiscussions(offset)
        }

        model.getDiscussions().observe(viewLifecycleOwner) {
            adapter.loadMoreModule.loadMoreComplete()
            adapter.addData(it)
            offset += it.size
        }
        model.fetchDiscussions(offset)
    }




}