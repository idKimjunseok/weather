package com.weather.widger.image

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.weather.base.GlideApp
import com.weather.base.GlideRequest
import com.weather.util.MyLog
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.io.InputStream

object ImageModuleGlide : IImageModule {

    override fun init(context: Context) {
        val builder = OkHttpClient.Builder()
                .cache(Cache(File(context.cacheDir, "okhttp_cache"), (100 * 1024 * 1024)))
                .addInterceptor {
                    val time = System.currentTimeMillis()
                    val originalResponse = it.proceed(it.request())
                    val length = originalResponse.body?.contentLength()
                    val url = "${originalResponse.request.url}"
                    MyLog.img("glide loaded. [$url] ($length-bytes, " + (System.currentTimeMillis() - time) + "/ms)")

                    ImageManager.sendImageSizeAlert(url, length)

                    originalResponse
                }
        GlideApp.get(context).registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(builder.build()))

    }

    override fun defaultBuilder(context: Context, url: String?): GlideRequest<Bitmap> {
        val path = url?.let {
            ImageUtil.imageUrlCheck(
                context,
                url
            )
        }
//        val path = ImageUtil.imageUrlCheck(context, url)
        MyLog.img("defaultBuilder Glide url = $path")
        return GlideApp.with(context).asBitmap().load(path)
    }

    override fun defaultBuilder(context: Context, file: File?): GlideRequest<Bitmap> {
        val checkFile = file ?: File("")
        MyLog.img("defaultBuilder Glide file = $checkFile")
        return GlideApp.with(context).asBitmap().load(checkFile)
    }

    /**
     * Glide 를 이용해 이미지를 load 한다.
     *
     * @param context Context
     * @param loadData String 과 File 형식만 지원한다.
     * @param imageView 실제 이미지가 보여질 imageView
     * @param errorView 이미지 로드 실패시 보여질 View. default - null.
     * @param loadingView 이미지 로드 중 보여질 View. default - null.
     */
    override fun internalLoad(context: Context?, loadData: Any?, imageView: ImageView,
                              errorView: View?, loadingView: View?) {
        MyLog.img("internalLoadGlide : [" + loadData.toString() + "]")

        var builder: GlideRequest<Bitmap>? = null

        @Suppress("UNCHECKED_CAST")
        when (loadData) {
            is String -> {
                if (context == null) return
                builder = defaultBuilder(
                    context,
                    loadData
                )
            }
            is File -> {
                if (context == null) return
                builder = defaultBuilder(
                    context,
                    loadData
                )
            }
            is GlideRequest<*> -> builder = loadData as GlideRequest<Bitmap>
        }

        if (builder == null) {
            return
        }

        imageView.setImageDrawable(null)

        loadingView?.visibility = View.VISIBLE
        errorView?.visibility = View.GONE

        val listener = object : RequestListener<Bitmap> {
            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?,
                                         dataSource: DataSource?,
                                         isFirstResource: Boolean): Boolean {
                loadingView?.visibility = View.GONE
                errorView?.visibility = View.GONE
                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?,
                                      isFirstResource: Boolean): Boolean {
                loadingView?.visibility = View.GONE
                errorView?.visibility = View.VISIBLE
                return false
            }
        }

        builder.listener(listener).into(imageView)
    }
}
