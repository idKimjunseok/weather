package com.weather.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseKotlinActivity<T : ViewDataBinding, L : BaseKotlinViewModel> :
    AppCompatActivity(), View.OnClickListener {

    lateinit var viewDataBinding: T

    abstract val layoutResourceId: Int

    abstract val viewModel: L

    abstract fun initStartView()

    abstract fun initDataBinding()

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mContext = this
        viewDataBinding = DataBindingUtil.setContentView(this, layoutResourceId)

        initStartView()
        initDataBinding()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

}