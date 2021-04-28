package com.weather.model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class Location(
    var consolidated_weather: ArrayList<Weather>,
    var title: String,
    var woeid: Int,
)  : Serializable