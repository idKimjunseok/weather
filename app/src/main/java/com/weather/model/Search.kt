package com.weather.model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class Search(
    var title: String,
    var location_type: String,
    var woeid: Int,
)  : Serializable