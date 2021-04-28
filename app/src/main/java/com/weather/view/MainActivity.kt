package com.weather.view

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.weather.R
import com.weather.base.BaseKotlinActivity
import com.weather.databinding.ActivityMainBinding
import com.weather.util.showToast
import com.weather.view.adapter.WeatherAdapter
import com.weather.view.viewmodel.MainViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseKotlinActivity<ActivityMainBinding, MainViewModel>(), OnRefreshListener {
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel()

    private val weatherAdapter: WeatherAdapter by inject()

    override fun initStartView() {

        viewModel.setProgress(viewDataBinding.progress)
        viewDataBinding.recyclerview.run {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            val dividerItemDecoration = DividerItemDecoration(this.context, LinearLayoutManager(this@MainActivity).orientation)
            dividerItemDecoration.setDrawable(resources.getDrawable(R.color.g_030))
            addItemDecoration(dividerItemDecoration)
            setHasFixedSize(true)
        }

        viewDataBinding.swipe.setOnRefreshListener(this)
        viewModel.getSearch("se")
    }

    override fun initDataBinding() {
        viewModel.search.observe(this, {
            viewModel.getLocation(it)
        })

        viewModel.list.observe(this, {
            viewDataBinding.swipe.isRefreshing = false
            weatherAdapter.setItems(it)
        })

        viewModel.error.observe(this,  {
            it.showToast(this)
        })

    }

    override fun onClick(v: View?) {
    }

    override fun onRefresh() {
        weatherAdapter.clearItems()
        viewModel.search.value?.let {
            viewModel.getLocation(it)
        }
    }
}