package com.weather.widger.image

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.widget.TextViewCompat
import com.squareup.picasso.RequestCreator
import com.weather.R
import com.weather.base.GlideRequest
import kotlinx.android.synthetic.main.cell_load_image_view.view.*
import java.io.File


/**
 * 서버에서 이미지를 받아 처리하는 ImageView Class 이다.
 */
class ImageLoadView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    enum class LOAD_TYPE {
        G, // glide
        P // picasso
    }

    var leftTop: Float = 0f
        set(value) {
            field = value
            updateSelectableImageViewRadii()
        }
    var rightTop: Float = 0f
        set(value) {
            field = value
            updateSelectableImageViewRadii()
        }
    var rightBottom: Float = 0f
        set(value) {
            field = value
            updateSelectableImageViewRadii()
        }
    var leftBottom: Float = 0f
        set(value) {
            field = value
            updateSelectableImageViewRadii()
        }

    var circleType: Boolean = false

    private val drawClipPath = Path()
    private val drawRect = RectF(0f, 0f, 0f, 0f)

    private var isNougat: Boolean = false

    init {
        LayoutInflater.from(getContext()).inflate(R.layout.cell_load_image_view, this, true)

        isNougat = Build.VERSION.SDK_INT == Build.VERSION_CODES.N
        val localImageView  = if (isNougat) this.selectable_imageView else this.imageView

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ImageLoadView, defStyleAttr, 0)
            leftBottom = a.getDimension(R.styleable.ImageLoadView_left_bottom_radius, 0f)
            leftTop = a.getDimension(R.styleable.ImageLoadView_left_top_radius, 0f)
            rightBottom = a.getDimension(R.styleable.ImageLoadView_right_bottom_radius, 0f)
            rightTop = a.getDimension(R.styleable.ImageLoadView_right_top_radius, 0f)

            circleType = a.getBoolean(R.styleable.ImageLoadView_circleType, false)

            val errorTextColor: ColorStateList? = a.getColorStateList(R.styleable.ImageLoadView_errorTextColor)
            if (errorTextColor != null) {
                errorView.setTextColor(errorTextColor)
            }

            val errorText: String? = a.getString(R.styleable.ImageLoadView_errorText)
            if (!errorText.isNullOrEmpty()) {
                this.errorView.text = errorText
            }

            val errorTextStyle: Int = a.getResourceId(R.styleable.ImageLoadView_errorTextStyle, -1)
            if (errorTextStyle > 0) {
                TextViewCompat.setTextAppearance(errorView, errorTextStyle)
            }

            val index = a.getInt(R.styleable.ImageLoadView_android_scaleType, -1)
            if (index >= 0) {
                localImageView.scaleType = ImageView.ScaleType.values().find { it.ordinal == index }
            }

            a.recycle()
        }

        if (isNougat) {
            selectable_imageView.isOval = circleType
            updateSelectableImageViewRadii()
        }

        localImageView.visibility = View.VISIBLE
        localImageView.setImageDrawable(background)

        setBackgroundResource(R.color.b_000)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (!isNougat) {
            drawRoundRect(canvas)
        }
        super.dispatchDraw(canvas)
    }

    private fun drawRoundRect(canvas: Canvas?) {
        drawRect.set(0f, 0f, this.width.toFloat(), this.height.toFloat())
        drawClipPath.reset()
        if (circleType) {
            val radius = this.width.toFloat() / 2
            drawClipPath.addRoundRect(drawRect, floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius), Path.Direction.CW)
        } else {
            drawClipPath.addRoundRect(drawRect, floatArrayOf(leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom), Path.Direction.CW)
        }
        canvas?.clipPath(drawClipPath)
    }

    private fun updateSelectableImageViewRadii() {
        if (!isNougat) {
            return
        }
        val density = this.resources.displayMetrics.density
        selectable_imageView.setCornerRadiiDP(leftTop / density, rightTop / density, leftBottom / density, rightBottom / density)
    }

    /**
     * loadData에 전달되는 image를 load한다.
     *
     * @param type LOAD_TYPE.G - glide, LOAD_TYPE.P - picasso. default - LOAD_TYPE.G
     * @param isShowLoading 이미지로드 관련 프로그레스바를 보일지 여부. default - true
     * @param isShowError 이미지로드 실패시 에러이미지를 보일지 여부 default - true
     */
    @JvmOverloads
    fun load(url: String?, type: LOAD_TYPE = LOAD_TYPE.G, isShowError: Boolean = false,
             isShowLoading: Boolean = true) {
        val localImageView = if (isNougat) this.selectable_imageView else this.imageView
        val localErrorView = if (isShowError) this.errorView else null
        val localLoadingView = if (isShowLoading) this.loadingView else null

        when (type) {
            LOAD_TYPE.G -> ImageManager.glide.load(context, url, localImageView, localErrorView, localLoadingView) // Glide
            else -> ImageManager.picasso.load(context, url, localImageView, localErrorView, localLoadingView) // Picasso
        }
    }

    /**
     * loadData에 전달되는 image를 load한다.
     *
     * @param type LOAD_TYPE.G - glide, LOAD_TYPE.P - picasso. default - LOAD_TYPE.G
     * @param isShowLoading 이미지로드 관련 프로그레스바를 보일지 여부. default - true
     * @param isShowError 이미지로드 실패시 에러이미지를 보일지 여부 default - true
     */
    @JvmOverloads
    fun load(file: File?, type: LOAD_TYPE = LOAD_TYPE.G, isShowError: Boolean = false,
             isShowLoading: Boolean = false) {
        val localImageView = if (isNougat) this.selectable_imageView else this.imageView
        val localErrorView = if (isShowError) this.errorView else null
        val localLoadingView = if (isShowLoading) this.loadingView else null

        when (type) {
            LOAD_TYPE.G -> ImageManager.glide.load(context, file, localImageView, localErrorView, localLoadingView) // Glide
            else -> ImageManager.picasso.load(context, file, localImageView, localErrorView, localLoadingView) // Picasso
        }
    }

    /**
     * builder를 load한다.
     *
     * @param builder BitmapRequestBuilder. defaultGlide 에서 생성된 객체를 전달해야 한다.
     * @param isShowLoading 이미지로드 관련 프로그레스바를 보일지 여부. default - true
     * @param isShowError 이미지로드 실패시 에러이미지를 보일지 여부 default - true
     */
    @JvmOverloads
    fun load(builder: GlideRequest<Bitmap>?, isShowError: Boolean = false,
             isShowLoading: Boolean = false) {
        val localImageView = if (isNougat) this.selectable_imageView else this.imageView
        val localErrorView = if (isShowError) this.errorView else null
        val localLoadingView = if (isShowLoading) this.loadingView else null

        ImageManager.glide.load(builder, localImageView, localErrorView, localLoadingView)
    }

    /**
     * builder를 load한다.
     *
     * @param builder BitmapRequestBuilder. defaultPicasso 에서 생성된 객체를 전달해야 한다.
     * @param isShowLoading 이미지로드 관련 프로그레스바를 보일지 여부. default - true
     * @param isShowError 이미지로드 실패시 에러이미지를 보일지 여부 default - true
     */
    @JvmOverloads
    fun load(builder: RequestCreator?, isShowError: Boolean = false,
             isShowLoading: Boolean = false) {
        val localImageView = if (isNougat) this.selectable_imageView else this.imageView
        val localErrorView = if (isShowError) this.errorView else null
        val localLoadingView = if (isShowLoading) this.loadingView else null

        ImageManager.picasso.load(builder, localImageView, localErrorView, localLoadingView)
    }

    /**
     * bitmap image
     */
    fun load(bitmap: Bitmap) {
        (if (isNougat) this.selectable_imageView else this.imageView)
            .setImageBitmap(bitmap)
    }

    /**
     * 로컬 이미지를 load한다.
     */
    fun load(resourceId: Int) {
        (if (isNougat) this.selectable_imageView else this.imageView)
            .setImageResource(resourceId)
    }
}