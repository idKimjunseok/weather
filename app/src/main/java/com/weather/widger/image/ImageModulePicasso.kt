package com.weather.widger.image

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.*
import com.weather.util.MyLog
import java.io.File

object ImageModulePicasso : IImageModule {
    override fun init(context: Context) {
        val checkInterceptorPicasso = okhttp3.Interceptor {
            val time = System.currentTimeMillis()
            val originalResponse = it.proceed(it.request())
            val length = originalResponse.body?.contentLength()
            val url = "${originalResponse.request.url}"
            MyLog.img("picasso loaded. [$url] ($length-bytes, ${System.currentTimeMillis() - time}/ms)")

            ImageManager.sendImageSizeAlert(url, length)

            originalResponse.newBuilder().build()
        }

        val httpBuilderPicasso = okhttp3.OkHttpClient().newBuilder()
        httpBuilderPicasso.interceptors().add(checkInterceptorPicasso)
        httpBuilderPicasso.cache(okhttp3.Cache(File(context.cacheDir, "okhttp_cache"), (100 * 1024 * 1024).toLong()))

        val httpClientPicasso = httpBuilderPicasso.build()

        // init Picasso
        val picasso = Picasso.Builder(context).downloader(OkHttp3Downloader(httpClientPicasso))
                .build()
        picasso.isLoggingEnabled = false
        Picasso.setSingletonInstance(picasso)
    }

    fun cancelTag(context: Context?) {
        if (context == null) return

        Picasso.get().cancelTag(context)
    }

    /**
     * cache clear. Picasso 사용시 간헐적으로 이미지 Bitmap이 정상적으로 변환되지 않는 이슈가 있다.
     * 히여 Picasso의 경우에만 앱 재시작시 cache를 초기화 하도록 한다.
     */
    fun clearCache(context:Context) {
        val cacheFile = File(context.cacheDir, "okhttp_cache")
        cacheFile.delete()


        Picasso.get().clearCache()
    }

    override fun defaultBuilder(context: Context, url: String?): RequestCreator {
        val path = url?.let {
            ImageUtil.imageUrlCheck(
                context,
                url
            )
        }
        MyLog.img("defaultBuilder Picasso url = $path")
        return Picasso.get().load(path).tag(context)
    }

    override fun defaultBuilder(context: Context, file: File?): RequestCreator {
        val checkFile = file ?: File("")
        MyLog.img("defaultBuilder Picasso url = $checkFile")
        return Picasso.get().load(checkFile).tag(context)
    }

    override fun internalLoad(context: Context?, loadData: Any?, imageView: ImageView,
                              errorView: View?, loadingView: View?) {
        MyLog.img("internalLoad : [" + loadData.toString() + "]")

        var creator: RequestCreator? = null

        when (loadData) {
            is String -> {
                if (context == null) return
                creator = defaultBuilder(
                    context,
                    loadData
                )
            }
            is File -> {
                if (context == null) return
                creator = defaultBuilder(
                    context,
                    loadData
                )
            }
            is RequestCreator -> creator = loadData
        }

        if (creator == null) {
            return
        }

        imageView.setImageDrawable(null)

        loadingView?.visibility = View.VISIBLE
        errorView?.visibility = View.GONE

        val callback = object : Callback {
            override fun onSuccess() {
                loadingView?.visibility = View.GONE
                errorView?.visibility = View.GONE
            }

            override fun onError(e: java.lang.Exception?) {
                loadingView?.visibility = View.GONE
                errorView?.visibility = View.VISIBLE
            }
        }

        creator.into(imageView, callback)
    }
}
