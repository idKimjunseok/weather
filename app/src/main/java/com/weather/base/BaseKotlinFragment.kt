package com.weather.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseKotlinFragment <T: ViewDataBinding, R: BaseKotlinViewModel> : Fragment() {

    lateinit var viewDataBinding: T

    abstract val layoutResourceId: Int

    /**
     *  by viewModel() : 해당 클래스 생성 뷰모델
     *  by sharedViewModel() : Activity에서 만들 뷰 모델 Fragment에서 가져다 쓸때
     */
    abstract val viewModel: R

    lateinit var callback: OnBackPressedCallback

    abstract fun initStartView()

    abstract fun initDataBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container,false)

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStartView()
        initDataBinding()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}