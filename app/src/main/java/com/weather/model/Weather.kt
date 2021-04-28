package com.weather.model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class Weather(
        var id: Long,
        var weather_state_name: String,
        var weather_state_abbr: String,
        var the_temp: Double,
        var humidity: Float,
) : Serializable