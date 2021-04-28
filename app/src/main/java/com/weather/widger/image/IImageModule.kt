package com.weather.widger.image

import android.content.Context
import android.view.View
import android.widget.ImageView
import java.io.File

interface IImageModule {

    fun init(context: Context)

    /**
     * 기본 builder를 반환한다.
     *
     * @param context Context
     * @param url 이미지 url
     */
    fun defaultBuilder(context: Context, url: String?): Any

    /**
     * 기본 builder를 반환한다.
     *
     * @param context Context
     * @param file File
     */
    fun defaultBuilder(context: Context, file: File?): Any

    fun load(context: Context?, url: String?,
             imageView: ImageView) = load(context, url, imageView, null, null)

    fun load(context: Context?, url: String?, imageView: ImageView,
             errorView: View?) = load(context, url, imageView, errorView, null)

    fun load(context: Context?, url: String?, imageView: ImageView, errorView: View?,
             loadingView: View?) {
        internalLoad(context, url, imageView, errorView, loadingView)
    }

    fun load(context: Context?, file: File?,
             imageView: ImageView) = load(context, file, imageView, null, null)

    fun load(context: Context?, file: File?, imageView: ImageView,
             errorView: View?) = load(context, file, imageView, errorView, null)

    fun load(context: Context?, file: File?, imageView: ImageView, errorView: View?,
             loadingView: View?) {
        internalLoad(context, file, imageView, errorView, loadingView)
    }

    fun load(builder: Any?,
             imageView: ImageView) = load(builder, imageView, null, null)

    fun load(builder: Any?, imageView: ImageView,
             errorView: View?) = load(builder, imageView, errorView, null)

    fun load(builder: Any?, imageView: ImageView,
             errorView: View?, loadingView: View?) {
        internalLoad(null, builder, imageView, errorView, loadingView)
    }

    /**
     * 이미지를 load 한다.
     *
     * @param context Context
     * @param loadData String 과 File 형식만 지원한다.
     * @param imageView 실제 이미지가 보여질 imageView
     * @param errorView 이미지 로드 실패시 보여질 View. default - null.
     * @param loadingView 이미지 로드 중 보여질 View. default - null.
     */
    fun internalLoad(context: Context?, loadData: Any?, imageView: ImageView,
                     errorView: View? = null, loadingView: View? = null)
}
