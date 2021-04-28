package com.weather.view.adapter

import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.weather.model.Weather
import com.weather.widger.image.ImageLoadView

object WeatherBindingAdapter {

    @BindingAdapter("setimg")
    @JvmStatic
    fun setImg(
            loadview: ImageLoadView,
            setdata: Weather
    ) {
        setdata?.let {
            loadview.load(setdata.weather_state_abbr)
        }
    }

    @BindingAdapter("state_name")
    @JvmStatic
    fun setStateName(
            textview: AppCompatTextView,
            setdata: Weather
    ) {
        textview.text = setdata.weather_state_name
    }

    @BindingAdapter("the_temp")
    @JvmStatic
    fun setTheTemp(
            textview: AppCompatTextView,
            setdata: Weather
    ) {
        textview.text = "${setdata.the_temp.toInt()}℃"
    }

    @BindingAdapter("humidity")
    @JvmStatic
    fun setHumidity(
            textview: AppCompatTextView,
            setdata: Weather
    ) {
        textview.text = "${setdata.humidity.toInt()}%"
    }
}