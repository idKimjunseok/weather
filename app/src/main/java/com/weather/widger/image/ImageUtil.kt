package com.weather.widger.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.text.TextUtils.isEmpty
import android.util.DisplayMetrics
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.weather.R
import com.weather.util.MyLog
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageUtil {

    companion object {
        var API_SERVER_IMAGE: String? = null
        private val MEGA_BYTE = 1024 * 1024

        // 리사이징할 이미지의 최대 용량. 0.7MB
        val RESIZING_IMAGE_SIZE = (0.9f * MEGA_BYTE).toInt()

        private val RESIZE_LIMIT_X3_HEIGHT = 1080      // x3 이미지 높이 제한
        private val RESIZE_LIMIT_X2_WIDTH = 720       // x2 이미지 너비 제한
        private val RESIZE_LIMIT_X2_HEIGHT = 360      // x2 이미지 높이 제한

        val THUMBNAIL = 1
        val PANORAMA = 0
        val HOT_STAY = 2
        val KAKAO = 10001
        val WATER_MARK = 10002
        val RESIZE = 10003
        val RESIZE_OF_PADDING = 10004


        private fun loadImageServer(context: Context): String {
            return  "https://www.metaweather.com/static/img/weather/png/"
        }

        fun getHeightAtRatio(context: Context, ratioW: Int, ratioH: Int): Int {
            return getHeightAtRatioWithPadding(
                context,
                ratioW,
                ratioH,
                0
            )
        }

        fun getHeightAtRatioWithPadding(
            context: Context,
            ratioW: Int,
            ratioH: Int,
            padding: Int
        ): Int {
            var displayWidth = 0
            try {
                val metrics = context.resources.displayMetrics
                displayWidth = metrics.widthPixels - padding
            } catch (e: Exception) {
                MyLog.e(e)
            }

            return displayWidth * ratioH / ratioW
        }

        fun getImagePath(
            context: Context,
            type: Int,
            path: String,
            vararg size: Int
        ): String { // size 비율
            if (path.startsWith("http") || path.startsWith("https")) {
                return path
            }

            var strUrl =
                loadImageServer(context) // 기본 이미지 URL

//        if (IMAGE_SERVER_VERSION !== 1) {
//            /** 업데이트 되는 이미지 패스  */
//            return strUrl
//        }

            /** 기본 이미지 패스  */
            var width =
                RESIZE_LIMIT_X2_WIDTH
            var height =
                RESIZE_LIMIT_X2_HEIGHT
            val metrics = context.resources.displayMetrics
            when (type) {
                THUMBNAIL // 섬네일
                -> {
                    width = context.resources.getDimensionPixelSize(R.dimen.thumb_image_height)
                    height = context.resources.getDimensionPixelSize(R.dimen.thumb_image_height)
                }
                PANORAMA  // 파노라마
                -> if (metrics.density >= 2.0f) {
                    width = metrics.widthPixels
                    height = width / 2
                }
                HOT_STAY  // 핫스테이
                -> if (metrics.density >= 2.0f) {
                    width = metrics.widthPixels
                    height = width * 4 / 5
                }
                KAKAO // 카카오 공유이미지
                -> {
                    width =
                        RESIZE_LIMIT_X2_WIDTH
                    height =
                        RESIZE_LIMIT_X2_HEIGHT
                }
                RESIZE // 비율 리사이즈
                -> {
                    width = metrics.widthPixels
                    height =
                        getHeightAtRatio(
                            context,
                            size[0],
                            size[1]
                        )
                }
                RESIZE_OF_PADDING -> {
                    width = metrics.widthPixels
                    height =
                        getHeightAtRatioWithPadding(
                            context,
                            size[0],
                            size[1],
                            size[2]
                        )
                }
                WATER_MARK // 워터마크
                -> {
                    width = metrics.widthPixels
                    height =
                        RESIZE_LIMIT_X3_HEIGHT
                }
                else -> {
                    width = context.resources.getDimensionPixelSize(R.dimen.thumb_image_height)
                    height = context.resources.getDimensionPixelSize(R.dimen.thumb_image_height)
                }
            }
            strUrl += "/resize_" + width + "x" + height + path
            return strUrl
        }

        fun getStatusBarHeight(context: Context): Int {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        fun dpToPx(context: Context, dp: Float): Int {
            try {
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    context.resources.displayMetrics
                )
                return Math.round(px)
            } catch (e: Exception) {
                MyLog.e(e)
                return 0
            }
        }

        fun PixelToDp(context: Context, pixel: Int): Int {
            val metrics: DisplayMetrics = context.resources.displayMetrics
            val dp = pixel / (metrics.densityDpi / 160f)
            return dp.toInt()
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }

            // We ask for the bounds if they have been set as they would be most
            // correct, then we check we are  > 0
            val width = if (!drawable.bounds.isEmpty)
                drawable.bounds.width()
            else
                drawable.intrinsicWidth

            val height = if (!drawable.bounds.isEmpty)
                drawable.bounds.height()
            else
                drawable.intrinsicHeight

            // Now we check we are > 0
            val bitmap = Bitmap.createBitmap(
                if (width <= 0) 1 else width, if (height <= 0) 1 else height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }

        fun convertBitmapToFile(bitmap: Bitmap, filePath: String): File {
            val file = File(filePath)
            val os: OutputStream
            try {
                file.createNewFile()
                os = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            } catch (ex: Exception) {
                MyLog.e(ex)
            }

            return file
        }

        @Synchronized
        fun decodeSampledBitmapFromFile(
            metrics: DisplayMetrics,
            filename: String,
            maxImageSize: Int,
            degree: Int
        ): Bitmap? {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filename, options)
            var scale = 1
            while (options.outWidth * options.outHeight * (1 / Math.pow(
                    scale.toDouble(),
                    2.0
                )) > maxImageSize
            ) {
                scale++
            }
            val deviceWidth = metrics.widthPixels
            val deviceHeight = metrics.heightPixels
            var imgWidth = options.outWidth
            var sampleSize = 1
            while (imgWidth >= deviceWidth) {
                sampleSize++
                imgWidth /= 2
            }
            sampleSize -= if (sampleSize % 2 == 0) 0 else 1
            options.inSampleSize = sampleSize
            options.inJustDecodeBounds = false
            MyLog.d("inSampleSize : $sampleSize / $scale")
            try {
                var bm: Bitmap? = BitmapFactory.decodeFile(filename, options)
                if (scale > 1) {
                    val w = bm!!.width
                    val h = bm.height
                    var y = Math.sqrt(maxImageSize / (w.toDouble() / h))
                    y = if (y > deviceHeight) deviceHeight.toDouble() else y
                    val x = y / h * w
                    val scaledBitmap = Bitmap.createScaledBitmap(bm, x.toInt(), y.toInt(), true)
                    bm.recycle()
                    bm = scaledBitmap
                    System.gc()
                }
                if (bm != null && degree > 0) bm =
                    rotateBitmap(bm, degree)
                return bm
            } catch (oe: OutOfMemoryError) {
                val sample_size = if (options.inSampleSize % 2 == 0) 2 else 1
                options.inSampleSize += sample_size
                var bm: Bitmap? = BitmapFactory.decodeFile(filename, options)
                if (scale > 1) {
                    val w = bm!!.width
                    val h = bm.height
                    val y = Math.sqrt(maxImageSize / (w.toDouble() / h))
                    val x = y / h * w
                    val scaledBitmap = Bitmap.createScaledBitmap(bm, x.toInt(), y.toInt(), true)
                    bm.recycle()
                    bm = scaledBitmap
                    System.gc()
                }
                if (bm != null && degree > 0) {
                    bm = rotateBitmap(
                        bm,
                        degree
                    )
                }
                return bm
            }

        }

        fun getOrientationDegrees(exifOrientation: Int): Int {
            when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            }
            return 0
        }

        fun rotateBitmap(bitmap: Bitmap?, degree: Int): Bitmap? {
            var bitmap = bitmap
            if (degree != 0 && bitmap != null) {
                val m = Matrix()
                m.setRotate(
                    degree.toFloat(),
                    bitmap.width.toFloat() / 2,
                    bitmap.height.toFloat() / 2
                )
                try {
                    val converted =
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
                    if (bitmap != converted) {
                        bitmap.recycle()
                        bitmap = converted
                        System.gc()
                    }
                } catch (ex: OutOfMemoryError) {
                }

            }
            return bitmap
        }

        fun getVectorImage(context: Context, @DrawableRes resId: Int): Drawable? {
            return VectorDrawableCompat.create(context.resources, resId, null)
        }

        fun setVectorImage(
            context: Context,
            view: ImageView, @DrawableRes resId: Int, @ColorRes color: Int
        ) {
            view.setImageDrawable(
                getVectorImage(
                    context,
                    resId
                )
            )
            DrawableCompat.setTint(view.drawable, ContextCompat.getColor(context, color))
        }

        fun imageUrlCheck(context: Context, url: String): String {
            var url = url
            if (isEmpty(url)) return "is_empty"
            if (!url.startsWith("http")) {
                url = "${loadImageServer(context)}${url}.png"
            }
            return url
        }
    }
}