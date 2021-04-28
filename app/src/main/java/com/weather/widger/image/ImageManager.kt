
package com.weather.widger.image

import android.content.Context

object ImageManager {

    fun init(context: Context) {
        ImageModulePicasso.init(context)
        ImageModuleGlide.init(context)
    }

    val picasso: ImageModulePicasso by lazy {
        ImageModulePicasso
    }

    val glide: ImageModuleGlide by lazy {
        ImageModuleGlide
    }


    fun sendImageSizeAlert(url: String, imageSize: Long?) {
        if (imageSize == null || imageSize <= (1.5 * 1024 * 1024)) { // 1.5M 보다 큰 경우에만 전송한다.
            return
        }

    }
}
